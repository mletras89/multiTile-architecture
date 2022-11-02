
public class Action {
  private double start_time;
  private double due_time;
  private Actor actor;
  
  public Action(Actor actor) {
      this.setStart_time(0.0);
      this.setDue_time(0.0);
      this.actor = actor;
  }
  
  public Action(Action other) {
  	this.setStart_time(other.getStart_time());
  	this.setDue_time(other.getDue_time());
      this.setActor(other.getActor());
  }
  
  public double getStart_time() {
      return start_time;
  }
  
  public void setStart_time(double start_time) {
      this.start_time = start_time;
  }
  
  public double getDue_time() {
      return due_time;
  }
  
  public void setDue_time(double due_time) {
      this.due_time = due_time;
  }
  
  
  public double getProcessing_time() {
      return actor.getExecutionTime();
  }

  public Actor getActor(){
    return this.actor;
  }    

  public void setActor(Actor actor){
    this.actor = actor;
  }
   
}
