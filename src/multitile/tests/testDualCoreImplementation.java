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
  @date   27 November 2022
  @version 1.1
  @ brief
		This is a dual core implementation with unbounded memory and 2 channels
		in the crossbar, running the sobel application

--------------------------------------------------------------------------
*/
package src.multitile.tests;

import src.multitile.scheduler.FCFS;

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

public class testDualCoreImplementation {
    public static void main(String[] args) throws IOException {
      System.out.println("Testing dualcore implementation testcase!");

      Architecture architecture = new Architecture("Arch","Tile_testDualCore",2,1.0,2);
      Tile t1 = architecture.getTiles().get(0); 
      
      TestApplicationDualCore sampleApplication = new TestApplicationDualCore(t1);  
      Application app = sampleApplication.getSampleApplication();
      ApplicationManagement.assignFifoMapping(app,architecture); 

      FCFS scheduler = new FCFS();
      scheduler.setApplication(app);
      scheduler.setArchitecture(architecture);

      scheduler.setMaxIterations(3);
      scheduler.schedule();

      architecture.getTiles().get(0).getProcessors().get(0).getScheduler().saveScheduleStats(".");
      architecture.getTiles().get(0).getProcessors().get(1).getScheduler().saveScheduleStats(".");
      architecture.getTiles().get(0).getCrossbar().saveCrossbarUtilizationStats(".");

      System.out.println("Testing dualcore implementation testcase done!");
    }
}
