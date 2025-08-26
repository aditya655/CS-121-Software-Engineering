import java.util.*;

public class BoardEvent implements Event {
  public final Passenger p; public final Train t; public final Station s;
  public BoardEvent(Passenger p, Train t, Station s) {
    this.p = p; this.t = t; this.s = s;
  }
  public boolean equals(Object o) {
    if (o instanceof BoardEvent e) {
      return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(p, t, s);
  }
  public String toString() {
    return "Passenger " + p + " boards " + t + " at " + s;
  }
  public List<String> toStringList() {
    return List.of(p.toString(), t.toString(), s.toString());
  }
  @Override
  public void replayAndCheck(MBTA mbta) {
    // Check if the train is at the station
    if (t.getCurrentStation() == null || !t.getCurrentStation().equals(s)) {
      throw new IllegalStateException("Train " + t + " is not at station " + s);
    }

    // Check if the passenger is at the station
    if (p.getCurrentStation() == null || !p.getCurrentStation().equals(s)) {
      throw new IllegalStateException("Passenger " + p + " is not at station " + s);
    }

    // Board the train
    t.board(p);
    p.boardTrain(t);
  }
}
