import java.util.*;

import com.google.gson.*;
import java.io.*;   
import java.nio.file.*;

public class MBTA {

  // Creates an initially empty simulation
  public MBTA() { }

  public Map<String, List<Station>> lines = new HashMap<String, List<Station>>();
  public Map<String, List<Station>> journeys = new HashMap<String, List<Station>>();

  // Adds a new transit line with given name and stations
  public void addLine(String name, List<String> stations) {
    if (!lines.containsKey(name)) {
      List<Station> station_list = new ArrayList<Station>(); 
      for (String station : stations) {
        station_list.add(Station.make(station));
      } 
      lines.put(name, station_list);
    }
  }

  // Adds a new planned journey to the simulation
  public void addJourney(String name, List<String> stations) {
    if (!journeys.containsKey(name)) {
      List<Station> station_list = new ArrayList<Station>(); 
      for (String station : stations) {
        station_list.add(Station.make(station));
      } 
      journeys.put(name, station_list);
    }
  }

  // Return normally if initial simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkStart() {
  }

  // Return normally if final simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkEnd() {
  }

  // reset to an empty simulation
  public void reset() {
    lines.clear();
    journeys.clear();
  }

  // adds simulation configuration from a file
  public void loadConfig(String filename) {
    try {
      Gson gson = new Gson();

      Reader reader = Files.newBufferedReader(Paths.get(filename));

      MbtaJSON json = gson.fromJson(reader, MbtaJSON.class);

      for (String k : json.lines.keySet()) {
        addLine(k, json.lines.get(k));
      }

      for (String j : json.trips.keySet()) {
        addJourney(j, json.trips.get(j));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
