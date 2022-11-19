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
  @date   16 November 2022
  @version 1.1
  @ brief
        Class application that encapsulates a set of actors communicated via 
	a set of fifos
--------------------------------------------------------------------------
*/
package src.multitile.application;

import java.util.*;

public class Application{
  private Map<Integer,Actor> actors;
  private Map<Integer,Fifo> fifos;

  public Application(){
    actors = new HashMap<>();
    fifos  = new HashMap<>();
  }
  
  public void setActors(Map<Integer,Actor> actors){
    this.actors = actors;
  }

  public void setActors(List<Actor> actors){
    this.actors = new HashMap<>();
    for(Actor actor:actors){
      this.actors.put(actor.getId(),actor);
    }
  }

  public void setFifos(Map<Integer,Fifo> fifos){
    this.fifos = fifos;
  }

  public Map<Integer,Fifo> getFifos(){
    return fifos;
  }

  public Map<Integer,Actor> getActors(){
    return actors;
  }

  public List<Actor> getListActors(){
    List<Actor> listActors = new ArrayList<>();
    for(Map.Entry<Integer,Actor> actor : actors.entrySet() ){
      listActors.add(actor.getValue());
    } 
    return listActors;
  }

}


