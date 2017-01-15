import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashSet;
import java.util.Set;

public class CleverCarAgent extends ShortWayCarAgent {

  public void setup(){
    super.setup();
    addBehaviour(new CyclicBehaviour() {
      @Override
      public void action() {
        ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
        if(msg != null){
          String[] r = msg.getContent().split(" ");
          int i = Integer.parseInt(r[0]);
          int j = Integer.parseInt(r[1]);
          int weight = Integer.parseInt(r[2]);
          map.set(i, j, weight);
        }else{
          block();
        }
      }
    });
  }

  @Override
  public void start(int inNode, int outNode, int time) {
    if(time > 500) {
      complited = new HashSet<>();
      deepSearch(0, outNode);
      makeWay(outNode);
    }
  }

  private void deepSearch(int deepth, int node) {
    complited.add(node);
    for (int j = 0; j < map.size(); j++) {
      if (map.get(node, j) > 0 && !complited.contains(j)){
        AID receiver = pages.getRoad(node, j);
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(receiver);
        send(msg);
        if (deepth < searchDepth) {
          deepSearch(deepth + 1, j);
        }
      }
    }
  }

  Set<Integer> complited;
  private int searchDepth = 4;
}
