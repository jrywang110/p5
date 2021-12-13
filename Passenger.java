import java.util.*;

public class Passenger extends Entity {
  private Passenger(String name) { super(name); }

  private static Map<String, Passenger> cache = new HashMap<String, Passenger>();

  private Station curr_station; 
  private int index = 0;

  public static Passenger make(String name) {
    if (cache.containsKey(name)) {
      return cache.get(name);
    } else {
      Passenger new_passenger = new Passenger(name);
      cache.put(name, new_passenger);
      return new_passenger;
    }
  }

  public Station get_station() {
    return this.curr_station;
  }

  public void update_station(Station station) {
    this.curr_station = station;
  }

  public int get_index() {
    return index;
  }
}
