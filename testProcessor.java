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

public class testProcessor {
    public static void main(String[] args) throws IOException {
      System.out.println("Testing processor!");

      Actor actor1 = new Actor("actor1");
      actor1.setExecutionTime(10000);
      Action a1 = new Action(actor1);
      Actor actor2 = new Actor("actor2");
      actor2.setExecutionTime(10000);
      Action a2 = new Action(actor2);
      Actor actor3 = new Actor("actor3");
      actor3.setExecutionTime(10000);
      Action a3 = new Action(actor3);
      Actor actor4 = new Actor("actor4");
      actor4.setExecutionTime(10000);
      Action a4 = new Action(actor4);
      Actor actor5 = new Actor("actor5");
      Actor actor6 = new Actor("actor6");
      Actor actor7 = new Actor("actor5");
      Actor actor8 = new Actor("actor6");

      Processor cpu1 = new Processor(1,"cpu1");
      
      cpu1.insertAction(a1);
      cpu1.insertAction(a2);
      cpu1.insertAction(a3);
      cpu1.insertAction(a4);

      cpu1.commitActionsinQueue();
      cpu1.saveProcessorUtilizationStats(".");

      System.out.println("Finishing testing crossbar!");
    }
}

