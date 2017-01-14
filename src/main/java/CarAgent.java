import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

/**
 * Created by nuka3 on 12/8/16.
 */
public abstract class CarAgent extends Agent {
  protected void setup() {
    Object[] args = getArguments();
    destiny = (int) args[0];
    map = (RoadMap) args[1];

    addBehaviour(new CyclicBehaviour(this) {
      @Override
      //Get messege from road
      public void action() {
        ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (msg != null) {
          int roadaddr = Integer.parseInt(msg.getContent());
          ACLMessage out = new ACLMessage(ACLMessage.PROPOSE);
          if (roadaddr == destiny) {
            out.setContent("-1");
            out.addReceiver(msg.getSender());
            send(out);
          }else{
            out.setContent(Integer.toString(choseWay(roadaddr)));
            out.addReceiver(msg.getSender());
            send(out);
          }
        }else{
          block();
        }
      }
    });
  }

  public abstract int choseWay(int node);


  protected RoadMap map;
  protected int destiny;


}
