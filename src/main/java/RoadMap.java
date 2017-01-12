import jade.core.AID;

import java.util.Random;

/**
 * Created by nuka3 on 12/13/16.
 */
public class RoadMap {
  public RoadMap(int n){
    map = new int[n][n];
    Random random = new Random();
    for(int i = 0; i < n; i++){
      for(int j = 0; j < n; j++){
        map[i][j] = (random.nextInt() % 250 + 250);
      }
    }
  }
  public int size(){
    return map.length;
  }

  public int get(int i, int j){
    return map[i][j];
  }
  int[][] map;
}
