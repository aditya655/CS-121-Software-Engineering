import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Train extends Entity {
  private static Map<String, Train> cache = new HashMap<>();
  private Station currentStation;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();
  private final List<Passenger> passengers = new ArrayList<>();

  private Train(String name) {
    super(name);
  }

  public static synchronized Train make(String name) {
    if (!cache.containsKey(name)) {
      cache.put(name, new Train(name));
    }
    return cache.get(name);
  }
  public void moveTo(Station station) {
    lock.lock();  // Synchronize to avoid race conditions
    try {
        this.currentStation = station;
    
    } finally {
        lock.unlock();
    }
}

  public Station getCurrentStation() {
    lock.lock();
    try {
      return currentStation;
    } finally {
      lock.unlock();
    }
  }

  public ReentrantLock getLock() {
    return lock;
  }

  public Condition getCondition() {
    return condition;
  }

  public void board(Passenger passenger) {
    lock.lock();
    try {
      passengers.add(passenger);
      condition.signalAll();
    } finally {
      lock.unlock();
    }
  }

  public void deboard(Passenger passenger) {
    lock.lock();
    try {
      passengers.remove(passenger);
      condition.signalAll();
    } finally {
      lock.unlock();
    }
  }

  public boolean hasPassenger(Passenger passenger) {
    lock.lock();
    try {
      return passengers.contains(passenger);
    } finally {
      lock.unlock();
    }
  }

  public void awaitStation(Station station) throws InterruptedException {
    lock.lock();
    try {
      while (!currentStation.equals(station)) {
        condition.await();  // Wait until the train is at the desired station
        System.out.println("Train " + this + " waiting to reach " + station);
      }
      System.out.println("Train " + this + " reached " + station);
    } finally {
      lock.unlock();
    }
  }
  
}
