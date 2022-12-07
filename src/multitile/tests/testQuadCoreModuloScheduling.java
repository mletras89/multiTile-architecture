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
  @date   03 December 2022
  @version 1.1
  @ brief
     Example of a single tile architecture with a single processor with local
     memory, crossbar and tile local memory running a modulo scheduluer
--------------------------------------------------------------------------
*/
package src.multitile.tests;

import src.multitile.ModuloScheduler;

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

public class testQuadCoreModuloScheduling {
    public static void main(String[] args) throws IOException {
      System.out.println("Testing quadcore implementation testcase!");

			Architecture architecture = new Architecture("architecture");

			for(HashMap.Entry<Integer,Tile> t : architecture.getTiles().entrySet()){
				System.out.println("arch:"+t.getValue().getId()+" name "+t.getValue().getName());
			}
      //Tile t1 = new Tile("Tile_testQuadCore",4,1.0,2);
      //HashMap<Integer,Tile> architecture = new HashMap<>();
      //architecture.put(t1.getId(),t1);

      TestApplicationQuadCore sampleApplication = new TestApplicationQuadCore(architecture.getTiles().get(0));  
      Application app = sampleApplication.getSampleApplication();

      ModuloScheduler scheduler = new ModuloScheduler();
      scheduler.setApplication(app);
      scheduler.setArchitecture(architecture.getTiles());

      scheduler.calculateModuloSchedule();
      //scheduler.printKernelBody();
      scheduler.schedule();

      System.out.println("The MMI is: "+scheduler.getMII());
      
      for(HashMap.Entry<Integer,Processor> p: architecture.getTiles().get(0).getProcessors().entrySet()){
        p.getValue().getScheduler().saveScheduleStats(".");
      }

		  architecture.getTiles().get(0).getCrossbar().saveCrossbarUtilizationStats(".");
      System.out.println("Testing quadcore implementation testcase done!");
    }
}

