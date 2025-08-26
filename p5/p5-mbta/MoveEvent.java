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
  @Override
  public void replayAndCheck(MBTA mbta) {
    // Check if the train is currently at s1
    if (t.getCurrentStation() == null || !t.getCurrentStation().equals(s1)) {
      throw new IllegalStateException("Train " + t + " is not at the expected station " + s1);
    }

    // Check if the move from s1 to s2 is valid (train lines should have sequential stations)
    List<Station> lineStations = mbta.getLines().get(t.toString());
    int indexS1 = lineStations.indexOf(s1);
    int indexS2 = lineStations.indexOf(s2);

    if (indexS1 == -1 || indexS2 == -1 || Math.abs(indexS1 - indexS2) != 1) {
      throw new IllegalStateException("Invalid move from " + s1 + " to " + s2 + " for train " + t);
    }

    // Perform the move
    t.moveTo(s2);
  }
}
