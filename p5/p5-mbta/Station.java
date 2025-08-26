import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Station extends Entity {
  private static Map<String, Station> cache = new HashMap<>();
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();
  private Train train;

  private Station(String name) {
    super(name);
  }

  public static synchronized Station make(String name) {
    if (!cache.containsKey(name)) {
      cache.put(name, new Station(name));
    }
    return cache.get(name);
  }

  public boolean hasTrain() {
    lock.lock();
    try {
      return train != null;
    } finally {
      lock.unlock();
    }
  }

  public Train getTrain() {
    lock.lock();
    try {
      return train;
    } finally {
      lock.unlock();
    }
  }

  public void setTrain(Train train) {
    lock.lock();
    try {
      this.train = train;
      condition.signalAll();  // Signal all waiting threads
    } finally {
      lock.unlock();
    }
  }

  public void awaitTrain() throws InterruptedException {
    lock.lock();
    try {
      while (train == null) {
        condition.await();  // Wait until a train arrives
        System.out.println("Waiting for train at " + this);
      }
      System.out.println("Train arrived at " + this);
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
}
