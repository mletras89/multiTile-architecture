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
     This class describes a communication channel implemented as a FIFO 
     buffer. Channels communicate actors in an application graph.
--------------------------------------------------------------------------
*/
package src.multitile.application;

import src.multitile.Transfer;
import src.multitile.architecture.Memory;
import java.util.*;

public class Fifo implements Buffer{
  private int             id;
  private String          name;
  private int             tokens;    // current number of tokens
  private int             initial_tokens; // the number of initial tokens
  private int             capacity;  // maximal number of tokens
  private int             tokenSize; // token size in bytes
  private int             consRate;
  private int             prodRate;
  private Queue<Transfer> timeProducedToken;  // each transfer transport a token, thus the due time is the produced token time
  private Memory mapping;   // map to memory 

  private Actor source; // source actor
  private Actor destination; // destination actor

  private Queue<Boolean> ReMapping; 
  private int numberOfReadsReMapping; 

  public int numberOfReadsTimeProduced;
  public int numberOfReads;

  private Vector<Integer> memory_footprint;

  public Fifo(int id, String name, int tokens, int capacity, int tokenSize,Memory mapping,int consRate, int prodRate, Actor src, Actor dst){
    this.id                          = id;
    this.name                        = name;
    this.tokens                      = tokens;
    this.capacity                    = capacity;
    this.setTokenSize(tokenSize);
    this.setMapping(mapping);
    this.initial_tokens              = tokens;
    this.setConsRate(consRate);
    this.setProdRate(prodRate);
    this.setSource(src);
    this.setDestination(dst);
    this.setNumberOfReadsTimeProduced(0);
    this.timeProducedToken = new LinkedList<>();
    this.numberOfReads = 0;
    this.ReMapping = new LinkedList<>();
    this.setNumberOfReadsReMapping(0);
  }

  public Fifo(int id, String name, int tokens, int capacity, int tokenSize,Memory mapping,int consRate, int prodRate){
    this.id                          = id;
    this.name                        = name;
    this.tokens                      = tokens;
    this.capacity                    = capacity;
    this.setTokenSize(tokenSize);
    this.setMapping(mapping);
    this.initial_tokens              = tokens;
    this.setConsRate(consRate);
    this.setProdRate(prodRate);

    this.setNumberOfReadsTimeProduced(0);
    this.timeProducedToken = new LinkedList<>();
    this.numberOfReads = 0;

    this.ReMapping = new LinkedList<>();
    this.setNumberOfReadsReMapping(0);
  }


  public Fifo(Fifo another){
    this.id                          = another.getId();
    this.name                        = another.getName();
    this.tokens                      = another.get_tokens();
    this.capacity                    = another.get_capacity();
    this.setTokenSize(another.getTokenSize());
    this.setMapping(another.getMapping());
    this.initial_tokens              = another.initial_tokens;
    this.setConsRate(another.getConsRate());
    this.setProdRate(another.getProdRate());

    this.setSource(another.getSource());
    this.setDestination(another.getDestination());

    this.timeProducedToken = new LinkedList<>();
    this.setNumberOfReadsTimeProduced(another.getNumberOfReadsTimeProduced());
    this.numberOfReads = 0;
    this.setNumberOfReadsReMapping(another.getNumberOfReadsReMapping());
    this.ReMapping = new LinkedList<>();

  }
  
  public void setNumberOfReadsReMapping(int numberOfReads) {
    this.numberOfReadsReMapping = numberOfReads;
  }

 public int getNumberOfReadsReMapping(){
    return this.numberOfReadsReMapping;
 }

  public void insertReMapping(boolean value) {
    this.ReMapping.add(value);
  }

  public boolean peekReMapping(){
    return this.ReMapping.peek();
  }


  public Transfer removeTimeProducedToken(){
    return this.timeProducedToken.remove();
  }

  public Transfer peekTimeProducedToken(){
    return this.timeProducedToken.peek();
  }

  public boolean equals(Fifo fifo){
    return this.getId()==fifo.getId() && this.getName().equals(fifo.getName());
  }

  public boolean fifoCanBeWritten(){
    if(this.get_capacity() < this.get_tokens() + this.getProdRate())
      return false;
    return true;
  }

  public boolean fifoCanBeRead(){
    if(this.get_tokens() - this.getConsRate() < 0)
      return false; 
    return true;
  }

  public void insertTimeProducedToken(Transfer transfer) {
	  this.timeProducedToken.add(new Transfer(transfer));
  }
 
  public double readTimeProducedToken(int n){
    // this method reads n tokens from the fifo and returns the one with
    // the max delay
    List<Double> reads = new ArrayList<>();
    for(int i=0;i<n;i++){
      reads.add(this.readTimeProducedToken());
    }
    return Collections.max(reads);
  }

  public double readTimeProducedToken() {
    Transfer status;
    this.numberOfReadsTimeProduced++;
    int currentNumberOfReads = this.numberOfReadsTimeProduced;
    
    status = this.timeProducedToken.remove();
    return status.getDue_time();
  }
  
 
  public boolean canFlushData() {
    return true;
  }
  
  public void increaseNumberOfReads() {
    this.numberOfReads++;
  }
  
  public boolean removeReMapping() {
    this.numberOfReadsReMapping++;
    int currentNumberOfReads = this.numberOfReadsReMapping;
    boolean status = false;
	  
    status = this.ReMapping.remove();
    return status;
  }
  
  public void reset_Fifo(){
    this.tokens = this.initial_tokens;
    this.memory_footprint.clear();
    this.memory_footprint.add(this.initial_tokens);
  }

  public void update_memory_footprint(){
    this.memory_footprint.add(tokens);
  }
  
  public String getName() {
    return this.name;
  }
  
  public int get_capacity(){
    return this.capacity;
  }

  public void set_capacity(int capacity){
    this.capacity = capacity;
  }

  public int get_tokens(){
    return this.tokens;
  }

  public void set_tokens(int tokens){
    this.tokens = tokens;
  }

  public int getTokenSize() {
    return tokenSize;
  }

  public void setTokenSize(int tokenSize) {
    this.tokenSize = tokenSize;
  }

  public Memory getMapping() {
    return mapping;
  }

  public void setMapping(Memory mapping) {
    this.mapping = mapping;
  }

  public int getConsRate() {
    return consRate;
  }

  public void setConsRate(int consRate) {
    this.consRate = consRate;
  }

  public int getProdRate() {
    return prodRate;
  }

  public void setProdRate(int prodRate) {
    this.prodRate = prodRate;
  }

  public int getId() {
    return this.id;
  }

  public boolean isCompositeChannel(){
      return false;
  }

  public Actor getSource() {
    return source;
  }

  public void setSource(Actor source) {
    this.source = source;
  }

  public Actor getDestination() {
    return destination;
  }

  public void setDestination(Actor destination) {
    this.destination = destination;
  }

  public Queue<Boolean> getReMapping() {
    return ReMapping;
  }

  public void setReMapping(Queue<Boolean> reMapping) {
    ReMapping = reMapping;
  }

  public int getNumberOfReadsTimeProduced() {
    return numberOfReadsTimeProduced;
  }

  public void setNumberOfReadsTimeProduced(int numberOfReadsTimeProduced) {
    this.numberOfReadsTimeProduced = numberOfReadsTimeProduced;
  }

}
