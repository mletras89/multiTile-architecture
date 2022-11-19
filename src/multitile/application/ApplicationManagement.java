/* vim: ts=2 sw=2*/

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
  @date  16 November 2022
  @version 1.1
  @ brief
        This class implements methods for application management
 
--------------------------------------------------------------------------
*/

package src.multitile.application;

import java.util.*;
import src.multitile.application.Actor;
import src.multitile.application.Application;
import src.multitile.application.Fifo;
import src.multitile.application.CompositeFifo;

public class ApplicationManagement{
  
  public static Map<Integer,MulticastActor> getMulticastActors(Application app){
    Map<Integer,MulticastActor> multicastActors = new HashMap<>();
    for(Map.Entry<Integer,Actor> actor : app.getActors().entrySet()){
      if (actor.getValue().getType() == Actor.ACTOR_TYPE.MULTICAST){
        multicastActors.put(actor.getKey(),(MulticastActor)actor.getValue());
      }
    }
    return multicastActors;
  }

  // these method receives an application and returns a modified application removing 
  // all the mergeable multicast actors from the application and replace them by composite channels
  public static void collapseMergeableMulticastActors(Application app){
    // get all the multicast actors
    Map<Integer,MulticastActor> multicastActors = getMulticastActors(app);
    
    for(Map.Entry<Integer,MulticastActor> multicastActor : multicastActors.entrySet()){
      MulticastActor selectedActor = multicastActor.getValue();
      
      if(selectedActor.isMergeMulticast() == true){
        // if the actor is mergeable, we remove it and replace it by a composite channel
        Vector<Fifo> inputFifos  = selectedActor.getInputFifos(); // it should be only one writer
        Vector<Fifo> outputFifos = selectedActor.getOutputFifos(); // it might be multiple readers, more that one
        Fifo writer = inputFifos.get(0);
        List<Fifo>  readerFifos = new ArrayList<Fifo>(outputFifos);

        CompositeFifo compositeFifo = FifoManagement.createCompositeChannel(writer,readerFifos,selectedActor); 
        // once created the compositefifo, we have to connected into the application
        int idWriterActor = writer.getSource().getId();
        app.getActors().get(idWriterActor).removeOutputFifo(writer.getId());
        // connecting the input of the composite fifo
        app.getActors().get(idWriterActor).getOutputFifos().add(compositeFifo);

        // now connect the readers to the composite fifo
        for(Fifo dstFifo : readerFifos){
          int idReaderActor = dstFifo.getDestination().getId();
          app.getActors().get(idReaderActor).removeInputFifo(dstFifo.getId());
          // connectinf the outputs of the composite fifo
          app.getActors().get(idReaderActor).getInputFifos().add(compositeFifo);
        }
      }
    }
  }

}
