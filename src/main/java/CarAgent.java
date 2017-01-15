import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public abstract class CarAgent extends Agent {
  protected void setup() {
    Object[] args = getArguments();
    destiny = (int) args[0];
    map = (RoadMap) args[1];
    pages = (YellowPages) args[2];

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

    //new Road
    addBehaviour(new CyclicBehaviour() {
      @Override
      public void action() {
        ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF));
        if(msg != null) {
          String[] m = msg.getContent().split(" ");
          int i = Integer.parseInt(m[0]);
          int j = Integer.parseInt(m[1]);
          int time = Integer.parseInt(m[1]);
          start(i, j, time);
        }else{
          block();
        }
      }
    });
  }

  public abstract int choseWay(int node);
  public abstract void start(int inNode, int outNode, int start);

  protected RoadMap map;
  protected int destiny;
  protected YellowPages pages;

}
