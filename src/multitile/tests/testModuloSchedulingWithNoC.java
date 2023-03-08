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
  @date   15 December 2022
  @version 1.1
  @ brief
     Example of a single tile architecture with four processors with local
     memory, crossbar and tile local memory running a modulo scheduluer
     and implementing memory relocation
--------------------------------------------------------------------------
*/
package src.multitile.tests;

import src.multitile.scheduler.ModuloScheduler;

import src.multitile.architecture.Architecture;
import src.multitile.architecture.Tile;
import src.multitile.architecture.Memory;
import src.multitile.architecture.Processor;

import src.multitile.application.Application;
import src.multitile.application.Actor;
import src.multitile.application.Fifo;
import src.multitile.application.CompositeFifo;
import src.multitile.application.FifoManagement;
import src.multitile.application.ApplicationManagement;
import src.multitile.application.ActorManagement;
import src.multitile.application.FifoManagement;
import src.multitile.architecture.ArchitectureManagement;


import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class testModuloSchedulingWithNoC {
    public static void main(String[] args) throws IOException {
      System.out.println("Testing two tiles implementation, each tile has 2 cores and connected with a NoC!");

      ActorManagement.resetCounters();
      FifoManagement.resetCounters();
      ArchitectureManagement.resetCounters();

      Architecture architecture = new Architecture("architecture",2,2, 1.0, 2);

      for(HashMap.Entry<Integer,Tile> t: architecture.getTiles().entrySet()){
        System.out.println("Tile name:"+t.getValue().getName());
        for(HashMap.Entry<Integer,Processor> p: t.getValue().getProcessors().entrySet()){
     	  System.out.println("\tProcessor name:"+p.getValue().getName()+" with id "+p.getValue().getId());
	  System.out.println("\tOwner tile:"+p.getValue().getOwnerTile().getName());
     	  System.out.println("\t\tAttached to local memory:"+p.getValue().getLocalMemory().getName());
        }			
      }

      // set the memory sizes
      architecture.getTiles().get(0).getProcessors().get(0).getLocalMemory().setCapacity(5000000);
      architecture.getTiles().get(0).getProcessors().get(1).getLocalMemory().setCapacity(5000000);
      architecture.getTiles().get(1).getProcessors().get(2).getLocalMemory().setCapacity(5000000);
      architecture.getTiles().get(1).getProcessors().get(3).getLocalMemory().setCapacity(5000000);

      TestApplicationQuadCoreMemoryBound sampleApplication = new TestApplicationQuadCoreMemoryBound(architecture.getTiles().get(0), architecture.getTiles().get(1),architecture.getGlobalMemory());  
      Application app = sampleApplication.getSampleApplication();
      
      ModuloScheduler scheduler = new ModuloScheduler();
      // I need to update the actor mapping according to the modulo schedule!!!!!
      scheduler.setApplication(app);
      scheduler.setArchitecture(architecture);
			
      scheduler.setMaxIterations(10);
      scheduler.calculateModuloSchedule();
      //System.out.println("PRINTING KERNEL: ");
      //scheduler.printKernelBody();
      // once the kernell is done, reassign the actor Mapping and then reassing the fifoMapping
      scheduler.findSchedule();
      ApplicationManagement.assignActorMapping(app,architecture,scheduler);
      ApplicationManagement.assignFifoMapping(app,architecture); 
      //app.printFifos();
 
      scheduler.schedule();

      System.out.println("Single iteration delay: "+scheduler.getDelaySingleIteration());

      System.out.println("The MMI is: "+scheduler.getMII());


      // dumping system utilization statistics
      
      for(HashMap.Entry<Integer,Tile> t: architecture.getTiles().entrySet()){
        for(HashMap.Entry<Integer,Processor> p: t.getValue().getProcessors().entrySet()){
          p.getValue().getScheduler().saveScheduleStats(".");
	  p.getValue().getLocalMemory().saveMemoryUtilizationStats(".");
        }
        t.getValue().getCrossbar().saveCrossbarUtilizationStats(".");
        t.getValue().getTileLocalMemory().saveMemoryUtilizationStats(".");
      }

      architecture.getNoC().saveNoCUtilizationStats(".");
      architecture.getGlobalMemory().saveMemoryUtilizationStats(".");

      System.out.println("Testing quadcore implementation testcase done!");
    }
}
