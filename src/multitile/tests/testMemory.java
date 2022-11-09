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

public class testMemory {
    public static void main(String[] args) throws IOException {
      System.out.println("Testing class!");
      Memory memoryTest = new Memory(1,"Memory_1",100);
      Memory memoryTest2 = new Memory(2,"Memory_2",200);
      Memory memoryTest3 = new Memory(3,"Memory_3",100);

      //memoryTest.writeDataInMemory(50,0);
      //memoryTest.readDataInMemory(40,50);
      //memoryTest.writeDataInMemory(50,100);
      //memoryTest.readDataInMemory(23,200);
      //
      //if (memoryTest.canPutDataInMemory(100))
      //  System.out.println("I can put 100 more");
      //else
      //  System.out.println("I can not put 100 more"); 

      //if (memoryTest.canPutDataInMemory(63))
      //  System.out.println("I can put 63 more");
      //else
      //  System.out.println("I can not put 63 more"); 

      //memoryTest.writeDataInMemory(63,250);
      
      memoryTest.writeDataInMemory(100,225);
      memoryTest.readDataInMemory(100,300);

      memoryTest2.writeDataInMemory(200,150);
      memoryTest2.readDataInMemory(200,300);

      memoryTest3.writeDataInMemory(100,150);
      memoryTest3.readDataInMemory(100,300);

      //memoryTest.saveMemoryUtilizationStats(".");

      System.out.println("The memory 1 utilization was: "+memoryTest.getUtilization());
      System.out.println("The memory 2 utilization was: "+memoryTest2.getUtilization());
      System.out.println("The memory 3 utilization was: "+memoryTest3.getUtilization());

      try{
          File memUtilStatics = new File("exampleUtil.csv");
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

      FileWriter myWriter = new FileWriter("exampleUtil.csv"); 
      myWriter.write("Memory\tWhen\tCapacity\n");

      memoryTest.saveMemoryUtilizationStats(myWriter);
      memoryTest2.saveMemoryUtilizationStats(myWriter);
      memoryTest3.saveMemoryUtilizationStats(myWriter);

      myWriter.close();

    }
}

