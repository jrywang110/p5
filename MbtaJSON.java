import java.util.*;
import com.google.gson.*;
import org.junit.*;

public class MbtaJSON {
    public Map<String, List<String>> lines;
    public Map<String, List<String>> trips;

    @Test
    public void test1() {
        // Gson gson = new Gson();
        // MbtaJSON c = new MbtaJSON();
        // String[] array = {"h", "i", "v"};

        // c.lines = Map.of("j1", Arrays.asList(array), "j2", Arrays.asList(array));
        // c.trips = Map.of("k1", Arrays.asList(array), "k2", Arrays.asList(array));
        // String s = gson.toJson(c);
        // System.out.println(s);
  
        // MbtaJSON c2 = gson.fromJson(s, MbtaJSON.class);
        // System.out.println(c2.lines);
        // System.out.println(c2.trips);
        MBTA mbta = new MBTA();
        mbta.loadConfig("sample.json");
    }
}
