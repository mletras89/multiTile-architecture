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
  @date   14 November 2022
  @version 1.1
  @ brief
     This class describes a composite communicate channel.
     This Channel communicate actors in an application graph as well as 
     regular Fifo class, however it allows not destructive reads of tokens
--------------------------------------------------------------------------
*/

package src.multitile.application;

import src.multitile.Transfer;
import src.multitile.architecture.Memory;
import java.util.*;

public class CompositeFifo extends Fifo implements Buffer{
  Fifo writer;
  List<Fifo> readers;
  //private HashMap<Integer,Fifo> readers;

  public CompositeFifo(String name, int tokens, int capacity, int tokenSize,Memory mapping,int consRate, int prodRate, Actor src, Actor dst){
    super(name,tokens,capacity,tokenSize,mapping,consRate,prodRate,src,dst);
    this.readers = new ArrayList<>();
  }

  public CompositeFifo(String name, int tokens, int capacity, int tokenSize,Memory mapping,int consRate, int prodRate){
    super(name,tokens,capacity,tokenSize,mapping,consRate,prodRate);
    this.readers = new ArrayList<>();
  }

  public CompositeFifo(CompositeFifo another){
    super(another);
    this.setReaders(another.getReaders());
    this.setWriter(another.getWriter());
  }

  public boolean removeReMapping(){
    this.setNumberOfReadsReMapping(this.getNumberOfReadsReMapping()+1);
    int currentNumberOfReads = this.getNumberOfReadsReMapping();
    boolean status = false;
	  
    if(currentNumberOfReads % readers.size() == 0)
      status =  this.removeReMapping();
    else
      status = this.peekReMapping();
	  
    return status;
  }

  public void fifoWrite(){
    for(Fifo fifo : this.readers) {
      int new_tokens = fifo.get_tokens()+fifo.getProdRate();
      fifo.set_tokens(new_tokens);
      assert (fifo.get_tokens()<=fifo.get_capacity()): "Error in writing composite fifo!!!";
    }
  }

  public void fifoRead(){
    int new_tokens = this.writer.get_tokens() - this.writer.getConsRate();
    this.writer.set_tokens(new_tokens);
    assert (this.writer.get_tokens()>=0) :  "Error reading composite Fifo!!!";
  }

  public boolean canFlushData(){
    if(this.numberOfReads % readers.size() == 0)
      return true;
    return false; 
  }

  public boolean canBeWritten(){
    for(Fifo reader : readers){
      if(reader.get_capacity() < reader.get_tokens() + reader.getProdRate())
        return false;
    }
    return true;
  }

  public double readTimeProducedToken() {
    Transfer status;
    System.out.println("FIFO: "+this.getName());
    this.numberOfReadsTimeProduced++;
    int currentNumberOfReads = this.numberOfReadsTimeProduced;
	  
    if (currentNumberOfReads % readers.size()==0)
      status = this.removeTimeProducedToken();
    else 
      status = this.peekTimeProducedToken(); 
	  
    return status.getDue_time();
  }

  public boolean isCompositeChannel(){
    return true;
  }

  public void setWriter(Fifo writer){
    this.writer = writer;
  }

  public Fifo getWriter(){
    return this.writer;
  }

  public void setReaders(List<Fifo> readers){
    this.readers = readers;
  }

  public List<Fifo> getReaders(){
    return this.readers;
  }

}
