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

public class Processor {
  private int id;
  private String name;
  private Scheduler scheduler;
  // each processors has a local memory
  private LocalMemory localMemory;


  public Processor(int id, String name) {
    this.setName(name);
    this.setId(id);
    
    scheduler = new FCFS(name);
    scheduler.setNumberIterations(1);
    
    localMemory = new LocalMemory(1,"localMemory");
  }
    
  public Processor(Processor other) {
    this.setName(other.getName());
    this.setId(other.getId());
    this.scheduler = other.scheduler;
  }

  public LocalMemory getLocalMemory(){
    return this.localMemory;
  }

  public voud setLocalMemory(LocalMemory localMemory){
    this.localMemory = localMemory;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void restartProcessor() {
    this.scheduler.restartScheduler();
    this.localMemory.resetMemoryUtilization();
  }

  public int getRunIterations(){
    return scheduler.getRunIterations():
  }

  public Scheduler getScheduler(){
    return this.scheduler;
  }










}
