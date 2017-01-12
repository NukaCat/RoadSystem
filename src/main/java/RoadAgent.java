import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
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

    //putCar
    addBehaviour(new CyclicBehaviour(this) {
      @Override
      public void action() {
        ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (msg != null) {
          System.out.println(msg.getContent() + " added to " + getName());
          cars.add(msg.getContent());
        }else {
          block();
        }
      }
    });

    addBehaviour(new CyclicBehaviour(this) {
      @Override
      public void action() {
        ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
        if (msg != null) {
          if(cars.peek().equals(msg.getSender().getName())) {
            System.out.println("Car Stopped");
            cars.remove();
          }
        }else {
          block();
        }
      }
    });


    //process car
    addBehaviour(new TickerBehaviour(this, delay) {
      @Override
      protected void onTick() {
        if(cars.size() > 0) {;
          ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
          msg.setContent(outNode + "");
          AID id = new AID(cars.peek(), true);
          msg.addReceiver(id);
          send(msg);
        }
      }
    });

    addBehaviour(new CyclicBehaviour(this) {
      @Override
      public void action() {
        ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        if (msg != null) {
          int roadaddr = Integer.parseInt(msg.getContent());
          if(cars.peek().equals(msg.getSender().getName())){
            if(addresses.containsKey(roadaddr)){
              ACLMessage out = new ACLMessage(ACLMessage.INFORM);
              out.setContent(cars.remove());;
              out.addReceiver(addresses.get(roadaddr));
              send(out);
            }else{
              System.out.println("ERRRRORRRR WRONG ADDRESS " + roadaddr + " in " + getName());
            }
          }
        }else {
          block();
        }
      }
    });
  }

  private int delay;
  private int inNode;
  private int outNode;
  private Queue<String> cars;
  Map<Integer, AID> addresses;
  RoadMap roadMap;
}
