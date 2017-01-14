/**
 * Created by nuka3 on 1/14/17.
 */
public class ShortWayCarAgent extends CarAgent {
  @Override
  public int choseWay(int node) {
    if( way == null){
      makeWay(node);
    }
    int res = way.getDir(node);
    if(res != -1){
      return res;
    }
    makeWay(node);
    if(res == -1){
      System.out.println("Error in finding way in car " + getName());
    }
    return 1;
  }

  @Override
  public void start(int inNode, int outNode) {

  }

  private void makeWay(int node){
    //deixtra
    int n = map.size();
    boolean vistednodes[] = new boolean[n];
    int dist[] = new int[n];
    int ways[] = new int[n];
    for (int i = 0; i < n; i++) {
      dist[i] = Integer.MAX_VALUE;
      ways[i] = node;
      vistednodes[i] = false;
    }
    dist[node] = 0;

    for (int i = 0; i < n; i++) {
      int min = Integer.MAX_VALUE;
      int mini = 0;
      //chose min node
      for (int j = 0; j < n; j++) {
        if (!vistednodes[j] && dist[j] < min) {
          mini = j;
          min = dist[i];
        }
      }
      vistednodes[mini] = true;
      //rebuild distances from i
      for (int j = 0; j < n; j++) {
        int d = map.get(mini, j);
        if (d > 0) {
          if (d + dist[mini] < dist[j]) {
            dist[j] = d + dist[mini];
            ways[j] = mini;
          }
        }
      }
    }
    way = new Way(ways, destiny, node);
  }

  Way way;
  class Way{
    public Way(int[] a, int destiny, int start){
      int n = 0;
      for (int i = destiny; i != start; i = a[i]){
        n++;
      }
      n++;
      way = new int[n];
      for (int i = destiny; i != start; i = a[i]){
        n--;
        way[n] = i;
      }
      way[0] = start;
    }

    int getDir(int node){
      if(way[place] == node){
        place++;
        return way[place];
      }
      for(int i = 0; i < way.length; i++){
        if(way[i] == node){
          place = i + 1;
          return node;
        }
      }
      return -1;
    }
    int place;
    int[] way;
  }
}
