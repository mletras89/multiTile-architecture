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
  @date   07 December
  @version 1.1
  @ brief
     This class is the basis of an scheduler implementation
--------------------------------------------------------------------------
*/

package src.multitile.scheduler;

import src.multitile.Action;
import src.multitile.Transfer;

import src.multitile.architecture.Processor;
import src.multitile.architecture.Tile;
import src.multitile.architecture.Architecture;

import src.multitile.application.Application;
import src.multitile.application.Actor;
import src.multitile.application.Actor;
import src.multitile.application.Fifo;

import java.util.List;
import java.util.Map;
import java.util.*;

public class BaseScheduler{
  // key is the step and the list are the actions scheduled in the step
  private HashMap<Integer,List<Action>> scheduledStepActions;

  private int maxIterations;
  public Architecture architecture;
  public Application application;
  public Queue<Action> queueActions;

  public BaseScheduler(){
    this.queueActions = new LinkedList<>();
    this.scheduledStepActions = new HashMap<>();
  }

  public HashMap<Integer,List<Action>> getScheduledStepActions(){
    return this.scheduledStepActions;
  }

  public void setMaxIterations(int maxIterations){
    this.maxIterations = maxIterations;
  }

  public int getMaxIterations(){
    return this.maxIterations;
  }

  public void setApplication(Application application){
    this.application = application;
  }

  public void setArchitecture(Architecture architecture){
    this.architecture = architecture;
  }
  public void insertAction(Action a){
    queueActions.add(new Action(a));
  }

  public void cleanQueue(){
    this.queueActions.clear();
  }

  public Queue<PassTransferOverArchitecture> calculatePathOfTransfer(Transfer transfer){
    // this function returns a list of interconnect sequences
    Queue<PassTransferOverArchitecture> sequence = new LinkedList<>();

    if (transfer.getType() == Transfer.TRANSFER_TYPE.READ){
      // then here the source is the memory and the destination is the processor
      Memory source           = transfer.getFifo().getMapping();

      Processor destination   = transfer.getActor().getMapping();
      Tile destinationTile    = destination.getOwnerTile();
      switch(source.getType()){
        case MEMORY_TYPE.GLOBAL_MEM:
          // this is the easiest case, the sequence es GlobalMemory -> NoC -> Tile local crossbar -> processor
          sequence.add(new passTransferOverArchitecture(architecture.getNoC()));
          sequence.add(new passTransferOverArchitecture(destinationTile.getCrossbar()));
        break;
    
        case MEMORY_TYPE.TILE_LOCAL_MEM:
          Tile sourceTile         = source.getOwner();
          // here is a bit more complex, if both the source and the destination are in the same tile
          // TILE_LOCAL_MEM -> CROSSBAR TILE LOCAL -> processor
          if (sourceTile.equals(destinationTile)){
            sequence.add(new passTransferOverArchitecture(destinationTile.getCrossbar()));  
          }else{
          // if source and destination are not in the same tile
          // TILE_LOCAL_MEM_T1 -> CROSSBAR T1 -> NoC -> CROSSBAR T2 -> processor
            sequence.add(new passTransferOverArchitecture(sourceTile.getCrossbar()));
            sequence.add(new passTransferOverArchitecture(architecture.getNoC()));
            sequence.add(new passTransferOverArchitecture(destinationTile.getCrossbar()));
          }
        break;

        case MEMORY_TYPE.LOCAL_MEM:
          Processor localMemOwner = source.getEmbeddedToProcessor();
          Tile tileSource = localMemOwner.getOwnerTile();
          // mapped to different processors but in the same tile
          if (!localMemOwner.equals(destination) && destinationTile.equals(tileSource)){
            // the sequence must be MEM_SOURCE -> CROSSBAR -> processor
            sequence.add(new passTransferOverArchitecture(destinationTile.getCrossbar()));
          }else if(!localMemOwner.equals(destination) && !destinationTile.equals(tileSource)){
            // the sequence must be MEM_SOURCE -> CROSSBAR_SOURCE -> NoC -> CROSSBAR_DEST -> processor
            sequence.add(new passTransferOverArchitecture(tileSource.getCrossbar());
            sequence.add(new passTransferOverArchitecture(architecture.getNoc());
            sequence.add(new passTransferOverArchitecture(destinationTile.getCrossbar());
          }
        break;
      } 
    }
    if(transfer.getType() == Transfer.TRANSFER_TYPE.WRITE){
      // here the source is the processor and the destination is the memory
      Processor source    = transfer.getActor().getMapping();
      Tile sourceTile     = source.getOwnerTile();
      
      Memory destination  = transfer.getFifo().getMapping();
      switch(destination.getType()){
        case MEMORY_TYPE.GLOBAL_MEM:
          // SOURCE_CROSSBAR -> Noc -> GLOBAL MEMORY
          sequence.add(new passTransferOverArchitecture(sourceTile.getCrossbar() ) );
          sequence.add(new passTransferOverArchitecture(architecture.getNoC()) );
        break;

        case MEMORY_TYPE.TILE_LOCAL_MEM:
          Tile destinationTile = destination.getOwner();
          // here is a bit more complex, if both source and destination are in the same tile
          // source -> source CROSSBAR -> TILE_LOCAL_MEM
          if(sourceTile.equals(destinationTile())){
            sequence.add(new passTransferOverArchitecture(destinationTile.getCrossbar()));
          }else{
            // if source and destination are not in the same tile
            // processor -> CROSSBAR SOURCE -> NoC -> CROSSBAR DESTINATION -> TILE_LOCAL_MEM
            sequence.add(new passTransferOverArchitecture(tileSource.getCrossbar());
            sequence.add(new passTransferOverArchitecture(architecture.getNoc());
            sequence.add(new passTransferOverArchitecture(destinationTile.getCrossbar());        
          }
        break;

        case MEMORY_TYPE.LOCAL_MEM:
          Processor localMemOwner = destination.getEmbeddedToProcessor();
          Tile destinationTile    = localMemOwner.getOwnerTile();
          //mapped to different processors but in the same tile          
          if (!localMemOwner.equals(source) && destinationTile.equals(tileSource)){
            // the sequence must be MEM_SOURCE -> CROSSBAR -> processor
            sequence.add(new passTransferOverArchitecture(destinationTile.getCrossbar()));
          }else if(!localMemOwner.equals(source) && !destinationTile.equals(tileSource)){
            // the sequence must be MEM_SOURCE -> CROSSBAR_SOURCE -> NoC -> CROSSBAR_DEST -> processor
            sequence.add(new passTransferOverArchitecture(tileSource.getCrossbar());
            sequence.add(new passTransferOverArchitecture(architecture.getNoc());
            sequence.add(new passTransferOverArchitecture(destinationTile.getCrossbar());
          } 
        break;
      }
    }
    return sequence;
  }

}
