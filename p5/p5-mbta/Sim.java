import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Sim {

  public static void run_sim(MBTA mbta, Log log) {
    List<Thread> threads = new ArrayList<>();

    // Create and start train threads
    for (String lineName : mbta.getLines().keySet()) {
      Train train = Train.make(lineName);
      Thread trainThread = new Thread(() -> {
        List<Station> stations = mbta.getLines().get(lineName);
        int direction = 1; // 1 for forward, -1 for backward
        int index = 0;

        while (true) {
          Station currentStation = stations.get(index);
          Station nextStation = stations.get((index + direction + stations.size()) % stations.size());

          currentStation.getLock().lock();
          try {
            train.moveTo(nextStation);
            log.train_moves(train, currentStation, nextStation);
            currentStation.setTrain(null);
            nextStation.setTrain(train);
            currentStation.getCondition().signalAll();
          } finally {
            currentStation.getLock().unlock();
          }

          nextStation.getLock().lock();
          try {
            nextStation.getCondition().signalAll();
          } finally {
            nextStation.getLock().unlock();
          }

          try {
            Thread.sleep(500);

            if (nextStation.equals(stations.get(0)) || nextStation.equals(stations.get(stations.size() - 1))) {
              direction *= -1;
            }

            index = (index + direction + stations.size()) % stations.size();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
          }
        }
      });
      threads.add(trainThread);
      trainThread.start();
    }

    // Create and start passenger threads
    for (String passengerName : mbta.getJourneys().keySet()) {
      Passenger passenger = Passenger.make(passengerName);
      Thread passengerThread = new Thread(() -> {
        List<Station> journey = mbta.getJourneys().get(passengerName);
        for (int i = 0; i < journey.size() - 1; i++) {
          Station currentStation = journey.get(i);
          Station nextStation = journey.get(i + 1);

          currentStation.getLock().lock();
          try {
            currentStation.awaitTrain(); // Wait for a train to arrive
            Train train = currentStation.getTrain();
            train.board(passenger);
            log.passenger_boards(passenger, train, currentStation);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
          } finally {
            currentStation.getLock().unlock();
          }

          nextStation.getLock().lock();
          try {
            Train train = currentStation.getTrain();
            train.awaitStation(nextStation); // Wait for the train to reach the next station
            train.deboard(passenger);
            log.passenger_deboards(passenger, train, nextStation);
            passenger.moveTo(nextStation);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
          } finally {
            nextStation.getLock().unlock();
          }
        }
      });
      threads.add(passengerThread);
      passengerThread.start();
    }

    // Wait for all threads to finish
    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("usage: ./sim <config file>");
      System.exit(1);
    }

    MBTA mbta = new MBTA();
    mbta.loadConfig(args[0]);

    Log log = new Log();

    run_sim(mbta, log);

    String s = new LogJson(log).toJson();
    PrintWriter out = new PrintWriter("log.json");
    out.print(s);
    out.close();

    mbta.reset();
    mbta.loadConfig(args[0]);
    Verify.verify(mbta, log);
  }
}
