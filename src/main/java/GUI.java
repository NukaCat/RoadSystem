import javax.swing.*;
import java.awt.*;


/**
 * Created by nuka3 on 1/13/17.
 */
public class GUI extends JFrame {
  public GUI(){
    super("Road Map");
    h = 700;
    w = 700;
    this.setBounds(100, 100, w, h);
    setVisible(true);
  }

  private void draw(Graphics g){
    Image buf = createImage(w, h);
    Graphics g2 = buf.getGraphics();
    g2.setFont(new Font("TimesRoman", Font.PLAIN, 10));
    g2.clearRect(0, 0, w, h);
    City.instance().draw(g2, w, h);
    g.drawImage(buf, 0, 0, this);
  }

  public void step(){
    Graphics g = getGraphics();
    draw(g);
  }

  private City city;
  private int h;
  private int w;
}
