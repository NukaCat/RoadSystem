import jade.Boot;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    String[] s = {"-gui", "start:StartAgent"};
    Boot.main(s);
    GUI g = new GUI();
    while (true){
      g.step();
      Thread.sleep(200);
    }
  }
}
