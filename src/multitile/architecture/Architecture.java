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
  @date   07 December 2022
  @version 1.1
  @ brief
     This class describes an architecture. Each architecture contains:
        - tiles: a map of tiles in the architecture
        - NoC: NoC that communicates the tiles
        - globalMemory: is the memory global to all the tiles
--------------------------------------------------------------------------
*/
package src.multitile.architecture;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.Queue;
import java.util.*;

public class Architecture{
  // the key is the id
  private String name;
  private HashMap<Integer,Tile> tiles;
  private NoC noc;
  private GlobalMemory globalMemory;
  
  public Architecture(String name){
    this.name = name;
    // creaate one tile in the architecture
    Tile t1 = new Tile("Tile1",4,1.0,2);
    
    tiles = new HashMap<>();	
    tiles.put(t1.getId(),t1);
    
    globalMemory = new GlobalMemory("GlobalMemory");
  }

  public Architecture(String name, int nTiles, int nProcPerTile, double BWCrossbars, int channelsCrossbar){
    this.name = name;
    tiles = new HashMap<>(); 
    for(int i=0; i < nTiles; i++){
      Tile t = new Tile("Tile"+(i+1), nProcPerTile, BWCrossbars, channelsCrossbar);
      tiles.put(t.getId(),t);  
    }
    globalMemory = new GlobalMemory("GlobalMemory");
  }

  public Architecture(String name, String nameTile, int nProcPerTile, double BWCrossbars, int channelsCrossbar){
    // for test purposes, single tile with name tile
    this.name = name;
    tiles = new HashMap<>(); 

    Tile t = new Tile(nameTile, nProcPerTile, BWCrossbars, channelsCrossbar);
    tiles.put(t.getId(),t);  

    globalMemory = new GlobalMemory("GlobalMemory");
  }
  
  public String getName(){
  	return this.name;
  }
  
  public void setName(String name){
  	this.name = name;
  }
  
  public HashMap<Integer,Tile> getTiles(){
  	return this.tiles;
  }
  
  public GlobalMemory getGlobalMemory(){
  	return this.globalMemory;
  }
  
  public void resetArchitecture(){
    for(HashMap.Entry<Integer,Tile> t: tiles.entrySet()){
      t.getValue().resetTile();
    }
    // refresh the global memory
    this.globalMemory.resetMemoryUtilization();
  }

  public void printArchitectureState(){
    for(HashMap.Entry<Integer,Tile> t: this.tiles.entrySet()){
      for(HashMap.Entry<Integer,Processor> p : t.getValue().getProcessors().entrySet()){
        p.getValue().getLocalMemory().printMemoryState();
      }
      t.getValue().getTileLocalMemory().printMemoryState();
    }
  }

}
