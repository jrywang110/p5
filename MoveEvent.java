import java.util.*;

public class MoveEvent implements Event {
  public final Train t; public final Station s1, s2;
  public MoveEvent(Train t, Station s1, Station s2) {
    this.t = t; this.s1 = s1; this.s2 = s2;
  }
  public boolean equals(Object o) {
    if (o instanceof MoveEvent e) {
      return t.equals(e.t) && s1.equals(e.s1) && s2.equals(e.s2);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(t, s1, s2);
  }
  public String toString() {
    return "Train " + t + " moves from " + s1 + " to " + s2;
  }
  public List<String> toStringList() {
    return List.of(t.toString(), s1.toString(), s2.toString());
  }
  public void replayAndCheck(MBTA mbta) {
    if (mbta.lines.containsKey(t.toString())) {
      if (mbta.lines.get(t.toString()).containsValue(s1) && mbta.lines.get(t.toString()).containsValue(s2)) {
        for (String station_name : mbta.train_position.keySet()) {
          if (mbta.train_position.get(station_name) == s2) {
            throw new RuntimeException();
          }
        }
        mbta.train_position.replace(t.toString(), s2);
      } else {
        throw new RuntimeException();
      }
    } else {
      throw new RuntimeException();
    }
  }
}
