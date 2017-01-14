import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.tools.sniffer.Message;

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
        if(cars.size() > 0) {
          ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
          msg.setContent(outNode + "");
          AID id = new AID(cars.peek(), true);
          msg.addReceiver(id);
          send(msg);
        }
      }
    });

    //listen car
    addBehaviour(new CyclicBehaviour(this) {
      @Override
      public void action() {
        ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        if (msg != null) {
          int roadaddr = Integer.parseInt(msg.getContent());
          if(cars.size() > 0 && cars.peek().equals(msg.getSender().getName())){
            if(roadaddr == -1){
              System.out.println("Car " + cars.peek() + "stopped");
              cars.remove();
              view.removeCar();
            }else {
              if (addresses.containsKey(roadaddr)) {
                ACLMessage out = new ACLMessage(ACLMessage.INFORM);
                out.setContent(cars.remove());
                out.addReceiver(addresses.get(roadaddr));
                send(out);
                view.removeCar();
              } else {
                System.out.println("Car " + cars.peek() + " proposed wrong address");
              }
            }
          }
        }else{
          block();
        }
      }
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
        }else {
          block();
        }
      }
    });

  }


  private int delay;
  private int inNode;
  private int outNode;
  private RoadView view;
  private Queue<String> cars; //cars on road
  Map<Integer, AID> addresses; //Output roads
  RoadMap roadMap;
}