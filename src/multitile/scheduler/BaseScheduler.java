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
  private HashMap<Integer,LinkedList<Action>> scheduledActions;

  private int maxIterations;
  public Architecture architecture;
  public Application application;
  public Queue<Action> queueActions;

  public BaseScheduler(){
    this.queueActions = new LinkedList<>();
    this.scheduledActions = new HashMap<>();
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
}
