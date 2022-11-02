import java.util.*;

public class Actor {
  private int id;
  private String name;
  private int priority;    
  
  private int inputs;
  private int outputs;
  
  private Vector<Fifo> inputFifos;
  private Vector<Fifo> outputFifos;


  private HashMap<Integer,Integer> inputMergedFifos;  // the key is the fifo ID, and the value the merged fifo id
  private HashMap<Integer,ArrayList<Integer>> outputMergedFifos;
  
  private Processor mapping;  // mapping to the Processor object
  private double executionTime;  // the execution time is associated with the mapping
  
  private TYPE type;
  private boolean mergeBroadcast = false;
  
  public static enum TYPE {
      ACTOR,
      BROADCAST
    }
    
  public Actor(int id,
               String name,
               int priority,
               int inputs, 
               int outputs, 
               double executionTime, 
               Processor mapping){
    this.setId(id);
    this.setName(name);
    this.setPriority(priority);
    this.setInputs(inputs);
    this.setOutputs(outputs);

    this.setInputMergedFiFos(new HashMap<Integer,Integer>());
    this.setOutputMergedFifos(new HashMap<Integer,ArrayList<Integer>>()); 

    if (name.contains("broadcast"))
      this.setType(TYPE.BROADCAST);
    else
      this.setType(TYPE.ACTOR);

    this.mergeBroadcast = false;

    this.setMapping(mapping);
    this.setExecutionTime(executionTime);

    this.inputFifos  = new Vector<Fifo>();   
    this.outputFifos = new Vector<Fifo>();    
  }
    
  public Actor(Actor another){
    this.setId(another.getId());
    this.setName(another.getName());
    this.setPriority(another.getPriority());
    this.setInputs(another.getInputs());
    this.setOutputs(another.getOutputs());
    this.inputFifos    = another.getInputFifos();
    this.outputFifos   = another.getOutputFifos();

    this.setInputMergedFiFos(another.getInputMergedFiFos());   
    this.setOutputMergedFifos(another.getOutputMergedFifos());

    this.setType(another.getType());
    this.mergeBroadcast = another.isMergeBroadcast();
    
    this.setMapping(another.getMapping());
    this.setExecutionTime(another.getExecutionTime());

    this.inputFifos  = new Vector<Fifo>();   
    this.outputFifos = new Vector<Fifo>();    
  }
    
  public Actor(String name){
    this.setName(name);
    this.inputFifos  = new Vector<Fifo>();   
    this.outputFifos = new Vector<Fifo>();    
  }

  // method for checking if an actor can FIRE
  public boolean canFire(Map<Integer,Fifo> fifos){
    //System.out.println("Can fire "+this.name+" ?");

    for(Fifo fifo : this.outputFifos){
      Fifo selectedFifo = fifos.get(fifo.getId());
      if (!selectedFifo.fifoCanBeWritten())
        return false;
    }
    
    for(Fifo fifo : this.inputFifos){
      Fifo selectedFifo = fifos.get(fifo.getId());
      if(!selectedFifo.fifoCanBeRead())
        return false; 
    }
    return true;
  }
  
  // method that fires the actor
  public boolean fire(Map<Integer,Fifo> fifos){
    //System.out.println("Firing actor "+this.name);
    
    for(Fifo fifo : outputFifos){
      fifos.get(fifo.getId()).set_tokens(fifos.get(fifo.getId()).get_tokens()+fifos.get(fifo.getId()).getProdRate()); 
      assert (fifos.get(fifo.getId()).get_tokens()<=fifos.get(fifo.getId()).get_capacity()): "Something weird!";
    }

    for(Fifo fifo: inputFifos){
        fifos.get(fifo.getId()).set_tokens(fifos.get(fifo.getId()).get_tokens() - fifos.get(fifo.getId()).getConsRate());
        assert (fifos.get(fifo.getId()).get_tokens()>=0) :  "Second assert message";
    }
       
    return true;
  }

  public int getInputs() {
    return inputs;
  }
  
  public void setInputs(int inputs) {
    this.inputs = inputs;
  }
  
  public int getOutputs() {
    return outputs;
  }
  
  public void setOutputs(int outputs) {
    this.outputs = outputs;
  }
  
  public double getExecutionTime() {
    return executionTime;
  }
  
  public void setExecutionTime(double executionTime) {
    this.executionTime = executionTime;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getPriority() {
    return priority;
  }
  
  public void setPriority(int priority) {
    this.priority = priority;
  }

  public Processor getMapping() {
    return mapping;
  }
  
  public void setMapping(Processor mapping) {
    this.mapping = mapping;
  }
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public Vector<Fifo> getInputFifos(){
    return this.inputFifos;
  }
  public Vector<Fifo> getOutputFifos(){
    return this.outputFifos;
  }

  public void setInputFifos(Vector<Fifo> inputs){
    this.inputFifos =  inputs;
  }
  public void setOutputFifos(Vector<Fifo> outputs){
    this.outputFifos = outputs;
  }

  public TYPE getType() {
    return type;
  }
  
  public void setType(TYPE type) {
    this.type = type;
  }

  public boolean isMergeBroadcast() {
    return mergeBroadcast;
  }
  
  public void setMergeBroadcast(boolean mergeBroadcast) {
    this.mergeBroadcast = mergeBroadcast;
  }
  
  public HashMap<Integer,Integer> getInputMergedFiFos() {
    return inputMergedFifos;
  }
  
  public void setInputMergedFiFos(HashMap<Integer,Integer> inputMergedFifos) {
    this.inputMergedFifos = inputMergedFifos;
  }
  
  public HashMap<Integer,ArrayList<Integer>> getOutputMergedFifos() {
    return outputMergedFifos;
  }
  
  public void setOutputMergedFifos(HashMap<Integer,ArrayList<Integer>> outputMergedFifos) {
    this.outputMergedFifos = outputMergedFifos;
  }
  
  public int getNInputs() {
    return this.inputFifos.size();
  }

}
