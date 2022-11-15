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
  @date  15 November 2022
  @version 1.1
  @ brief
        This class implements methods for fifo management such as merging
        the fifos
 
 
--------------------------------------------------------------------------
*/
package src.multitile.application;

import java.util.*;
import src.multitile.application.Actor;
import src.multitile.application.Fifo;
import src.multitile.application.CompositeFifo;

public class FifoManagement{
  private static int fifoIdCounter;
  private static int compositeCounter;

  static{
    fifoIdCounter=1;
    compositeCounter = 1;
  }

  public static int getCompositeCounter(){
    return compositeCounter++;
  }

  public static int getFifoId(){
    return fifoIdCounter++;
  }

  public static CompositeFifo createCompositeChannel(Fifo writer,List<Fifo> readerFifos, Actor multicastActor){
    // create a composite channel from a given list of fifos
    // a composite actor has only one writer and multiple readers
    CompositeFifo compositeFifo = new CompositeFifo("compositeFifo_"+getCompositeCounter(),writer.get_tokens(),writer.get_capacity(),writer.getTokenSize(),writer.getMapping(),writer.getConsRate(),writer.getProdRate(),writer.getSource(),writer.getDestination());

    compositeFifo.setWriter(writer);
    compositeFifo.setReaders(readerFifos);

    return compositeFifo;
  }

}

