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
    int n = 5;
    AgentContainer c = getContainerController();
    RoadMap roadMap = new RoadMap(n);
    ArrayList<AgentController> roads = new ArrayList<>();
    Map<Integer, AID>[] addrfrom = new Map[n];
    for(int i = 0; i < n; i++){
      addrfrom[i] = new HashMap<>();
    }

    try {
      for (int i = 0; i < roadMap.size(); i++) {
        for (int j = 0; j < roadMap.size(); j++) {
          if (roadMap.get(i, j) > 0) {
            Object s[] = {addrfrom[j], roadMap, i, j, roadMap.get(i, j)};
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

    int carCount = 10;

    Random r = new Random();
    for (int i = 0; i < carCount; i++) {
      try {
        Object[] arg = {Math.abs(r.nextInt()%n)};
        AgentController a = c.createNewAgent("Car" + i, "CarAgent", arg);
        a.start();
        ACLMessage out = new ACLMessage(ACLMessage.INFORM);
        out.setContent(a.getName());
        AID res = addrfrom[Math.abs(r.nextInt()%n)].get(r.nextInt()%n);
        out.addReceiver(res);
        send(out);
      } catch (StaleProxyException e) {
        e.printStackTrace();
      }
    }
  }
}
