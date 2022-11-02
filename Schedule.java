import java.util.List;
import java.util.Map;

interface Schedule{
  public void getSchedulableActors(List<Actor> actors,Map<Integer,Fifo> fifos);
  public void runSchedule(List<Actor> actors,Map<Integer,Fifo> fifos);

}
