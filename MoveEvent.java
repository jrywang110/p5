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
    try {
      if (!mbta.lines.containsKey(t.toString())) {
        throw new RuntimeException();
      }

      List<Station> stationList = mbta.lines.get(t.toString());

      if (!stationList.contains(s1) || !stationList.contains(s2)) {
        throw new RuntimeException();
      }

      int startIndex = stationList.indexOf(s1);
      int endIndex = stationList.indexOf(s2);
      int maxIndex = stationList.size() - 1; 

      if (mbta.train_position.get(t.toString()) != stationList.get(startIndex)) {
        throw new RuntimeException();
      }

      mbta.train_position.replace(t.toString(), null);

      while (startIndex != endIndex) {
        if ((startIndex == maxIndex && t.isRight()) || (startIndex == 0 && !t.isRight())) {
          t.changeDir();
        }

        if (t.isRight()) {
          startIndex += 1;
          for (String trainName : mbta.train_position.keySet()) {
            if (mbta.train_position.get(trainName) == stationList.get(startIndex)) {
              throw new RuntimeException();
            }
          }
        } else {
          startIndex -= 1;
          for (String trainName : mbta.train_position.keySet()) {
            if (mbta.train_position.get(trainName) == stationList.get(startIndex)) {
              throw new RuntimeException();
            }
          }
        }
      }

      mbta.train_position.replace(t.toString(), s2);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
