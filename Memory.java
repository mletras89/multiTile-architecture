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
     This class describes a Memory element in the architecture. There exists
     three types of memories:
        - LOCAL_MEM,
        - TILE_LOCAL_MEM
        - GLOBAL_MEM
--------------------------------------------------------------------------
*/

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

public class Memory{
  private int id;
  private String name;
  private int capacity;
  private Map<Double,Double> memoryUtilization = new TreeMap<Double, Double>();
  private MEMORY_TYPE type;
  private Processor embeddedToProcessor;

  public static enum MEMORY_TYPE {
    LOCAL_MEM,
    TILE_LOCAL_MEM,
    GLOBAL_MEM
  }

  // Possible constructors

  // initializing empty memory
  public Memory() {
    this.setName("");
    this.setId(0);
    this.resetMemoryUtilization();
    // assume infinite size of memories if not specificed
    this.setCapacity(Integer.MAX_VALUE);
  }
   // cloning memory
  public Memory(Memory other) {
    this.setName(other.getName());
    this.setId(other.getId());
    this.resetMemoryUtilization();
    this.setCapacity(other.getCapacity());
    this.setType(other.getType());
  }
  // creating memory from given parameters
  public Memory(int id, String name, int capacity){
    this.name = name;
    this.id       = id;
    this.resetMemoryUtilization();
    this.capacity = capacity;
  }

  // creating memory from given parameters
  public Memory(int id, String name){
    this.name = name;
    this.id       = id;
    this.resetMemoryUtilization();
    this.capacity = Integer.MAX_VALUE;
  }
  
  public boolean equals(Memory memory){
    return this.getId() == memory.getId() && this.getName().equals(memory.getName());
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MEMORY_TYPE getType(){
    return this.type;
  }

  public void setType(MEMORY_TYPE type){
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getCapacity() {
    return capacity;
  }
  
  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public void resetMemoryUtilization() {
    // KEY is when and Value is the current utilization
    memoryUtilization.clear();
    this.memoryUtilization.put(0.0, 0.0);
  }
  
  public Map<Double,Double> getMemoryUtilization() {
    return this.memoryUtilization;
  }

  public Processor getEmbeddedToProcessor(){
    return this.embeddedToProcessor;
  }
 
  public void setEmbeddedToProcessor(Processor embeddedToProcessor){
    this.embeddedToProcessor = embeddedToProcessor;
  }

  // methods for memory managing

  public double getUtilization(){
    List<Double> listKeys = new ArrayList<>(memoryUtilization.keySet());
    Collections.sort(listKeys);
    double last_inserted_key = listKeys.get(listKeys.size()-1);
    double maxUtilization = last_inserted_key * capacity;

    double util = 0;
    for (int i=0;i<listKeys.size()-1;i++) {
      util += (listKeys.get(i+1) - listKeys.get(i)) * memoryUtilization.get(listKeys.get(i));
      //System.out.println("First square from"+listKeys.get(i)+" to "+listKeys.get(i+1)+" is: "+(listKeys.get(i+1) - listKeys.get(i)) * memoryUtilization.get(listKeys.get(i)));
    }
    return 1 - (maxUtilization - util)/maxUtilization;
  }

  public void writeDataInMemory(int amountBytes, double when) {
    List<Double> listKeys = new ArrayList<>(memoryUtilization.keySet());
    double last_inserted_key = listKeys.get(listKeys.size()-1);
    // get current amount of bytes
    double currentBytes = memoryUtilization.get(last_inserted_key);
    System.err.println("Writing memory "+this.getName()+ " storing "+currentBytes+" writing "+amountBytes+" at "+when);
    assert this.getCapacity() >= currentBytes+amountBytes;
    // I can only insert events from the last insert element, no insertions in the past
    assert last_inserted_key <= when;
    memoryUtilization.put(when, currentBytes+amountBytes);
  }

  public void readDataInMemory(int amountBytes, double when) {
    List<Double> listKeys = new ArrayList<>(memoryUtilization.keySet());
    double last_inserted_key = listKeys.get(listKeys.size()-1);
    // get current amount of bytes
    double currentBytes = memoryUtilization.get(last_inserted_key);
    System.err.println("Reading memory "+this.getName()+ " storing "+currentBytes+" reading "+amountBytes+" at "+when);
    assert currentBytes-amountBytes >= 0;
    // I can only insert events from the last insert element, no insertions in the past
    assert last_inserted_key <= when;
    memoryUtilization.put(when, currentBytes-amountBytes);
  }

  public boolean canPutDataInMemory(int amountBytes) {
    List<Double> listKeys = new ArrayList<>(memoryUtilization.keySet());
    double last_inserted_key = listKeys.get(listKeys.size()-1);
    // get current amount of bytes
    double currentBytes = memoryUtilization.get(last_inserted_key);
    //System.err.println("Storing "+currentBytes+" amount bytes "+amountBytes);
    if (currentBytes + amountBytes <= this.getCapacity())
            return true;
    return false;
  }

  // DUMPING the memory utilzation locally
  public void saveMemoryUtilizationStats(String path) throws IOException{
    try{
        File memUtilStatics = new File(path+"/memory-utilization-"+this.getName()+".csv");
        if (memUtilStatics.createNewFile()) {
          System.out.println("File created: " + memUtilStatics.getName());
        } else {
          System.out.println("File already exists.");
        }
    }
    catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }

    FileWriter myWriter = new FileWriter(path+"/memory-utilization-"+this.getName()+".csv"); 
    myWriter.write("Memory\tWhen\tCapacity\n");

    saveMemoryUtilizationStats(myWriter);

    myWriter.close();
  }

  public void saveMemoryUtilizationStats(FileWriter myWriter) throws IOException{
    Map<Double,Double> memoryUtilization = this.getMemoryUtilization();
    List<Double> listKeys = new ArrayList<>(memoryUtilization.keySet());
    
    for (double element : listKeys) {
      myWriter.write(this.getName()+"\t"+element+"\t"+memoryUtilization.get(element)+"\n");
    }
  }

}
