import java.util.*;

public class Train extends Entity {
  private Train(String name) { super(name); }

  private static Map<String, Train> cache = new HashMap<String, Train>();

  private List<Passenger> passengers = new ArrayList<Passenger>();

  public static Train make(String name) {
    // Change this method!
    if (cache.containsKey(name)) {
      return cache.get(name);
    } else {
      Train new_train = new Train(name);
      cache.put(name, new_train);
      return new_train;
    }
  }

  public void addPassenger(Passenger p) {
    passengers.add(p);
  }

  public boolean containsPassenger(Passenger p) {
    return passengers.contains(p);
  }

  public void removePassenger(Passenger p) {
    passengers.remove(p);
  }
}
