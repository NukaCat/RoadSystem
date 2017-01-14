/**
 * Created by nuka3 on 1/14/17.
 */
public class RoadView {
  public RoadView(int size){
    cars = new int[size];
    for(int i = 0; i < size; i++){
      cars[i] = 0;
    }
    first = 0;
    n = 0;
  }

  public void removeCar(){
    if(n > 0) n--;
  }

  public void addCar(int hash){
    n++;
  }

  public int carCount(){
    return n;
  }

  int first;
  int n;
  int[] cars;
}
