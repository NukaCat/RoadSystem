import jade.core.AID;

import java.util.Random;

/**
 * Created by nuka3 on 12/13/16.
 */
public class RoadMap {
  public RoadMap(int n){
    map = new int[n][n];
    for(int i = 0; i < n; i++){
      for(int j = 0; j < n; j++){
        map[i][j] = -1;
      }
    }
  }
  public int size(){
    return map.length;
  }

  public int get(int i, int j){
    return map[i][j];
  }
  public void set(int i, int j, int d){
    if(i >= 0 && j >= 0 && i < map.length && j< map.length) {
      map[i][j] = d;
    }
  }

  public int getFirstRoadFrom(int i){
    for(int j = 0; j < size(); j++){
      if(map[i][j] > 0){
        return j;
      }
    }
    return -1;
  }

  int[][] map;
}
