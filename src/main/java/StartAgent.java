import com.sun.org.apache.bcel.internal.generic.NEW;
import jade.core.AID;
import jade.core.Agent;

import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class StartAgent extends Agent {
  protected void setup() {
    AgentContainer c = getContainerController();
    RoadMap roadMap = City.instance().getMap();
    int n = roadMap.size();
    YellowPages pages = new YellowPages(n);
    ArrayList<AgentController> roads = new ArrayList<>();
    //road addresses from i node
    Map<Integer, AID>[] addrfrom = new Map[n];
    for(int i = 0; i < n; i++){
      addrfrom[i] = new HashMap<>();
    }

    try {
      Random random = new Random();
      for (int i = 0; i < roadMap.size(); i++) {
        for (int j = 0; j < roadMap.size(); j++) {
          if (roadMap.get(i, j) > 0) {

            Object s[] = {addrfrom[j], roadMap, i, j, roadMap.get(i, j)*(1000 + random.nextInt()%500),
                    Math.max(1, (int)(City.instance().getDist(i,j)*70))};
            AgentController a = c.createNewAgent("Road(" + i + "," + j + ")", "RoadAgent", s);
            AID aid = new AID(a.getName(), true);
            addrfrom[i].put(j, aid);
            pages.setAID(aid, i, j);
            roads.add(a);
          }
        }
      }
      for(AgentController r: roads){
        r.start();
      }
    } catch (StaleProxyException e) {
      e.printStackTrace();
    }

    int carCount = 500;

    //Creatimg cars
    Random r = new Random();
    int border = City.instance().getBorder(0.5);
    for (int i = 0; i < carCount; i++) {
      try {
        Object[] arg = {Math.abs(r.nextInt()%(n - border)), roadMap, pages};
        AgentController a = c.createNewAgent("Car" + i, "CleverCarAgent", arg);
        a.start();
        ACLMessage out = new ACLMessage(ACLMessage.INFORM);
        out.setContent(a.getName());
        int k = Math.abs(r.nextInt()%(n - border) + border);
        int p = roadMap.getFirstRoadFrom(k);
        AID res = addrfrom[k].get(p);
        out.addReceiver(res);
        send(out);
      } catch (StaleProxyException e) {
        e.printStackTrace();
      }
    }
  }
}
