import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

/**
 * Created by nuka3 on 12/8/16.
 */
public class CarAgent extends Agent {
  protected void setup() {
    Object[] args = getArguments();
    destiny = (int) args[0];

    addBehaviour(new CyclicBehaviour(this) {
      @Override
      public void action() {
        ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (msg != null) {
          int roadaddr = Integer.parseInt(msg.getContent());
          if (roadaddr == destiny) {
            ACLMessage out = new ACLMessage(ACLMessage.REFUSE);
            out.addReceiver(msg.getSender());
            send(out);
          }else{
            ACLMessage out = new ACLMessage(ACLMessage.PROPOSE);
            out.setContent(Math.abs((new Random()).nextInt() % 3) + "");
            out.addReceiver(msg.getSender());
            send(out);
          }
        }else{
          block();
        }
      }
    });
  }

  int destiny;
}
