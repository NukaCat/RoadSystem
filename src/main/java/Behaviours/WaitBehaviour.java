package Behaviours;

import jade.core.behaviours.Behaviour;

/**
 * Created by nuka3 on 1/15/17.
 */
public class WaitBehaviour extends Behaviour{
  public WaitBehaviour(long time){
    done = false;
    waitTime = time;
    endTime = System.currentTimeMillis() + time;
  }

  @Override
  public void action() {
    long time = System.currentTimeMillis();
    if(time > endTime){
      done = true;
    }else{
      block(endTime - time);
    }
  }

  public void reset(){
    done = false;
    endTime = System.currentTimeMillis() + waitTime;
  }

  @Override
  public boolean done() {
    return done;
  }
  boolean done;
  long waitTime;
  long endTime;
}
