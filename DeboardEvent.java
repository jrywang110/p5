import java.util.*;

public class DeboardEvent implements Event {
  public final Passenger p; public final Train t; public final Station s;
  public DeboardEvent(Passenger p, Train t, Station s) {
    this.p = p; this.t = t; this.s = s;
  }
  public boolean equals(Object o) {
    if (o instanceof DeboardEvent e) {
      return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(p, t, s);
  }
  public String toString() {
    return "Passenger " + p + " deboards " + t + " at " + s;
  }
  public List<String> toStringList() {
    return List.of(p.toString(), t.toString(), s.toString());
  }
  public void replayAndCheck(MBTA mbta) {
    if (mbta.lines.containsKey(t.toString())) {
      if (mbta.lines.get(t.toString()).containsValue(s)) {
        if (mbta.train_position.get(t.toString()) == s && t.containsPassenger(p)) {
          if (mbta.journeys.get(t.toString())[p.get_index() + 1] == s) {
            t.removePassenger(p);
          } else {
            throw new RuntimeException();
          }
        } else {
          throw new RuntimeException();
        }
      } else {
        throw new RuntimeException();
      }
    } else {
      throw new RuntimeException();
    }
  }
}
