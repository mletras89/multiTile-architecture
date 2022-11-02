public class LocalMemory extends Memory{
  private Processor embeddedToProccessor;

  public LocalMemory(){
    super();
    this.setType(MEMORY_TYPE.LOCAL_MEM);
  }

  public LocalMemory(LocalMemory other){
    super(other);
  }

  public LocalMemory(int id, String name, int capacity){
    super(id,name,capacity);
    this.setType(MEMORY_TYPE.LOCAL_MEM);
  }

  public LocalMemory(int id, String name){
    super(id,name);
    this.setType(MEMORY_TYPE.LOCAL_MEM);
  }

  public Processor getEmbeddedToProcessor(){
    return this.embeddedToProcessor;
  }

  public void setEmbeddedToProcessor(Processor embeddedToProccessor){
    this.embeddedToProccessor = embeddedToProccessor;
  }

}
