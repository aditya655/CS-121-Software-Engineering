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
  @Override
  public void replayAndCheck(MBTA mbta) {
    // Check if the train is at the station
    if (t.getCurrentStation() == null || !t.getCurrentStation().equals(s)) {
      throw new IllegalStateException("Train " + t + " is not at station " + s);
    }

    // Check if the passenger is on the train
    if (!t.hasPassenger(p)) {
      throw new IllegalStateException("Passenger " + p + " is not on train " + t);
    }

    // Deboard the train
    t.deboard(p);
    p.deboardTrain(s);
  }
}
