import java.util.*;
import com.google.gson.*;
import java.io.*;

public class MBTA {
  
  private Map<String, List<Station>> lines;
  private Map<String, List<Station>> journeys;
  private Map<String, Train> trains;
  private Map<String, Passenger> passengers;

  // Creates an initially empty simulation
  public MBTA() {
    this.lines = new HashMap<>();
    this.journeys = new HashMap<>();
    this.trains = new HashMap<>();
    this.passengers = new HashMap<>();
  }

  // Adds a new transit line with given name and stations
  public synchronized void addLine(String name, List<String> stationNames) {
    List<Station> stations = new ArrayList<>();
    for (String stationName : stationNames) {
        stations.add(Station.make(stationName));
    }
    lines.put(name, stations);
    Train train = Train.make(name);
    train.moveTo(stations.get(0)); // Set the initial station for the train
    stations.get(0).setTrain(train); // Set the train to the initial station
    trains.put(name, train);
}

  // Adds a new planned journey to the simulation
  public synchronized void addJourney(String name, List<String> stationNames) {
    List<Station> stations = new ArrayList<>();
    for (String stationName : stationNames) {
      stations.add(Station.make(stationName));
    }
    journeys.put(name, stations);
    Passenger passenger = Passenger.make(name);
    passenger.moveTo(stations.get(0)); // Set the initial station for the passenger
    passengers.put(name, passenger);
  }

  // Return normally if initial simulation conditions are satisfied, otherwise
  // raises an exception
  public synchronized void checkStart() {
    for (Map.Entry<String, List<Station>> entry : lines.entrySet()) {
        Train train = trains.get(entry.getKey());
        if (!train.getCurrentStation().equals(entry.getValue().get(0))) {
            throw new IllegalStateException("Train " + train + " is not at the starting station " + entry.getValue().get(0));
        }
    }

    for (Map.Entry<String, List<Station>> entry : journeys.entrySet()) {
        Passenger passenger = passengers.get(entry.getKey());
        if (!passenger.getCurrentStation().equals(entry.getValue().get(0))) {
            throw new IllegalStateException("Passenger " + passenger + " is not at the starting station " + entry.getValue().get(0));
        }
    }
}


  // Return normally if final simulation conditions are satisfied, otherwise
  // raises an exception
  public synchronized void checkEnd() {
    for (Map.Entry<String, List<Station>> entry : journeys.entrySet()) {
      Passenger passenger = passengers.get(entry.getKey());
      List<Station> journey = entry.getValue();
      if (!passenger.getCurrentStation().equals(journey.get(journey.size() - 1))) {
        throw new IllegalStateException("Passenger " + passenger + " has not reached the final station");
      }
    }
  }

  // Reset to an empty simulation
  public synchronized void reset() {
    lines.clear();
    journeys.clear();
    trains.clear();
    passengers.clear();
  }

  // Adds simulation configuration from a file
  public void loadConfig(String filename) {
    try {
      Gson gson = new Gson();
      BufferedReader br = new BufferedReader(new FileReader(filename));
      MBTAConfig config = gson.fromJson(br, MBTAConfig.class);
      
      for (Map.Entry<String, List<String>> line : config.lines.entrySet()) {
        addLine(line.getKey(), line.getValue());
      }
      
      for (Map.Entry<String, List<String>> journey : config.trips.entrySet()) {
        addJourney(journey.getKey(), journey.getValue());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Getter for lines
  public Map<String, List<Station>> getLines() {
    Map<String, List<Station>> immutableLines = new HashMap<>();
    for (Map.Entry<String, List<Station>> entry : lines.entrySet()) {
      immutableLines.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
    }
    return immutableLines;
  }

  // Getter for journeys
  public Map<String, List<Station>> getJourneys() {
    Map<String, List<Station>> immutableJourneys = new HashMap<>();
    for (Map.Entry<String, List<Station>> entry : journeys.entrySet()) {
      immutableJourneys.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
    }
    return immutableJourneys;
  }

  // Configuration class to match the JSON structure
  class MBTAConfig {
    Map<String, List<String>> lines;
    Map<String, List<String>> trips;
  }
}
