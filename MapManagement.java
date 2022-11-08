import java.util.*;

public class MapManagement{
  public static boolean isActorIdinMap(Set<Actor> mapKeys, int id){
    if (mapKeys.isEmpty())
      return false;
    for(Actor key: mapKeys){
      if(key.getId() == id)
        return true;
    }
    return false;
  }
}
