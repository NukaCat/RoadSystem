package Behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Created by nuka3 on 1/15/17.
 */
public abstract class StoppableCyclicBehaviour extends Behaviour {

  public StoppableCyclicBehaviour(){
    super();
    done = false;
  }

  public StoppableCyclicBehaviour(Agent agent){
    super(agent);
    done = false;
  }


  public void stop(){
    done = true;
  }

  public void stop(int x){
    onEnd = x;
    done = true;
  }

  public void reset(){
    super.reset();
    done = false;
  }

  @Override
  public abstract void action();

  @Override
  public boolean done() {
    return done;
  }

  public int onEnd(){
    return onEnd;
  }

  public int onEnd;
  private boolean done ;
}
