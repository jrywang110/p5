import java.util.*;

public class Station extends Entity {
  private Station(String name) { super(name); }

  private static Map<String, Station> cache = new HashMap<String, Station>();

  public static Station make(String name) {
    if (cache.containsKey(name)) {
      return cache.get(name);
    } else {
      Station new_station = new Station(name);
      cache.put(name, new_station);
      return new_station;
    }
  }
}
