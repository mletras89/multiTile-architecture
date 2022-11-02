import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.Queue;

class Scheduler{
  private LinkedList<Action> scheduledActions;
  private Queue<Action> queueActions;
  private Map<Action,List<Transfer>> readTransfers;
  private Map<Action,List<Transfer>> writeTransfers;

  private double lastEventinProcessor;
  private int numberIterations;
  private int runIterations;
  private String name;

  Scheduler(String name){
    this.scheduledActions = new LinkedList<Action>();
    this.queueActions = new LinkedList<>();
    this.readTransfers = new HashMap<>();
    this.writeTransfers = new HashMap<>();
    this.lastEventinProcessor = 0.0;
    this.numberIterations = 1;
    this.runIterations = 0;
    this.name = name;
  }

  public void restartScheduler() {
    this.scheduledActions.clear();
    this.queueActions.clear();
    this.readTransfers.clear();
    this.writeTransfers.clear();
    this.lastEventinProcessor = 0.0;
    this.runIterations = 0;
  } 

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void cleanQueue(){
    this.queueActions.clear();
  }

  public void setNumberIterations(int numberIterations){
    this.numberIterations = numberIterations;
  }

  public int getNumberIterations(){
    return this.numberIterations;
  }

  public int getRunIterations(){
    return this.runIterations;
  }

  public void setScheduledActions(LinkedList<Action> scheduledActions) {
    this.scheduledActions = scheduledActions;
  }
  
  public LinkedList<Action> getScheduledActions(){
    return this.scheduledActions;
  }

  public void setQueueActions(Queue<Action> queueActions) {
    this.queueActions = queueActions;
  }
 
  public HashMap<Action,List<Transfer>> getReadTransfers(){
    return this.readTransfers;
  } 

  public double getTimeLastReadofActor(Actor actor){
    if(readTransfers.containsKey(actor)){
      double max = 0.0;
      for(Transfer transfer : readTransfers.get(actor)){
        if transfer.getDue_time() > max{
          max = transfer.getDue_time();
        }
      }
      return max;
    }
    return 0.0;
  }

  public double getTimeLastWriteofActor(Actor actor){
    if(writeTransfers.containsKey(actor)){
      double max = 0.0;
      for(Transfer transfer : writeTransfers.get(actor)){
        if transfer.getDue_time() > max{
          max = transfer.getDue_time();
        }
      }
      return max;
    }
    return 0.0;
  }

  public HashMap<Action,List<Transfer>> getWriteTransfers(){
    return this.writeTransfers;
  } 

  public Queue<Action> getQueueActions(){
    return this.queueActions;
  }

  public void setLastEventinProcessor(double lastEventinProcessor){
    this.lastEventinProcessor  = lastEventinProcessor;
  }

  public double getLastEventinProcessor(){
    return this.lastEventinProcessor;
  }

  public void insertAction(Action a){
    queueActions.add(a);
  }

  public void commitActionsinQueue(){
    // then commit all the schedulable Actions in the queue
    for(Action commitAction : this.queueActions){
      // proceed to schedule the Action
      double ActionTime = commitAction.getProcessing_time();
      double startTime = (commitAction.getStart_time() > this.lastEventinProcessor) ? commitAction.getStart_time() : this.lastEventinProcessor; 
      double endTime = startTime + ActionTime;
      // update now the commit Action
      commitAction.setStart_time(startTime);
      commitAction.setDue_time(endTime);
      // update the last event in processor
      this.lastEventinProcessor = endTime;
      // commit the Action
      this.scheduledActions.addLast(commitAction);
    }
  }

  public void commitReadsToCrossbar(){
    for(Action commitAction : this.queueActions){
      List<Transfer> reads = new ArrayList<>();
      for(Fifo fifo : commitAction.getInputFiFos()){
        int cons      = fifo.getProdRate();
        // I scheduled read of data by token reads
        for(int n = 0 ; n<cons;n++) {
          if(fifo.getMapping().getType() == Memory.MEMORY_TYPE.TILE_LOCAL_MEM ||
            (fifo.getMapping().getType() == Memory.MEMORY_TYPE.LOCAL_MEM &&
            fifo.getMapping().getEmbeddedToProccesor() != commitAction.getMapping())){
            // then the read must be scheduled in the crossbar
            Transfer readTransfer = new Transfer(commitAction,fifo,this.lastEventinProcessor,Transfer.TRANSFER_TYPE.READ);
            reads.add(readTransfer);
        }
      }
      readTransfers.put(commitAction,reads);
    }
  }
  
  public void commitWritesToCrossbar(){
    for(Action commitAction : this.queuActions){
      List<Transfer> writes = new ArrayList<>();
      for(Fifo fifo : commitAction.getOutputFifos()){
        int prod    = fifo.getProdRate();
        for(int n=0; n<prod; n++){
          if(fifo.getMapping().getType() == Memory.MEMORY_TYPE.TILE_LOCAL_MEM ||
            (fifo.getMapping().getType() == Memory.MEMORY_TYPE.LOCAL_MEM &&
            fifo.getMapping().getEmbeddedToProcessor() != commitAction.getMapping())){
              // Then the write must be scheduled in the crossbar
              Transfer writeTransfer = new Transfer(commitAction,fifo,this.lastEventinProcessor,Transfer.TRANSFER_TYPE.WRITE);
              writes.add(writeTransfer);
            }
        }
      }
      writeTransfers.put(commitAction,writes);
    }
  }

  public void fireCommitedActions(Map<Integer,Fifo> fifos){
    // update the fifos after the firing of the action
    int elementsinQueue = this.queueActions.size();
    for(int i=0;i<elementsinQueue;i++){
      Action firingAction = this.queueActions.remove();
      firingAction.getActor().fire(fifos);
      if (firingAction.getActor().getName().contains("sink") == true){
       this.runIterations++; 
      }
    }
  }

  // DUMPING the processor utilzation locally
  public void saveScheduleStats(String path) throws IOException{
    try{
        File memUtilStatics = new File(path+"/processor-utilization-"+this.getName()+".csv");
        if (memUtilStatics.createNewFile()) {
          System.out.println("File created: " + memUtilStatics.getName());
        } else {
          System.out.println("File already exists.");
        }
    }
    catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }

    FileWriter myWriter = new FileWriter(path+"/processor-utilization-"+this.getName()+".csv"); 
    myWriter.write("Job\tStart\tFinish\tResource\n");
    saveScheduleStats(myWriter);

    myWriter.close();
  }

  public void saveScheduleStats(FileWriter myWriter) throws IOException{
    for(Action a : scheduledActions){
      myWriter.write(a.getActor().getName()+"\t"+a.getStart_time()+"\t"+a.getDue_time()+"\t"+this.getName()+"\n");
    }
  }

}
