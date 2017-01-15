import java.awt.*;

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
    first = (first + 1) % cars.length;
  }

  public void addCar(int hash){
    if( n < cars.length - 1) {
      n++;
      cars[(n - 1) % cars.length] = hash;
    }
  }

  public int carCount(){
    return n;
  }

  public void drawCars(Graphics g, int x, int y, double dx, double dy){
    double fx = x;
    double fy = y;
    int bufN = n;
    int bufF = first;
    for(int i = 0; i < bufN; i++){
      fx += dx;
      fy += dy;
      g.setColor(new Color(cars[(i + bufF)%cars.length] % 16777216));
      g.drawRect((int) fx - 1, (int)fy - 1, 2, 2);
    }
  }

  int first;
  int n;
  int[] cars;
}
