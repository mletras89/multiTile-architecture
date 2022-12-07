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
     This class describes a Tile in the architecture. Each Tile contains:
        - processors: a list of processors in the tile.
        - crossbar: crossbar that communicate the processors
        - tileLocalMemory: is the memory local to this tile
--------------------------------------------------------------------------
*/
package src.multitile.architecture;

import src.multitile.application.Actor;
import src.multitile.application.Fifo;
import src.multitile.application.Application;

import src.multitile.FCFS;
import src.multitile.Action;
import src.multitile.Transfer;
import src.multitile.SchedulerManagement;

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
import java.util.*;

public class Tile{
  private int id;
  private String name;
  private int numberProcessors;
  // the key is the id
  private HashMap<Integer,Processor> processors;
  private Crossbar crossbar;
  private TileLocalMemory tileLocalMemory;
  private int totalIterations;
  
  public Tile(){
    this.id = ArchitectureManagement.getTileId();
    this.name = "Tile1";
    this.numberProcessors = 1;
    this.processors = new HashMap<>();
    for(int i=0; i<this.numberProcessors;i++){
      Processor processor = new Processor("Processor"+i);
      processor.setOwnerTile(this);
      processors.put(processor.getId(),processor);
    }

    for(HashMap.Entry<Integer,Processor> e: processors.entrySet()){
      // connecting local memory to processor
      e.getValue().getLocalMemory().setEmbeddedToProcessor(e.getValue());
    }
    crossbar = new Crossbar("crossbar_"+this.name, 1,2);
    tileLocalMemory = new TileLocalMemory("TileLocalMemory_"+this.name);
    this.totalIterations = 1;
  }

  public Tile(String name,int numberProcessors,double crossbarBw,int crossbarChannels){
    this.id = ArchitectureManagement.getTileId();
    this.name = name;
    this.numberProcessors = numberProcessors;
    this.processors = new HashMap<>();
    //System.out.println("Here!");
    for(int i=0; i<this.numberProcessors;i++){
      Processor processor = new Processor(this.name+"_Processor"+i);
      processor.setOwnerTile(this);
      processors.put(processor.getId(),processor);
    }
    for(HashMap.Entry<Integer,Processor> e: processors.entrySet()){
      // connecting local memory to processor     
      e.getValue().getLocalMemory().setEmbeddedToProcessor(e.getValue());     
    }
    crossbar = new Crossbar("crossbar_"+this.name, crossbarBw,crossbarChannels);
    tileLocalMemory = new TileLocalMemory("TileLocalMemory_"+this.name);
    this.totalIterations = 1;
  }

  public void setName(String name){
    this.name = name;
    crossbar.setName("crossbar_"+this.name);
    int i=0;
    for(HashMap.Entry<Integer,Processor> e: processors.entrySet()){
      e.getValue().getScheduler().setName(this.name+"_Processor"+(i++));  
    }
    tileLocalMemory.setName("TileLocalMemory_"+this.name);
  }

  public boolean equals(Tile tile){
    return this.getId() == tile.getId() && this.getName().equals(tile.getName());
  }

  public Crossbar getCrossbar(){
    return this.crossbar;
  }
/*
  public void runTileActors(Application application){ 
    List<Actor> actors = application.getListActors(); 
    Map<Integer,Fifo> fifoMap = application.getFifos();
    this.resetTile();
    int runIterations = 0;
    List<Transfer> transfersToMemory = new ArrayList<>();
    while(runIterations < this.totalIterations){
      // first collect all the schedulable actors per processor
      //application.printFifosState();
      for(int i =0 ; i < this.numberProcessors; i++){
        ((FCFS)processors.get(i).getScheduler()).getSchedulableActors(actors,fifoMap);
	//System.out.println("Processor:"+processors.get(i).getName());
	//((FCFS)processors.get(i).getScheduler()).printSchedulableActors();
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
          // update the last event in processor, taking into the account the processorWriteTransfers
          processors.get(i).getScheduler().updateLastEventAfterWrite(action);
          // insert the time of the produced tokens by acton into the correspondent fifos
          processors.get(i).getScheduler().produceTokensinFifo(action,fifoMap);
          
          // managing the tracking of the memories
          processors.get(i).getScheduler().setTransfersToMemory();
          transfersToMemory.addAll(processors.get(i).getScheduler().getTransfersToMemory());

          processors.get(i).getScheduler().getReadTransfers().clear();
          processors.get(i).getScheduler().getWriteTransfers().clear();       
        }
      }
      //fire the actions, updating fifos
      for(int i =0 ; i < this.numberProcessors; i++){
        processors.get(i).getScheduler().fireCommitedActions(fifoMap);
        // update the memories
        // clean the transfers to memories
        processors.get(i).getScheduler().getTransfersToMemory().clear();       
      } 
      // commit the reads/writes to memory
      SchedulerManagement.sort(transfersToMemory);
      for(Transfer t : transfersToMemory){
        if(t.getType() == Transfer.TRANSFER_TYPE.READ)
          t.getFifo().fifoReadFromMemory(t);
        else
          t.getFifo().fifoWriteToMemory(t);

      }
      transfersToMemory.clear();
      runIterations = this.getRunIterations();
    }
  }*/
/*
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
	}*//*
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
  */
  
  public void setTotalIterations(int totalIterations){
    this.totalIterations = totalIterations;
  }
  
  public TileLocalMemory getTileLocalMemory(){
    return this.tileLocalMemory;
  }

  public HashMap<Integer,Processor> getProcessors(){
    return this.processors;
  }

  public int getRunIterations(){
    int max = 0 ;
    for(HashMap.Entry<Integer,Processor> e : processors.entrySet()){
      if(max < e.getValue().getRunIterations())
        max = e.getValue().getRunIterations();
    }
    return max;
  }

  public void resetTile(){
    // first reset the processors
    for(HashMap.Entry<Integer,Processor> e: processors.entrySet()){
      e.getValue().restartProcessor();
    }
    // refresh the tile local memory
    tileLocalMemory.resetMemoryUtilization();
    // restart the crossbar
    crossbar.restartCrossbar();
  }
  
  public String getName(){
    return this.name;
  }

  public int getId(){
    return this.id;
  }

  public void setId(int id){
    this.id = id;
  }

}
