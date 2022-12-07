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
     This class describes an FCFS scheduler
--------------------------------------------------------------------------
*/
package src.multitile;

import src.multitile.architecture.Processor;

import src.multitile.application.Actor;
import src.multitile.application.Fifo;

import java.util.List;
import java.util.Map;

public class FCFS extends Scheduler implements Schedule{
  
  public FCFS(String name,Processor owner){
    super(name,owner);
  }
	  
  public void getSchedulableActors(List<Actor> actors,Map<Integer,Fifo> fifos){
    // from the list of actors in Processor, check which of them can fire
    this.cleanQueue();
   
    for(Actor actor: actors){
			if(actor.getMapping().equals(this.getOwner())){
      	if(actor.canFire(fifos)){
        	//System.out.println("Fireable: "+actor.getName());
        	Action action = new Action(actor);
        	this.insertAction(action);
      	}
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
