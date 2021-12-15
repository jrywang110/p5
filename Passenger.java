import java.util.*;

public class Passenger extends Entity {
  private Passenger(String name) { super(name); }

  private static Map<String, Passenger> cache = new HashMap<String, Passenger>();

  private Station curr_station; 
  private int index = 0;
  private boolean boarded = false;

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
    return curr_station;
  }

  public void update_station(Station station) {
    curr_station = station;
  }

  public int get_index() {
    return index;
  }

  public void updateIndex() {
    index += 1;
  }

  public boolean isBoarded() {
    return boarded;
  }

  public void onBoard() {
    boarded = true;
  }

  public void deBoard() {
    boarded = false;
  }
}
