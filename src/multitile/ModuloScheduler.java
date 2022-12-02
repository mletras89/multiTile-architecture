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
     This class describes the Modulo scheduler
--------------------------------------------------------------------------
*/
package src.multitile;

import src.multitile.architecture.Processor;
import src.multitile.architecture.Tile;


import src.multitile.application.Application;
import src.multitile.application.Actor;
import src.multitile.application.Fifo;

import java.util.List;
import java.util.Map;
import java.util.*;

public class ModuloScheduler  { //extends Scheduler implements Schedule{
  
  private List<Tile> tiles;
  private Application application;
  // key is the actor id and the value is the scheduled step
  private HashMap<Integer,Integer> l;

  public ModuloScheduler(){
    //super(name,owner);
    this.l = new HashMap<>();
  }

  public void setApplication(Application application){
    this.application = application;
  }

  public void setArchitecture(List<Tile> tiles){
    this.tiles = tiles;
  }

  public void schedule(){
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
    for(Tile t: tiles){
      usage.put(t.getId(), 0);
    }
    // update the usage
    for(Tile t: tiles){
      usage.put(t.getId(),application.getActors().size());
    }
    // 2 [Determine recurrencies]
    // 		Enumerate all the recurrences in the dependence graph.
    // 		Let C be the set of all recurrences in the dependence graph. Compute len(c) \forall c \in C
    // 	3 [Compute the lower bound of minimum initiation interval]
    // 		a) [Compute the resource-constrained initiation interval]
    List<Integer> tmpL = new ArrayList<>();
    for(Tile t:tiles){
      tmpL.add(usage.get(t.getId()));
    }
    int RESII = Collections.max(tmpL);
    //          b) [Compute the recurrence-constrained initiation interval]
    // 		   I do not have to calcualte this because there are not cycles
    // 		c) [Compute the minimum initiation interval]
    int MII = RESII;
    // [Modulo schedule the loop]
    // 		a) [Schedule operations in G(V, E) taking only intra-iteration dependences into account]
    // 		   Let U(i, j) denote the usage of the i-th resource class in control step j
    //             In this implementation, U(i, j) denote the usage of the i-th tile class in control step j
    //             i and j are stored in a list which serves as key in a map
    Map<ArrayList<Integer>, Integer> U = new HashMap<>();
    for(Tile t: tiles) {
      for (int i=0; i<application.getActors().size()*2;i++) {
        ArrayList<Integer> p = new ArrayList<>();
        p.add(t.getId());
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
	  //BU = calcU(l,MII,U,Mapping);
	  //int BU = calcU(l,MII,U,v);
	  //ArrayList<Integer> lQuery = new ArrayList<>();
	  //lQuery.add(actors.get(v).getMapping().getOwnerTile().getId());
	  //lQuery.add(l.get(v));
	  int BU = calcU(l,MII,U,v); 
	  //while BU > architecture[Mapping[v]] :
	  // BU > the number of processors
	  while(BU>0) {
	    l.put(v, l.get(v)+1);
	    BU = calcU(l,MII,U,v);  
	  }
	  //ArrayList<Integer> luNew = new ArrayList<>();
	  //luNew.add(actors.get(v).getMapping());
	  //luNew.add(l.get(v));
	  //U.put(luNew, U.get(luNew)+1);
	  for (int w : SUCC.get(v)) {
	    PCOUNT.put(w, PCOUNT.get(w) -1 );
	    int maxVal = l.get(w) > l.get(v)+1 ? l.get(w) : l.get(v)+1;
	    l.put(w,maxVal);
	  }
	  scheduled.put(v, true);
	  removeV.add(v);
	  //System.out.println("Scheduling "+actors.get(v).getName()+" on control step "+l.get(v)+ " on resource "+cpus.get(actors.get(v).getMapping()).getName());
	  //V.remove(Integer.valueOf(v));
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
          System.out.println("Actor"+application.getActors().get(entryL.getKey()).getName());
          scheduled++;
        }
      }
      step++;
    }
  }

//  public void getSchedulableActors(List<Actor> actors,Map<Integer,Fifo> fifos){
//    // from the list of actors in Processor, check which of them can fire
//    this.cleanQueue();
//   
//    for(Actor actor: actors){
//			if(actor.getMapping().equals(this.getOwner())){
//      	if(actor.canFire(fifos)){
//        	//System.out.println("Fireable: "+actor.getName());
//        	Action action = new Action(actor);
//        	this.insertAction(action);
//      	}
//			}
//    }
//  }
//
//  public void runSchedule(List<Actor> actors,Map<Integer,Fifo> fifos){
//    while(this.getRunIterations() < this.getNumberIterations()){
//    //for(int i=0;i<10;i++){
//      // First enqueue the fireable actors!
//      this.getSchedulableActors(actors,fifos);
//      this.commitActionsinQueue();
//      this.fireCommitedActions(fifos);
//    }
//  }

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



}
