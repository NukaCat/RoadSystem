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

/**
 * Created by nuka3 on 12/8/16.
 */
public class StartAgent extends Agent {
  protected void setup() {
    AgentContainer c = getContainerController();
    RoadMap roadMap = City.instance().getMap();
    int n = roadMap.size();
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
            Object s[] = {addrfrom[j], roadMap, i, j, roadMap.get(i, j)*(1000 + random.nextInt()%500), 10};
            AgentController a = c.createNewAgent("Road(" + i + "," + j + ")", "RoadAgent", s);
            addrfrom[i].put(j, new AID(a.getName(), true));
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
        Object[] arg = {Math.abs(r.nextInt()%(n - border)), roadMap};
        AgentController a = c.createNewAgent("Car" + i, "ShortWayCarAgent", arg);
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
