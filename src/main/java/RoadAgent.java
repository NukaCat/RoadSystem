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

    view = City.instance().getRoadView(inNode, outNode);

    //process last car in queue
    addBehaviour(new TickerBehaviour(this, delay) {
      @Override
      protected void onTick() {
        if (cars.size() > 0) {
          if(b == null || b.done()) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent(outNode + "");
            AID id = new AID(cars.peek(), true);
            msg.addReceiver(id);
            send(msg);
            b = new ListenCarBehaviour(getAgent());
            getAgent().addBehaviour(b);
          }else{
            System.out.println("Broken Car at " + getName());
            cars.remove();
            view.removeCar();
            b.stop();
          }
        }
      }
      ListenCarBehaviour b;
    });

    //putCar
    addBehaviour(new CyclicBehaviour(this) {
      @Override
      public void action() {
        ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (msg != null) {
          System.out.println(msg.getContent() + " added to " + getName());
          cars.add(msg.getContent());
          view.addCar(msg.getContent().hashCode());
        } else {
          block();
        }
      }
    });

    //getInfoaboutRoad
    addBehaviour(new CyclicBehaviour(this) {
      @Override
      public void action() {
        ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        if (msg != null) {
          ACLMessage out = new ACLMessage(ACLMessage.INFORM);
          out.setContent(Integer.toString(cars.size()*delay));
          out.addReceiver(addresses.get(msg.getSender()));
          send(out);
        } else {
          block();
        }
      }
    });

  }

  class ListenCarBehaviour extends Behaviour {
    public ListenCarBehaviour(Agent agent){
      super(agent);
      done = false;
    }

    @Override
    public void action() {
      ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
      if (msg != null && !done) {
        int roadaddr = Integer.parseInt(msg.getContent());
        if (cars.size() > 0 && cars.peek().equals(msg.getSender().getName())) {
          if (roadaddr == -1) {
            System.out.println("Car " + cars.peek() + " stopped");
            cars.remove();
            view.removeCar();
            done = true;
          } else {
            if (addresses.containsKey(roadaddr)) {
              ACLMessage out = new ACLMessage(ACLMessage.INFORM);
              out.setContent(cars.remove());
              out.addReceiver(addresses.get(roadaddr));
              send(out);
              view.removeCar();
              done = true;
            } else {
              System.out.println("Car " + cars.peek() + " proposed wrong address");
            }
          }
        }
      } else {
        block();
      }
    }

    public void stop(){
      done = true;
    }

    @Override
    public boolean done() {
      return done;
    }

    private boolean done;
  }


  private int delay;
  private int inNode;
  private int outNode;
  private RoadView view;
  private Queue<String> cars; //cars on road
  Map<Integer, AID> addresses; //Output roads
  RoadMap roadMap;

}