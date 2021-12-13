import java.util.*;

public class Passenger extends Entity {
  private Passenger(String name) { super(name); }

  private static Map<String, Passenger> cache = new HashMap<String, Passenger>();

  public static Passenger make(String name) {
    if (cache.containsKey(name)) {
      return cache.get(name);
    } else {
      Passenger new_passenger = new Passenger(name);
      cache.put(name, new_passenger);
      return new_passenger;
    }
  }
}
