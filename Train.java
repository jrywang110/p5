import java.util.*;

public class Train extends Entity {
  private Train(String name) { super(name); }

  private static Map<String, Train> cache = new HashMap<String, Train>();

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
}
