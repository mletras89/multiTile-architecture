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
  @date   29 November 2022
  @version 1.1
  @ brief
     This class describes the Modulo scheduler implemented as presented in:
     Kejariwal, A., Nicolau, A. (2011). Modulo Scheduling and Loop Pipelining. 
     In: Padua, D. (eds) Encyclopedia of Parallel Computing. 
     Springer, Boston, MA. https://doi.org/10.1007/978-0-387-09766-4_65
  
  NOTE: This is only valid for single-rate dataflows
--------------------------------------------------------------------------
*/
package src.multitile.scheduler;

import src.multitile.Action;
import src.multitile.Transfer;

import src.multitile.architecture.Processor;
import src.multitile.architecture.Tile;
import src.multitile.architecture.Architecture;
import src.multitile.architecture.ArchitectureManagement;
import src.multitile.architecture.Memory;

import src.multitile.application.Application;
import src.multitile.application.Actor;
import src.multitile.application.Actor;
import src.multitile.application.Fifo;

import java.util.List;
import java.util.Map;
import java.util.*;

public class ModuloScheduler extends BaseScheduler implements Schedule{

  // first level key is the step,
  // the second level map, key is the id of the tile, 
  // the third level of the map, the key is the processor and bool the state of the occupation
  private HashMap<Integer,HashMap<Integer,HashMap<Integer,Boolean>>> resourceOcupation;

  // key is the actor id and the value is the scheduled step
  private HashMap<Integer,Integer> l;
  private int MII;
  private int lastStep;
  private HashMap<Integer,List<Integer>> kernel;

  public ModuloScheduler(){
    super();
    this.l = new HashMap<>();

    this.resourceOcupation = new HashMap<>();
    this.setMaxIterations(3);
  }

  public void calculateModuloSchedule(){
    HashMap<Integer,Tile> tiles = architecture.getTiles();
    List<Integer> V = new ArrayList<>();
    for(Map.Entry<Integer,Actor> v : application.getActors().entrySet()){
      V.add(v.getKey());
    }
    // first define a map  of scheduled actions <key,value> <ActorId,Bool>
    HashMap<Integer, Boolean> scheduled = new HashMap<>(); 
    // set the map, with the actors as not scheduled at this point
    for(Map.Entry<Integer,Actor> e :   application.getActors().entrySet()){
      scheduled.put(e.getKey(),false);
    }
    // 1 [Compute resource usage]
    // Examine the loop body to determine the usage, usage(i), of each resource class R(i) by the loop body

    // <K,V> here the key is the id of the tile and the value is the usage of cpus in the tile
    HashMap<Integer, Integer> usage = new HashMap<>();
    for(HashMap.Entry<Integer,Tile> t: tiles.entrySet()){
      usage.put(t.getKey(), 0);
    }
    // update the usage
    for(HashMap.Entry<Integer,Tile> t: tiles.entrySet()){
      usage.put(t.getKey(),application.getActors().size());
    }
    // 2 [Determine recurrencies]
    // 		Enumerate all the recurrences in the dependence graph.
    // 		Let C be the set of all recurrences in the dependence graph. Compute len(c) \forall c \in C
    // 	3 [Compute the lower bound of minimum initiation interval]
    // 		a) [Compute the resource-constrained initiation interval]
    List<Integer> tmpL = new ArrayList<>();
    for(HashMap.Entry<Integer,Tile> t:tiles.entrySet()){
			// have to modifiy this part for multitile, here I am assuming that all the actors are mapped to the same tile
      tmpL.add((int)Math.ceil(usage.get(t.getKey())/(double)t.getValue().getProcessors().size()));
    }
    int RESII = Collections.max(tmpL);
    //          b) [Compute the recurrence-constrained initiation interval]
    // 		   I do not have to calcualte this because there are not cycles
    // 		c) [Compute the minimum initiation interval]
    MII = RESII;
    // [Modulo schedule the loop]
    // 		a) [Schedule operations in G(V, E) taking only intra-iteration dependences into account]
    // 		   Let U(i, j) denote the usage of the i-th resource class in control step j
    //             In this implementation, U(i, j) denote the usage of the i-th tile class in control step j
    //             i and j are stored in a list which serves as key in a map
    Map<ArrayList<Integer>, Integer> U = new HashMap<>();
    for(HashMap.Entry<Integer,Tile> t: tiles.entrySet()) {
      for (int i=0; i<application.getActors().size()*2;i++) {
        ArrayList<Integer> p = new ArrayList<>();
        p.add(t.getKey());
        p.add(i);
        U.put(p, 0);
      }
    }
    // compute PCOUNT and SUCC
    // PCOUNT: is the number of immediate predecessors of v not yet scheduled  
    // SUCC: is the set of all immediate successors of v
    // Map<ActorId, Value>
    l 		                        = new HashMap<>();
    HashMap<Integer,Integer> PCOUNT	= new HashMap<>();
    HashMap<Integer,Set<Integer>> SUCC 	= new HashMap<>();
    for(Map.Entry<Integer, Actor> actor : application.getActors().entrySet()) {
    	l.put(actor.getKey(), 1);
    	PCOUNT.put(actor.getKey(), getPCOUNT(actor.getValue(), scheduled));
    	SUCC.put(actor.getKey(), getSUCC(actor.getValue()));
    }
    
    // the number of the control step and the list is the actors scheduled in that control step
    HashMap<Integer,List<Integer>> controlStep = new HashMap<>();
    HashMap<Integer,List<Integer>> occHard     = new HashMap<>();

    while(!V.isEmpty()) {
      List<Integer> removeV = new ArrayList<>();
      for (int v : V) {
        //System.out.println("Trying to sched "+actors.get(v).getName());
	/* Check whether data dependences are satisfied */
	if (PCOUNT.get(v) == 0) {
	  //System.out.println("TRY Scheduling "+actors.get(v).getName()+" on control step "+l.get(v)+ " on resource "+cpus.get(actors.get(v).getMapping()).getName());
	  /* Check that no more than num(r(v)) operations are scheduled on the
             resources corresponding to *R(r(v)) at the same time modulo MII */
	  int BU = calcU(l,MII,U,v);
          //System.out.println("l:");
          //System.out.println(l);
          //System.out.println("U");
          //System.out.println(U);

          int mappingV = application.getActors().get(v).getMapping().getOwnerTile().getId();
          //System.out.println("BU:"+BU);
	  while(BU>=tiles.get(mappingV).getProcessors().size()) {
	    l.put(v, l.get(v)+1);
	    BU = calcU(l,MII,U,v);  
	  }
          ArrayList<Integer> pair = new ArrayList<>();
          pair.add(mappingV);
          pair.add(l.get(v));
          U.put(pair, U.get(pair) + 1);
          //System.out.println(U);
	  for (int w : SUCC.get(v)) {
	    PCOUNT.put(w, PCOUNT.get(w) -1 );
	    int maxVal = l.get(w) > l.get(v)+1 ? l.get(w) : l.get(v)+1;
	    l.put(w,maxVal);
	  }
	  scheduled.put(v, true);
          //System.out.println("Actor: "+application.getActors().get(v).getName());
	  removeV.add(v);
	}
      }
      V.removeAll(removeV);
    }
  }
 
  public void printKernelBody(){
    int scheduled = 0;
    int step = 1;
    while(scheduled < application.getActors().size()){
      System.out.println("Scheduling Step: "+step);
      for(Map.Entry<Integer,Integer> entryL : l.entrySet()){
        if(entryL.getValue() == step){
          System.out.println("Actor: "+application.getActors().get(entryL.getKey()).getName());
          scheduled++;
        }
      }
      step++;
    }
  }

  public void findSchedule(){
    // at least 3 iterations
    List<List<Integer>>	singleIteration     = new ArrayList<>();
    this.kernel  = new HashMap<>();
    int scheduled = 0;
    int step = 1;

    int stepStartKernel = 0;
    int stepEndKernel = 0;
    
    // 1) calculate the order of a single iteration
    while(scheduled < application.getActors().size()){
      List<Integer> stepList = new ArrayList<>();
      for(Map.Entry<Integer,Integer> entryL : l.entrySet()){
        if(entryL.getValue() == step){
          stepList.add(entryL.getKey());
    	  scheduled++;
        }
      }
      this.kernel.put(step, new ArrayList<>(stepList));
      singleIteration.add(new ArrayList<>(stepList));
      step++;
    }
    
    // 2) Generate a schedule up to maxIterations iterations and put them in kernel
    this.lastStep = 0;
    for(int k=1; k<this.getMaxIterations();k++){
      for(int i = MII*k+1; i < MII*k+singleIteration.size()+1; i++ ){
        if(this.kernel.containsKey(i)){
          List<Integer> actors = new ArrayList<>(this.kernel.get(i));
      	  actors.addAll(new ArrayList<>(singleIteration.get(i-(MII*k)-1)));
      	  this.kernel.put(i, actors);
        }
        else{
          this.kernel.put(i, new ArrayList<>(singleIteration.get(i-MII*k-1)));
        }
        this.lastStep = i;
      }
    }
//    System.out.println("Last step:"+lastStep);
//    // now, we print the schedule
    for(int i=1; i <= this.lastStep;i++){
      System.out.println("STEP: "+i);
      Collections.sort(this.kernel.get(i));
      for(int v : this.kernel.get(i)){
        System.out.println("Actor: "+application.getActors().get(v).getName());
      }
    }
    // 3) we find the kernel, to calculate the throuhgput
    boolean foundKernel = false;
    for(int i=1;i<=this.lastStep-1;i++){
      stepStartKernel = i;  
      for(int j=i+1;j<=this.lastStep;j++){
        stepEndKernel = j;
        if(this.kernel.get(stepStartKernel).equals(this.kernel.get(stepEndKernel))){
          foundKernel = true;
          break;
        }
      }
      if (foundKernel == true)
        break;
    }
    System.out.println("Kernel starts at: "+stepStartKernel+" and ends at: "+stepEndKernel);
  }

  public void schedule(){
    HashMap<Integer,Tile> tiles = architecture.getTiles();
    this.getScheduledStepActions().clear();
    // 4) set the resource ocupation of all the resources as empty
    resourceOcupation = new HashMap<>();
    for(int i=1; i <= this.lastStep;i++){
      // filling first level with step as key
      resourceOcupation.put(i,new HashMap<>());
      // filling second level where the key is the tile id
      for(HashMap.Entry<Integer,Tile> t: tiles.entrySet()){
        resourceOcupation.get(i).put(t.getKey(),new HashMap<>());
        // filling the third level where the key is the processor id
        for(HashMap.Entry<Integer,Processor> p: t.getValue().getProcessors().entrySet()){
          resourceOcupation.get(i).get(t.getKey()).put(p.getKey(),false);
        }
      }
    }   
    // 5) now, we schedule the actions in the first tree iterations
    int i = 1;
    List<Transfer> transfersToMemory = new ArrayList<>();
    while(i<=this.lastStep){
      this.getSchedulableActors(application.getActors(),application.getFifos(),i,this.kernel);
      //LinkedList<Action> stepScheduledActions = new LinkedList<Action>();
      // key is tile id
      HashMap<Integer,HashMap<Integer,Boolean>> currentTilesOccupation = resourceOcupation.get(i);
      // remove all the actions in the queue
      int sizeActions = queueActions.size();
      for(int k =0 ; k < sizeActions; k++){
        Action action = queueActions.remove();
        action.setStep(i);
        System.out.println("current actor "+action.getActor().getName());
        int actionToTileId = action.getActor().getMappingToTile().getId();
        // get the mapping 
        HashMap<Integer,Boolean> processorUtilization = currentTilesOccupation.get(actionToTileId);
        int availableProcessor = getNextAvailableProcessor(processorUtilization);
        assert availableProcessor != -1;
        Processor p = tiles.get(actionToTileId).getProcessors().get(availableProcessor);
        // setting the mapping of the actor and action
        application.getActors().get(action.getActor().getId()).setMapping(p);
        action.getActor().setMapping(p);
        // put the action in the processor
        tiles.get(actionToTileId).getProcessors().get(availableProcessor).getScheduler().getQueueActions().add(action);
        processorUtilization.put(availableProcessor,true);
        // update the availabilty
        currentTilesOccupation.put(actionToTileId,processorUtilization);
      }
      // iterate tiles and the processors to perform the simulation of the application
      for(HashMap.Entry<Integer,Tile> t: tiles.entrySet()){
        for(HashMap.Entry<Integer,Processor> p : t.getValue().getProcessors().entrySet()){
          Queue<Action> actions = p.getValue().getScheduler().getQueueActions();
          for(Action action : actions){
                // first schedule the reads
          	p.getValue().getScheduler().commitReadsToCrossbar(action,application.getFifos());
          	Map<Actor,List<Transfer>> readTransfers = p.getValue().getScheduler().getReadTransfers();
          	t.getValue().getCrossbar().cleanQueueTransfers();
          	for(Map.Entry<Actor,List<Transfer>> entry : readTransfers.entrySet()){
            	  t.getValue().getCrossbar().insertTransfers(entry.getValue());
          	}
          	//commit the read transfers
          	t.getValue().getCrossbar().commitTransfersinQueue();
          	// update the read transfers of each processor with the correct due time
          	Map<Actor,List<Transfer>> processorReadTransfers = t.getValue().getCrossbar().getScheduledReadTransfers(p.getValue());
          	// commit the action in the processor
          	p.getValue().getScheduler().setReadTransfers(processorReadTransfers);
          	p.getValue().getScheduler().commitSingleAction(action); // modificar este
          	// finally, schedule the write of tokens
          	p.getValue().getScheduler().commitWritesToCrossbar(action);
          	// put writing transfers to crossbar
          	// get write transfers from the scheduler
          	Map<Actor,List<Transfer>> writeTransfers = p.getValue().getScheduler().getWriteTransfers();
          	for(Map.Entry<Actor,List<Transfer>> entry: writeTransfers.entrySet()){
            	  t.getValue().getCrossbar().insertTransfers(entry.getValue());
          	}
          	// commit write transfers in the crossbar
          	t.getValue().getCrossbar().commitTransfersinQueue();
          	// update the write transfers of each processor with the correct start and due time
          	Map<Actor,List<Transfer>> processorWriteTransfers = t.getValue().getCrossbar().getScheduledWriteTransfers(p.getValue());
          	p.getValue().getScheduler().setWriteTransfers(processorWriteTransfers);
       		// update the last event in processor, taking into the account the processorWriteTransfers
          	p.getValue().getScheduler().updateLastEventAfterWrite(action);
          	// insert the time of the produced tokens by acton into the correspondent fifos
          	p.getValue().getScheduler().produceTokensinFifo(action,application.getFifos());
          	// managing the tracking of the memories
       	 	p.getValue().getScheduler().setTransfersToMemory();
          	transfersToMemory.addAll(p.getValue().getScheduler().getTransfersToMemory());

          	p.getValue().getScheduler().getReadTransfers().clear();
          	p.getValue().getScheduler().getWriteTransfers().clear();

                // put the action in the step action
                if(!this.getScheduledStepActions().containsKey(action.getStep())){
                  this.getScheduledStepActions().put(action.getStep(),new ArrayList<Action>());
                }
                this.getScheduledStepActions().get(action.getStep()).add(action);
          }
        }
        // when, I finish the tile update last event in each processor with the maximum of all the processors
        double maxTimeP = 0.0;
        for(HashMap.Entry<Integer,Processor> p : t.getValue().getProcessors().entrySet()){
          if (maxTimeP < p.getValue().getScheduler().getLastEventinProcessor())
            maxTimeP = p.getValue().getScheduler().getLastEventinProcessor();
          }
        for(HashMap.Entry<Integer,Processor> p : t.getValue().getProcessors().entrySet()){
          p.getValue().getScheduler().setLastEventinProcessor(maxTimeP);
        }
      }
      // commit the reads/writes to memory
      SchedulerManagement.sort(transfersToMemory);
      Transfer ReMapTransfer = null;
      boolean successMemoryOperations = true; 
      for(Transfer t: transfersToMemory){
        if(t.getType()==Transfer.TRANSFER_TYPE.READ){
          // check if we can read
          if(!t.getFifo().canFifoReadFromMemory()){
            ReMapTransfer = t;
            successMemoryOperations = false;
            break;
          }
          t.getFifo().fifoReadFromMemory(t);
        }else{
          if(!t.getFifo().canFifoWriteToMemory()){
            ReMapTransfer = t;
            successMemoryOperations = false;
            break;
          }
          t.getFifo().fifoWriteToMemory(t);
        }
      }
      if(successMemoryOperations){
        // commit the scheduled actions in this step
      	// update the state of the fifos
      	for(HashMap.Entry<Integer,Tile> t: tiles.entrySet()){
      	  for(HashMap.Entry<Integer,Processor> p : t.getValue().getProcessors().entrySet()){
      	    p.getValue().getScheduler().fireCommitedActions(application.getFifos());
      	    p.getValue().getScheduler().getTransfersToMemory().clear();
      	  }
      	}
        resourceOcupation.put(i,currentTilesOccupation);
        architecture.syncronizeStateOfArchitecture();
        i++;
      }else{
        System.out.println("MEMORY NEEDS TO BE RELOCATED");
        System.out.println("FIFO "+ReMapTransfer.getFifo().getName()+"wants to "+ReMapTransfer.getType()+" to memory "+ReMapTransfer.getFifo().getMapping().getName());
        Memory newMapping = ArchitectureManagement.getMemoryToBeRelocated(ReMapTransfer.getFifo(),architecture);
        System.out.println("new Mapping "+newMapping.getName());
        
        architecture.reverseStateOfArchitecture(i);
        assert true: "MEMORY NEED TO BE RELOCATED";
      }

      transfersToMemory.clear();



    }
  }

  public double getDelaySingleIteration(){
    // we take into account when k=3 and K=4
    int start  = this.MII*3+1;
    int end = this.MII*4+1;

    double startTime = Double.POSITIVE_INFINITY;
    double endTime  = -1;
    //System.out.println("Start iteration "+start);
    for(Action a : this.getScheduledStepActions().get(start)){
      //System.out.println("ACTION:"+a.getActor().getName());
      if(a.getStart_time() < startTime)
        startTime = a.getStart_time();
    }
    //System.out.println("End iteration "+end);
    for(Action a : this.getScheduledStepActions().get(end)){
      //System.out.println("ACTION:"+a.getActor().getName());
      if(a.getDue_time() > endTime)
        endTime = a.getDue_time();
    }
    return endTime - startTime;
  }

  public int getNextAvailableProcessor(HashMap<Integer,Boolean> processorUtilization){
    // key is the id of the processor and the boolean is the availability of the processor
    for(HashMap.Entry<Integer,Boolean> e : processorUtilization.entrySet()){
      if (e.getValue() == false)
        return e.getKey();
    }
    return -1;    // only return this, if there are no available processors; probably implemente remapping
  }

  public void getSchedulableActors(Map<Integer,Actor> actors,Map<Integer,Fifo> fifos, 
                                   int step,
                                   // step, actor_ids
                                   HashMap<Integer, List<Integer>> kernel){
                                   
    // from the list of actors in Processor, check which of them can fire
    this.cleanQueue();
    List<Integer> actorsInStep = kernel.get(step);
    for(int v : actorsInStep){
      if (actors.get(v).canFire(fifos)){
        Action action = new Action(actors.get(v));
        this.insertAction(action);
      }
    }
  }

  // PCOUNT: is the number of immediate predecessors of v not yet scheduled  
  int getPCOUNT(Actor v, HashMap<Integer, Boolean> scheduled) {
    int pCount=0;
    for(Fifo fifo : v.getInputFifos()) {
      int sourceActorId = fifo.getSource().getId();
      if (scheduled.get(sourceActorId) == false)
        pCount++;
    }
    return pCount;
  }
    
  // SUCC: is the set of all immediate successors of v
  //  the set is composed of the ids
  Set<Integer> getSUCC(Actor v) {
    Set<Integer> SUCC = new HashSet<Integer>();
		
    for(Fifo fifo: v.getOutputFifos()) {
      Integer targetActor = fifo.getDestination().getId();
      SUCC.add(targetActor);
    }
    return SUCC;
  }

  int calcU(HashMap<Integer,Integer> l,int MII, Map<ArrayList<Integer>, Integer> U,int v) {
    int BU=0;
    /*System.out.println("Here");
    for(Map.Entry<ArrayList<Integer>, Integer> u : U.entrySet()) {
            System.out.println("key :["+u.getKey().get(0)+","+u.getKey().get(1)+"] val: "+u.getValue());
    }
    System.out.println("==================================");*/
    for (int i=0;i<=Math.floor(l.get(v)/MII);i++) {
            int mapping = application.getActors().get(v).getMapping().getOwnerTile().getId();
            //System.out.println("getting key :["+mapping+","+(l.get(v)-i*MII)+"]");
            ArrayList<Integer> pair = new ArrayList<>();
            pair.add(mapping);
            pair.add(l.get(v)-i*MII);
            BU += U.get(pair);
    }
    return BU;
  }

  public int getMII(){
    return this.MII;
  }

}
