/*
--------------------------------------------------------------------------
 Copyright (c) 2022 Hardware-Software-Co-Design, Friedrich-
 Alexander-Universitaet Erlangen-Nuernberg (FAU), Germany. 
 All rights reserved.
 
 This code and any associated documentation is provided "as is"
 
 IN NO EVENT SHALL HARDWARE-SOFTWARE-CO-DESIGN, FRIEDRICH-ALEXANDER-
 UNIVERSITAET ERLANGEN-NUERNBERG (FAU) BE LIABLE TO ANY PARTY FOR DIRECT,
 INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 OF THE USE OF THIS CODE AND ITS DOCUMENTATION, EVEN IF HARDWARE-
 SOFTWARE-CO-DESIGN, FRIEDRICH-ALEXANDER-UNIVERSITAET ERLANGEN-NUERNBERG
 (FAU) HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. THE
 AFOREMENTIONED EXCLUSIONS OF LIABILITY DO NOT APPLY IN CASE OF INTENT
 BY HARDWARE-SOFTWARE-CO-DESIGN, FRIEDRICH-ALEXANDER-UNIVERSITAET
 ERLANGEN-NUERNBERG (FAU).
 
 HARDWARE-SOFTWARE-CO-DESIGN, FRIEDRICH-ALEXANDER-UNIVERSITAET ERLANGEN-
 NUERNBERG (FAU), SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT
 NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 FOR A PARTICULAR PURPOSE.
 
 THE CODE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND HARDWARE-
 SOFTWARE-CO-DESIGN, FRIEDRICH-ALEXANDER-UNIVERSITAET ERLANGEN-
 NUERNBERG (FAU) HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
 UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 -------------------------------------------------------------------------
 
  @author Martin Letras
  @date   02 November 2022
  @version 1.1
  @ brief
     -This class describes a crossbar that connects processors in a tile to 
      a tile local memory. 
     -numberofParallelChannels define the number of parallel transfers that
      can be scheduled
     -scheduledActions scheduled transfers in the crossbar
--------------------------------------------------------------------------
*/



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.Queue;

public class Crossbar{
  private int id;
  private String name;
  private Queue<Transfer> queueTransfers;
  private List<LinkedList<Transfer>> scheduledActions;
  private Map<Actor,List<Transfer>> scheduledReadTransfers;
  private Map<Actor,List<Transfer>> scheduledWriteTransfers;
  private List<Double> timeEachChannel;
  private int numberofParallelChannels;
  private double bandwidth;  // each crossbar has a bandwidht in Gbps

  final int GigabitPerSecondToBytePerSecond = 125000000;

  // initializing empty crossbar
  public Crossbar() {
    this.id = 0;
    this.name = "bus";
    this.queueTransfers = new LinkedList<>();
    this.numberofParallelChannels = 1; // as a regular bus
    this.scheduledActions = new ArrayList<>();
    this.timeEachChannel  = new ArrayList<>();
    LinkedList<Transfer> schedActions =  new LinkedList<Transfer>();
    this.scheduledReadTransfers = new HashMap<>();
    this.scheduledWriteTransfers = new HashMap<>();
    this.scheduledActions.add(schedActions);
    this.timeEachChannel.add(0.0);
    this.setBandwidth(16);
  }
   // cloning crossbar
  public Crossbar(Crossbar other) {
    this.name = other.getName();
    this.id   = other.getId();
    this.queueTransfers = new LinkedList<>(other.getQueueTransfers());
    this.scheduledActions = new ArrayList<>(other.getScheduledActions());
    this.setBandwidth(other.getBandwidth());
    this.scheduledReadTransfers = new HashMap<>();
    this.scheduledWriteTransfers = new HashMap<>();
  }
  // creating memory from given parameters
  public Crossbar(int id, String name, double bandwidth, int numberofParallelChannels){
    this.name = name;
    this.id   = id;
    this.numberofParallelChannels = numberofParallelChannels;
    this.queueTransfers = new LinkedList<>();
    this.scheduledActions = new ArrayList<>();
    this.timeEachChannel  = new ArrayList<>();
    for(int i = 0; i<numberofParallelChannels;i++){
      LinkedList<Transfer> schedActions =  new LinkedList<Transfer>();
      this.scheduledActions.add(schedActions);
      this.timeEachChannel.add(0.0);
    }
    this.bandwidth= bandwidth;
    this.scheduledReadTransfers = new HashMap<>();
    this.scheduledWriteTransfers = new HashMap<>();
  }

  public void restartCrossbar(){
    this.queueTransfers.clear();
    this.scheduledActions.clear();
    this.timeEachChannel.clear();
    for(int i = 0; i<numberofParallelChannels;i++){
      LinkedList<Transfer> schedActions =  new LinkedList<Transfer>();
      this.scheduledActions.add(schedActions);
      this.timeEachChannel.add(0.0);
    }
    this.scheduledReadTransfers.clear();
    this.scheduledWriteTransfers.clear();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Queue<Transfer> getQueueTransfers(){
    return this.queueTransfers;
  }

  public void setQueueTransfers(Queue<Transfer> queueTransfers ){
    this.queueTransfers = queueTransfers;
  }
  
  public void cleanQueueTransfers(){
    this.queueTransfers.clear();
    this.scheduledReadTransfers.clear();
    this.scheduledWriteTransfers.clear();
  }

  public List<LinkedList<Transfer>> getScheduledActions(){
    return this.scheduledActions;
  }

  public void setScheduledActions(List<LinkedList<Transfer>> scheduledAction){
    this.scheduledActions = scheduledActions;
  }

  public int getNumberofParallelChannels(){
    return numberofParallelChannels;
  }

  public void setNumberofParallelChannels(int numberofParallelChannels){
    this.numberofParallelChannels = numberofParallelChannels;
  }

  public double getBandwidth(){
    return bandwidth;
  }

  public void setBandwidth(double bandwidth){
    this.bandwidth = bandwidth;
  }

  public double calculateTransferTime(Transfer transfer){
    int numberofBytes = transfer.getBytes();
    double processingTime = ((( BytesToGigabytes(numberofBytes) / this.bandwidth))*1000000); // 8 bits in a byte, 100 000 to convert from secs to microseconds
    return processingTime;
  }
  
  double BytesToGigabytes(int bytes) {
    double ToKylo = bytes/1024;
    double ToMega = ToKylo/1024;
    double ToGiga = ToMega/1024;
    return ToGiga;
  }

  public Map<Actor,List<Transfer>> getScheduledReadTransfers(){
    return scheduledReadTransfers;
  }
  
  public Map<Actor,List<Transfer>> getScheduledReadTransfers(Processor processor){
    Map<Actor,List<Transfer>> processorTransfers = new HashMap<>();
    for(Map.Entry<Actor,List<Transfer>> entry : this.scheduledReadTransfers.entrySet()){
      if(entry.getKey().getMapping().equals(processor)){
	processorTransfers.put(entry.getKey(),entry.getValue());
      }
    }
    return processorTransfers;
  }

  public Map<Actor,List<Transfer>> getScheduledWriteTransfers(){
    return this.scheduledWriteTransfers;
  }

  public Map<Actor,List<Transfer>> getScheduledWriteTransfers(Processor processor){
    Map<Actor,List<Transfer>> processorTransfers = new HashMap<>();
    for(Map.Entry<Actor,List<Transfer>> entry : this.scheduledWriteTransfers.entrySet()){
      if(entry.getKey().getMapping().equals(processor)){
	processorTransfers.put(entry.getKey(),entry.getValue());
      }
    }
    return processorTransfers;
  }

// methods for managing the crossbar, the insertion in each channel

  public void insertTransfer(Transfer transfer) {
    queueTransfers.add(transfer);
  }

  public void insertTransfers(List<Transfer> transfers) {
    queueTransfers.addAll(transfers);
  }

  public void addScheduledTransfer(Transfer transfer){
    if(transfer.getType()==Transfer.TRANSFER_TYPE.READ){
      List<Transfer> transfers;
      if (scheduledReadTransfers.containsKey(transfer.getActor())){
        transfers = scheduledReadTransfers.get(transfer.getActor());
      }else{
        transfers = new ArrayList<>();
      } 
      transfers.add(transfer);
      scheduledReadTransfers.put(transfer.getActor(),transfers);
    }else{
      List<Transfer> transfers;
      if (scheduledWriteTransfers.containsKey(transfer.getActor())){
        transfers = scheduledWriteTransfers.get(transfer.getActor());
      }else{
        transfers = new ArrayList<>();
      } 
      transfers.add(transfer);
      scheduledWriteTransfers.put(transfer.getActor(),transfers);
    }
  }

  public void commitTransfersinQueue(){
    // then commit all the transfers in the Queue
    int elementsinQueue = queueTransfers.size();
    
    for(int i=0;i<elementsinQueue;i++){
      Transfer commitTransfer = queueTransfers.remove();
      // proceed to schedule the transfer
      int availChannelIndex = getAvailableChannel();
      double timeLastAction = this.timeEachChannel.get(availChannelIndex);
      double transferTime = this.calculateTransferTime(commitTransfer);
      double startTime = (commitTransfer.getStart_time() > timeLastAction) ? commitTransfer.getStart_time() : timeLastAction;
      double endTime  = startTime + transferTime;
      // update now the commit transfer
      commitTransfer.setStart_time(startTime);
      commitTransfer.setDue_time(endTime);
      // update the channel time 
      this.timeEachChannel.set(availChannelIndex,endTime);
      // commit transfer
      scheduledActions.get(availChannelIndex).addLast(commitTransfer);
      // then add the scheduled transfers accordingly, with the scheduled due time
      this.addScheduledTransfer(commitTransfer);
      int lis= 0;
      for(LinkedList<Transfer> l : scheduledActions){
        System.out.println("lista "+(i++));
        for(Transfer t : l){
          System.out.println("\tCrossbar: scheduled from "+t.getFifo().getName()+" to "+t.getActor().getName()+" start time "+t.getStart_time()+" due time "+t.getDue_time());
        }
      }
    }
  }
  
  public int getAvailableChannel(){
    int availChannelIndex = 0;
    int numberScheduledActions = Integer.MAX_VALUE;
    for (int i=0; i<this.numberofParallelChannels;i++){
      if (scheduledActions.get(i).size() < numberScheduledActions){
        availChannelIndex = i;
        numberScheduledActions  = scheduledActions.get(i).size(); 
      }
    }
    return availChannelIndex;
  }

  // DUMPING the crossbar utilzation locally
  public void saveCrossbarUtilizationStats(String path) throws IOException{
    try{
        File memUtilStatics = new File(path+"/crossbar-utilization-"+this.getName()+".csv");
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

    FileWriter myWriter = new FileWriter(path+"/crossbar-utilization-"+this.getName()+".csv"); 
    myWriter.write("Job\tStart\tFinish\tResource\n");
    saveCrossbarUtilizationStats(myWriter);

    myWriter.close();
  }

  public void saveCrossbarUtilizationStats(FileWriter myWriter) throws IOException{
    for(int i=0;i<scheduledActions.size();i++){
      for(Transfer transfer : scheduledActions.get(i)){
        String operation = "reading_crossbar";
        if (transfer.getType() == Transfer.TRANSFER_TYPE.WRITE) 
          operation = "writing_crossbar";
        myWriter.write(operation+"\t"+ transfer.getStart_time()+"\t"+transfer.getDue_time()+"\t"+this.getName()+"_"+i+"\n");
      }
    }
  }

}
