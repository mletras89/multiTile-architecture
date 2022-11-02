import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class testCrossbar {
    public static void main(String[] args) throws IOException {
      System.out.println("Testing crossbar!");

      Memory memoryTest1 = new Memory(1,"Memory_1",100);
      memoryTest1.setType(Memory.MEMORY_TYPE.TILE_LOCAL_MEM);

      Actor actor1 = new Actor("actor1");
      Fifo fifo1 = new Fifo(1,"fifo1",0,2,1000000,memoryTest1,1,1,actor1,actor1);
      Transfer  t1 = new Transfer(actor1,fifo1);
      t1.setStart_time(100);
      t1.setType(Transfer.TRANSFER_TYPE.WRITE);

      Actor actor2 = new Actor("actor2");
      Fifo fifo2 = new Fifo(1,"fifo1",0,2,1000000,memoryTest1,1,1,actor1,actor1);
      Transfer  t2 = new Transfer(actor2,fifo2);
      t2.setStart_time(100);
      t2.setType(Transfer.TRANSFER_TYPE.WRITE);

      Actor actor3 = new Actor("actor3");
      Fifo fifo3 = new Fifo(1,"fifo1",0,2,1000000,memoryTest1,1,1,actor1,actor1);
      Transfer  t3 = new Transfer(actor3,fifo3);
      t3.setStart_time(100);
      t3.setType(Transfer.TRANSFER_TYPE.WRITE);

      Actor actor4 = new Actor("actor4");
      Fifo fifo4 = new Fifo(1,"fifo1",0,2,1000000,memoryTest1,1,1,actor1,actor1);
      Transfer  t4 = new Transfer(actor4,fifo4);
      t4.setStart_time(100);
      t4.setType(Transfer.TRANSFER_TYPE.READ);

      Actor actor5 = new Actor("actor5");
      Actor actor6 = new Actor("actor6");
      Actor actor7 = new Actor("actor5");
      Actor actor8 = new Actor("actor6");

      Crossbar crossbar1 = new Crossbar(1,"Crossbar",1,4);
      crossbar1.insertTransfer(t1);
      crossbar1.insertTransfer(t2);
      crossbar1.insertTransfer(t3);
      crossbar1.insertTransfer(t4);
      crossbar1.insertTransfer(t4);
      crossbar1.insertTransfer(t4);
      crossbar1.insertTransfer(t4);

      Transfer  t5 = new Transfer(actor1,fifo1);
      t5.setStart_time(2000);
      t5.setType(Transfer.TRANSFER_TYPE.WRITE);
      crossbar1.insertTransfer(t5);
      crossbar1.insertTransfer(t4);
      crossbar1.insertTransfer(t4);
      crossbar1.insertTransfer(t4);
      crossbar1.commitTransfersinQueue();
      crossbar1.saveCrossbarUtilizationStats(".");

      System.out.println("Finishing testing crossbar!");
//      Memory memoryTest = new Memory(1,"Memory_1",100);
//      Memory memoryTest2 = new Memory(2,"Memory_2",200);
//      Memory memoryTest3 = new Memory(3,"Memory_3",100);
// 
//      memoryTest.writeDataInMemory(100,225);
//      memoryTest.readDataInMemory(100,300);
//
//      memoryTest2.writeDataInMemory(200,150);
//      memoryTest2.readDataInMemory(200,300);
//
//      memoryTest3.writeDataInMemory(100,150);
//      memoryTest3.readDataInMemory(100,300);
//
//      //memoryTest.saveMemoryUtilizationStats(".");
//
//      System.out.println("The memory 1 utilization was: "+memoryTest.getUtilization());
//      System.out.println("The memory 2 utilization was: "+memoryTest2.getUtilization());
//      System.out.println("The memory 3 utilization was: "+memoryTest3.getUtilization());
//
//      try{
//          File memUtilStatics = new File("exampleUtil.csv");
//          if (memUtilStatics.createNewFile()) {
//            System.out.println("File created: " + memUtilStatics.getName());
//          } else {
//            System.out.println("File already exists.");
//          }
//      }
//      catch (IOException e) {
//          System.out.println("An error occurred.");
//          e.printStackTrace();
//      }
//
//      FileWriter myWriter = new FileWriter("exampleUtil.csv"); 
//      myWriter.write("Memory\tWhen\tCapacity\n");
//
//      memoryTest.saveMemoryUtilizationStats(myWriter);
//      memoryTest2.saveMemoryUtilizationStats(myWriter);
//      memoryTest3.saveMemoryUtilizationStats(myWriter);
//
//      myWriter.close();
//
    }
}

