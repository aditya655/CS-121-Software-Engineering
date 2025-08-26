import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Passenger extends Entity {
  private static Map<String, Passenger> cache = new HashMap<>();
  private Station currentStation;
  private Train currentTrain;  // Add this field to store the current train
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();

  private Passenger(String name) { 
    super(name);
  }

  public static Passenger make(String name) {
    if (!cache.containsKey(name)) {
      cache.put(name, new Passenger(name));
    }
    return cache.get(name);
  }

  public void moveTo(Station station) {
    lock.lock();  // Synchronize to avoid race conditions
    try {
        this.currentStation = station;  // Update the passenger's current station
        this.currentTrain = null;       // Clear the current train if moving to a station
    } finally {
        lock.unlock();
    }
}


  public Station getCurrentStation() {
    return currentStation;
  }

  public ReentrantLock getLock() {
    return lock;
  }

  public Condition getCondition() {
    return condition;
  }

  public Train getCurrentTrain() {
    return currentTrain;
  }

  public void setCurrentTrain(Train train) {
    this.currentTrain = train;
  }

  public void boardTrain(Train train) {
    lock.lock();
    try {
      this.currentTrain = train;
      this.currentStation = null;  // Passenger is no longer at the station
    } finally {
      lock.unlock();
    }
  }

  public void deboardTrain(Station station) {
    lock.lock();
    try {
      this.currentTrain = null;
      this.currentStation = station;  // Passenger is now at the station
    } finally {
      lock.unlock();
    }
  }
}
