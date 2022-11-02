

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
