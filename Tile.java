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
     This class describes an Tile in the architecture. Each Tile contains:
        - processors: a list of processors in the tile.
        - crossbar: crossbar that communicate the processors
        - tileLocalMemory: is the memory local to this tile
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
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.Queue;

public class Tile{
  private int id;
  private String name;
  private int numberProcessors;
  private List<Processor> processors;
  private Crossbar crossbar;
  private TileLocalMemory tileLocalMemory;
  private int totalIterations;
  
  public Tile(){
    this.id = 1;
    this.name = "Tile1";
    this.numberProcessors = 1;
    this.processors = new ArrayList<>();
    for(int i=0; i<this.numberProcessors;i++){
      Processor processor = new Processor(i,"Processor"+i);
      processors.add(processor);
      crossbar = new Crossbar(1,"crossbar_"+this.name, 1,2);
    }
    tileLocalMemory = new TileLocalMemory(1,"TileLocalMemory_"+this.name);
    this.totalIterations = 1;
  }

  public Tile(int id,String name,int numberProcessors,double crossbarBw,int crossbarChannels){
    this.id = id;
    this.name = name;
    this.numberProcessors = numberProcessors;
    this.processors = new ArrayList<>();
    //System.out.println("Here!");
    for(int i=0; i<this.numberProcessors;i++){
      Processor processor = new Processor(i,this.name+"_Processor"+i);
      processors.add(processor);
      crossbar = new Crossbar(1,"crossbar_"+this.name, crossbarBw,crossbarChannels);
    }
    tileLocalMemory = new TileLocalMemory(1,"TileLocalMemory_"+this.name);
    this.totalIterations = 1;
  }

  public boolean equals(Tile tile){
    return this.getId() == tile.getId() && this.getName().equals(tile.getName());
  }

  public Crossbar getCrossbar(){
    return this.crossbar;
  }

  public void runTileActors(List<Actor> actors, Map<Integer,Fifo> fifoMap){ 
    this.resetTile();
    int runIterations = 0;
    while(runIterations < this.totalIterations){
      // first collect all the schedulable actors per processor
      for(int i =0 ; i < this.numberProcessors; i++){
        ((FCFS)processors.get(i).getScheduler()).getSchedulableActors(actors,fifoMap);
      } 
      // proceed to schedule each of the actions per processor
      for(int i=0; i< this.numberProcessors; i++){
        Queue<Action> actions = processors.get(i).getScheduler().getQueueActions();
        for(Action action : actions){
          // first schedule the reads
          processors.get(i).getScheduler().commitReadsToCrossbar(action,fifoMap);
          Map<Actor,List<Transfer>> readTransfers = processors.get(i).getScheduler().getReadTransfers();
          crossbar.cleanQueueTransfers();
          for(Map.Entry<Actor,List<Transfer>> entry : readTransfers.entrySet()){
            crossbar.insertTransfers(entry.getValue());
          }
          //commit the read transfers
          crossbar.commitTransfersinQueue();
          // update the read transfers of each processor with the correct due time
          Map<Actor,List<Transfer>> processorReadTransfers = crossbar.getScheduledReadTransfers(processors.get(i));

          // commit the action in the processor
          processors.get(i).getScheduler().setReadTransfers(processorReadTransfers);
          processors.get(i).getScheduler().commitSingleAction(action); // modificar este
          
          // finally, schedule the write of tokens 
          ((FCFS)processors.get(i).getScheduler()).commitWritesToCrossbar(action);
          // put writing transfers to crossbar
          // get write transfers from the scheduler
          Map<Actor,List<Transfer>> writeTransfers = processors.get(i).getScheduler().getWriteTransfers();
          for(Map.Entry<Actor,List<Transfer>> entry: writeTransfers.entrySet()){
            crossbar.insertTransfers(entry.getValue());
          }
          // commit write transfers in the crossbar
          crossbar.commitTransfersinQueue();
          // update the write transfers of each processor with the correct start and due time
          Map<Actor,List<Transfer>> processorWriteTransfers = crossbar.getScheduledWriteTransfers(processors.get(i));
          processors.get(i).getScheduler().setWriteTransfers(processorWriteTransfers);
          processors.get(i).getScheduler().produceTokensinFifo(action,fifoMap);

          processors.get(i).getScheduler().getReadTransfers().clear();
          processors.get(i).getScheduler().getWriteTransfers().clear();       
        }
      }
      //fire the actions, updating fifos
      for(int i =0 ; i < numberProcessors; i++){
        processors.get(i).getScheduler().fireCommitedActions(fifoMap);
      } 
      runIterations = this.getRunIterations();
    }
  }

  public void runTile(List<Actor> actors, Map<Integer,Fifo> fifoMap){
    this.resetTile();
    int runIterations = 0;
    while(runIterations < this.totalIterations){
      //System.out.println("ITERATION:");
      // first collect all the schedulable actors in each processor
      for(int i =0 ; i < numberProcessors; i++){
         ((FCFS)processors.get(i).getScheduler()).getSchedulableActors(actors,fifoMap);
      } 
      // then proceed to schedule the read transfers of each processor in the Tile
      for(int i =0 ; i < numberProcessors; i++){
        ((FCFS)processors.get(i).getScheduler()).commitReadsToCrossbar(fifoMap);
      } 
      crossbar.cleanQueueTransfers();
      for(int i=0; i< this.numberProcessors; i++){
        // get read actions from the scheduler
        Map<Actor,List<Transfer>> readTransfers = processors.get(i).getScheduler().getReadTransfers();
        for(Map.Entry<Actor,List<Transfer>> entry : readTransfers.entrySet()){
          crossbar.insertTransfers(entry.getValue());
        }
      }
      //commit the read transfers
      crossbar.commitTransfersinQueue();
      // scheduling reads and actions
      for(int i =0 ; i < numberProcessors; i++){
        // update the read transfers of each processor with the correct due time
	Map<Actor,List<Transfer>> processorReadTransfers = crossbar.getScheduledReadTransfers(processors.get(i));
	// debbuging
	/*for(Map.Entry<Actor,List<Transfer>> entry : processorReadTransfers.entrySet() ){
          for(Transfer t : entry.getValue()){
            System.out.println("\t\tScheduling reading from "+t.getFifo().getName()+" to "+t.getActor().getName()+" start time "+t.getStart_time()+" due time "+t.getDue_time());
          }
	}*/
	processors.get(i).getScheduler().setReadTransfers(processorReadTransfers);
        processors.get(i).getScheduler().commitActionsinQueue();
      }
      // scheduling writing transferss
      for(int i=0;i < numberProcessors; i++){
        ((FCFS)processors.get(i).getScheduler()).commitWritesToCrossbar();
      }
      // put writing transfers to crossbar
      for(int i=0; i<this.numberProcessors; i++){
        // get write transfers from the scheduler
        Map<Actor,List<Transfer>> writeTransfers = processors.get(i).getScheduler().getWriteTransfers();
        for(Map.Entry<Actor,List<Transfer>> entry: writeTransfers.entrySet()){
          crossbar.insertTransfers(entry.getValue());
        }
      }
      // commit write transfers in the crossbar
      crossbar.commitTransfersinQueue();
      // commit the writes in each processor
      for(int i=0;i<this.numberProcessors;i++){
        // update the write transfers of each processor with the correct start and due time
        Map<Actor,List<Transfer>> processorWriteTransfers = crossbar.getScheduledWriteTransfers(processors.get(i));
        processors.get(i).getScheduler().setWriteTransfers(processorWriteTransfers);
        processors.get(i).getScheduler().produceTokensinFifo(fifoMap);
      }

      // fire the actions, updating fifos
      for(int i =0 ; i < numberProcessors; i++){
        processors.get(i).getScheduler().fireCommitedActions(fifoMap);
      } 
      runIterations = this.getRunIterations();
      for(int i=0; i< this.numberProcessors; i++){ 
        // cleaning read and write transfer list in each processor
        processors.get(i).getScheduler().getReadTransfers().clear();
        processors.get(i).getScheduler().getWriteTransfers().clear();       
      }
    }
    
  }
  
  public void setTotalIterations(int totalIterations){
    this.totalIterations = totalIterations;
  }
  
  public TileLocalMemory getTileLocalMemory(){
    return this.tileLocalMemory;
  }

  public List<Processor> getProcessors(){
    return this.processors;
  }

  public int getRunIterations(){
    int max = 0 ;
    for(int i=0;i<numberProcessors;i++){
      if (max < processors.get(i).getRunIterations())
        max = processors.get(i).getRunIterations();
    }
    return max;
  }

  public void resetTile(){
    // first reset the processors
    for(int i=0;i<numberProcessors;i++){
      processors.get(i).restartProcessor();
    }
    // refresh the tile local memory
    tileLocalMemory.resetMemoryUtilization();
    // restart the crossbar
    crossbar.restartCrossbar();
  }
  
  public String getName(){
    return this.name;
  }

  public void setName(String name){
    this.name = name;
  }

  public int getId(){
    return this.id;
  }

  public void setId(int id){
    this.id = id;
  }

}
