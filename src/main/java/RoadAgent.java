import Behaviours.MessageWaitBehaviour;
import Behaviours.WaitBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


public class RoadAgent extends Agent {
  protected void setup() {
    cars = new LinkedList<>();
    Object[] arg = getArguments();
    addresses = (Map<Integer, AID>) arg[0];
    roadMap = (RoadMap) arg[1];
    inNode = (Integer) arg[2];
    outNode = (Integer) arg[3];
    delay = (Integer) arg[4];
    capacity = (Integer) arg[5];

    lastCarDir = -1;
    lastRoadConversationId = -1;
    view = City.instance().getRoadView(inNode, outNode);

    FSMBehaviour fsm = new FSMBehaviour();

    //process last car in queue
    fsm.registerFirstState(new Behaviour(this) {
      @Override
      public void action() {
        if (cars.size() > 0) {
          ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
          msg.setContent(outNode + "");
          AID id = new AID(cars.peek(), true);
          msg.addReceiver(id);
          send(msg);
          onEnd = 1;
        } else {
          onEnd = 0;
        }
      }


      public int onEnd() {
        return onEnd;
      }

      @Override
      public boolean done() {
        return true;
      }

      private int onEnd = 1;
    }, "Send message to Car");

    //Wait for a car response
    fsm.registerState(new ListenCarBehaviour(this, delay / 2), "Wait for Car response");

    //send car to the road
    fsm.registerState(new OneShotBehaviour() {
      @Override
      public void action() {
        ACLMessage out = new ACLMessage(ACLMessage.INFORM);
        out.setContent(cars.peek());
        out.addReceiver(addresses.get(lastCarDir));
        lastRoadConversationId++;
        out.setConversationId(Integer.toString(lastRoadConversationId));
        send(out);
      }
    }, "Send Car");

    //wait for road response (Roads always response)
    fsm.registerState(new MessageWaitBehaviour(this, delay, null) {

      @Override
      public void accept(ACLMessage msg) {
        if(msg != null) {
          cars.remove();
          view.removeCar();
        }
      }

      public void reset(){
        super.reset();
        template = MessageTemplate.and(
                MessageTemplate.MatchConversationId(Integer.toString(lastRoadConversationId)),
                MessageTemplate.MatchContent("1")
        );
      }
    }, "Wait for Road response");

    //fsm.registerState(new WaitBehaviour(delay), "Wait");
    fsm.registerState(new WaitBehaviour(delay), "Wait2");

    fsm.registerTransition("Send message to Car", "Wait2", 0, new String[]{"Send message to Car"});
    fsm.registerTransition("Send message to Car", "Wait for Car response", 1, new String[]{"Send message to Car"});
    fsm.registerTransition("Wait for Car response", "Send Car", MessageWaitBehaviour.SUCCESS, new String[]{"Wait for Car response"});
    fsm.registerTransition("Wait for Car response", "Wait2", MessageWaitBehaviour.FAILURE, new String[]{"Wait for Car response"});

    fsm.registerDefaultTransition("Send Car", "Wait for Road response", new String[]{"Send Car", "Wait for Road response"});
    fsm.registerTransition("Wait for Road response", "Send Car", 0, new String[]{"Wait for Road response"});
    fsm.registerTransition("Wait for Road response", "Wait2", 1, new String[]{"Wait for Road response"});

    fsm.registerTransition("Wait2", "Send message to Car", 0, new String[]{"Wait2"});

    addBehaviour(fsm);

    //putCar
    addBehaviour(new CyclicBehaviour(this) {
      @Override
      public void action() {
        ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (msg != null) {
          ACLMessage answer = new ACLMessage(ACLMessage.AGREE);
          answer.setConversationId(msg.getConversationId());
          answer.addReceiver(msg.getSender());
          if (capacity > cars.size()) {
            //System.out.println(msg.getContent() + " added to " + getName());
            if (!msg.getContent().equals(lastAcceptedCar)) {
              lastAcceptedCar = msg.getContent();
              cars.add(msg.getContent());
              view.addCar(msg.getContent().hashCode());
            }
            answer.setContent("1");
          } else {
            answer.setContent("0");
          }
          send(answer);
        } else {
          block();
        }
      }

      private String lastAcceptedCar = "";
    });

    //getWaitTime
    addBehaviour(new CyclicBehaviour(this) {
      @Override
      public void action() {
        ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        if (msg != null) {
          ACLMessage out = new ACLMessage(ACLMessage.INFORM);
          out.setContent(Integer.toString(cars.size() * delay));
          out.addReceiver(addresses.get(msg.getSender()));
          send(out);
        } else {
          block();
        }
      }
    });

  }


  //Waits for car message and move a car
  class ListenCarBehaviour extends MessageWaitBehaviour {
    public ListenCarBehaviour(Agent agent, long time) {
      super(agent, time, MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
    }

    @Override
    public void accept(ACLMessage msg) {
      if (msg == null) {
        System.out.println("Broken Car at " + getName());
        cars.remove();
        view.removeCar();
        onDone = 0;
        return;
      }

      int roadaddr = Integer.parseInt(msg.getContent());
      if (cars.peek().equals(msg.getSender().getName())) {
        if (roadaddr == -1) {
          //System.out.println("Car " + cars.peek() + " stopped");
          cars.remove();
          view.removeCar();
          onDone = 0;
        } else {
          if (addresses.containsKey(roadaddr)) {
            lastCarDir = roadaddr;
            onDone = 1;
          } else {
            onDone = 0;
            cars.remove();
            view.removeCar();
            System.out.println("Car " + cars.peek() + " proposed wrong address");
          }
        }
      }
    }

    public int onEnd() {
      super.onEnd();
      return onDone;
    }

    private int onDone = 1;

  }


  int lastCarDir;
  int lastRoadConversationId;

  private int delay;
  private int capacity;
  private int inNode;
  private int outNode;
  private RoadView view;
  private Queue<String> cars; //cars on road
  Map<Integer, AID> addresses; //Output roads
  RoadMap roadMap;
}