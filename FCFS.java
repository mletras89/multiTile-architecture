import java.util.List;
import java.util.Map;

class FCFS extends Scheduler implements Schedule{
  
  FCFS(String name){
    super(name);
  }
  
  public void getSchedulableActors(List<Actor> actors,Map<Integer,Fifo> fifos){
    // from the list of actors in Processor, check which of them can fire
    this.cleanQueue();
   
    for(Actor actor: actors){
      if(actor.canFire(fifos)){
        //System.out.println("Fireable: "+actor.getName());
        Action action = new Action(actor);
        this.insertAction(action);
      }
    }
  }

  public void runSchedule(List<Actor> actors,Map<Integer,Fifo> fifos){
    while(this.getRunIterations() < this.getNumberIterations()){
    //for(int i=0;i<10;i++){
      // First enqueue the fireable actors!
      this.getSchedulableActors(actors,fifos);
      this.commitActionsinQueue();
      this.fireCommitedActions(fifos);
    }
  }

}
