import jade.core.AID;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nuka3 on 1/15/17.
 */
public class YellowPages {
  public YellowPages(int size){
    this.size = size;
    roads = new HashMap<>();
  }

  public void setAID(AID aid, int i, int j){
    roads.put(i*size + j, aid);
  }

  public AID getRoad(int i, int j) {
    return roads.get(i * size + j);
  }

  private Map<Integer, AID> roads;
  private int size = 0;

}
