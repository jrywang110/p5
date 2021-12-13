import java.util.*;

import com.google.gson.*;
import java.io.*;   
import java.nio.file.*;

public class MBTA {

  // Creates an initially empty simulation
  public MBTA() { }

  public Map<String, List<Station>> lines = new HashMap<String, List<Station>>();
  public Map<String, List<Station>> journeys = new HashMap<String, List<Station>>();
  public Map<String, Station> train_position = new HashMap<String, Station>();

  // Adds a new transit line with given name and stations
  public void addLine(String name, List<String> stations) {
    if (!lines.containsKey(name)) {
      List<Station> station_list = new ArrayList<Station>(); 
      for (String station : stations) {
        station_list.add(Station.make(station));
      } 
      lines.put(name, station_list);
      Train.make(name);
      train_position.put(name, station_list[0]);
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
      Passenger.make(name).update_station(station_list[0]);
    }
  }

  // Return normally if initial simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkStart() {
    for (String train : train_position.keySet()) {
      if (train_position.get(train) != lines.get(train)[0]) {
        throw new RuntimeException();
      }
    }

    for (String p : journeys.keySet()) {
      if (Passenger.make(p).get_station() != journeys.get(p)[0]) {
        throw new RuntimeException();
      }
    }
  }

  // Return normally if final simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkEnd() {
    for (String p : journeys.keySet()) {
      if (Passenger.make(p).get_station() != journeys.get(p)[journeys.get(p).size() - 1]) {
        throw new RuntimeException();
      }
    }
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
