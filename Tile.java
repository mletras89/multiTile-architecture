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
     This class describes an Tile in the architecture. Each Tile contains:
        - processors: a list of processors in the tile.
        - crossbar: crossbar that communicate the processors
        - tileLocalMemory: is the memory local to this tile
--------------------------------------------------------------------------
*/

public class Tile{
  private int id;
  private String name;
  private int numberProcessors;
  private List<Processor> processors;
  private Crossbar crossbar;
  private TileLocalMemory tileLocalMemory;
  private int totalIterations;
  
  public Tile(){
    this.id = 1;
    this.name = "Tile1";
    this.numberProcessors = 1;
    this.processors = new ArrayList<>();
    for(int i=0; i<this.numberProcessors;i++){
      Processor processor = new Processor(i,"Processor"+i);
      processors.add(processor);
      crossbar = new Crossbar(1,"crossbar_"+this.name, 1,2);
    }
    tileLolcaMemory = new TileLocalMemory(1,"TileLocalMemory_"+this.name);
  }

  public Tile(int id,String name,int numberProcessors){
    this.id = id;
    this.name = name;
    this.numberProcessors = numberProcessors;
    this.processors = new ArrayList<>();
    for(int i=0; i<this.numberProcessors;i++){
      Processor processor = new Processor(i,"Processor"+i);
      processors.add(processor);
      crossbar = new Crossbar(1,"crossbar_"+this.name, 1,2);
    }
    tileLocalMemory = new TileLocalMemory(1,"TileLocalMemory_"+this.name);
    this.totalIterations = 2;
  }

  public void runTile(List<Actor> actors, Map<Integer,Fifo> fifoMap,  int numberInterations){
    this.resetTile();
    runIterations = 0;
    while(runIterations < totalIterations()){
      for(int i =0 ; i < numberProcessors; i++){
          
      } 
    }



  }

  public int getRunIterations(){
    int max = 0 ;
    for(int i=0;i<numberProcessors;i++){
      if (max < processors.get(i).getRunIterations())
        max = processors.get(i).getRunIterations();
    }
    return max;
  }

  public void resetTile(){
    // first reset the processors
    for(int i=0;i<numberProcessors;i++){
      processors.get(i).restartProcessor();
    }
    // refresh the tile local memory
    tileLocalMemory.resetMemoryUtilization();
    // restart the crossbar
    crossbar.restartCrossbar();
  }

}
