public class TileLocalMemory extends Memory{
  private Processor embeddedToProccessor;

  public TileLocalMemory(){
    super();
    this.setType(MEMORY_TYPE.TILE_LOCAL_MEM);
  }

  public TileLocalMemory(LocalMemory other){
    super(other);
  }

  public TileLocalMemory(int id, String name, int capacity){
    super(id,name,capacity);
    this.setType(MEMORY_TYPE.TILE_LOCAL_MEM);
    this.embeddedToProcessor = null;
  }

  public TileLocalMemory(int id, String name){
    super(id,name);
    this.setType(MEMORY_TYPE.TILE_LOCAL_MEM);
    this.embeddedToProcessor = null;
  }

  public Processor getEmbeddedToProcessor(){
    return this.embeddedToProcessor;
  }

  }

}
