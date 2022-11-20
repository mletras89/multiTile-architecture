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
  @date   04 November 2022
  @version 1.1
  @ brief
     Example of a single tile architecture with a single processor with local
     memory, crossbar and tile local memory
--------------------------------------------------------------------------
*/
package src.multitile.tests;

import src.multitile.architecture.Tile;
import src.multitile.architecture.Memory;
import src.multitile.architecture.Processor;

import src.multitile.application.Application;
import src.multitile.application.Actor;
import src.multitile.application.MulticastActor;
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

public class testCompositeChannel {
    public static void main(String[] args) throws IOException {
      System.out.println("Testing composite channel Implementation!");

      Tile t1 = new Tile(1,"Tile_testComposite",1,1.0,2);
      
      TestApplication sampleApplication = new TestApplication(t1);  
      Application app = sampleApplication.getSampleApplication();

      t1.setTotalIterations(3);
      t1.runTileActors(app);
      t1.getProcessors().get(0).getScheduler().saveScheduleStats(".");
      t1.getCrossbar().saveCrossbarUtilizationStats(".");


 //     ApplicationManagement.collapseMergeableMulticastActors(app);
//      app.resetApplication();
//      
//      t1.setName("Tile_testCompositeAfterMerging");
//      t1.setTotalIterations(3);
//      t1.runTileActors(app);
//      t1.getProcessors().get(0).getScheduler().saveScheduleStats(".");
//      t1.getCrossbar().saveCrossbarUtilizationStats(".");
      System.out.println("Testing composite channel Implementation done!");
    }
}

