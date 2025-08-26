import static org.junit.Assert.*;
import java.util.*;
import org.junit.*;

public class Tests {
  @Test public void testPass() {
    assertTrue("true should be true", true);
  }

 
 @Test
 public void testLoadConfig() {
   MBTA mbta = new MBTA();
   mbta.loadConfig("sample.json");
   
   // Check if lines are loaded correctly
   List<String> redLine = List.of("Davis", "Harvard", "Kendall", "Park", "Downtown Crossing",
                                  "South Station", "Broadway", "Andrew", "JFK");
   List<Station> loadedRedLineStations = mbta.getLines().get("red");
   List<String> loadedRedLine = new ArrayList<>();
   for (Station station : loadedRedLineStations) {
     loadedRedLine.add(station.toString());
   }
   System.out.println("Expected Red Line: " + redLine);
   System.out.println("Loaded Red Line: " + loadedRedLine);
   assertEquals(redLine, loadedRedLine);
   
   List<String> greenLine = List.of("Tufts", "East Sommerville", "Lechmere", "North Station",
                                    "Government Center", "Park", "Boylston", "Arlington", "Copley");
   List<Station> loadedGreenLineStations = mbta.getLines().get("green");
   List<String> loadedGreenLine = new ArrayList<>();
   for (Station station : loadedGreenLineStations) {
     loadedGreenLine.add(station.toString());
   }
   System.out.println("Expected Green Line: " + greenLine);
   System.out.println("Loaded Green Line: " + loadedGreenLine);
   assertEquals(greenLine, loadedGreenLine);
   
   // Check if journeys are loaded correctly
   List<String> bobJourney = List.of("Park", "Tufts");
   List<Station> loadedBobJourneyStations = mbta.getJourneys().get("Bob");
   List<String> loadedBobJourney = new ArrayList<>();
   for (Station station : loadedBobJourneyStations) {
     loadedBobJourney.add(station.toString());
   }
   System.out.println("Expected Bob Journey: " + bobJourney);
   System.out.println("Loaded Bob Journey: " + loadedBobJourney);
   assertEquals(bobJourney, loadedBobJourney);
   
   List<String> aliceJourney = List.of("Davis", "Kendall");
   List<Station> loadedAliceJourneyStations = mbta.getJourneys().get("Alice");
   List<String> loadedAliceJourney = new ArrayList<>();
   for (Station station : loadedAliceJourneyStations) {
     loadedAliceJourney.add(station.toString());
   }
   System.out.println("Expected Alice Journey: " + aliceJourney);
   System.out.println("Loaded Alice Journey: " + loadedAliceJourney);
   assertEquals(aliceJourney, loadedAliceJourney);
 }

  @Test
  public void testInitialConditions() {
    MBTA mbta = new MBTA();
    mbta.loadConfig("sample.json");
    
    // Verify initial conditions
    try {
      mbta.checkStart();
    } catch (IllegalStateException e) {
      fail("Initial conditions are not met: " + e.getMessage());
    }
  }

  @Test
  public void testReplayMoveEvent() {
    MBTA mbta = new MBTA();
    mbta.loadConfig("sample.json");
    
    Train train = Train.make("red");
    Station s1 = Station.make("Davis");
    Station s2 = Station.make("Harvard");
    train.moveTo(s1); // Ensure the train starts at the initial station
    MoveEvent moveEvent = new MoveEvent(train, s1, s2);
    
    try {
      moveEvent.replayAndCheck(mbta);
      assertEquals(s2, train.getCurrentStation());
    } catch (IllegalStateException e) {
      fail("Move event failed: " + e.getMessage());
    }
  }

  @Test
  public void testReplayBoardEvent() {
    MBTA mbta = new MBTA();
    mbta.loadConfig("sample.json");
    
    Passenger passenger = Passenger.make("Alice");
    Train train = Train.make("red");
    Station station = Station.make("Davis");
    train.moveTo(station); // Ensure the train is at the initial station
    passenger.moveTo(station); // Ensure the passenger is at the station
    BoardEvent boardEvent = new BoardEvent(passenger, train, station);
    
    try {
      boardEvent.replayAndCheck(mbta);
      assertTrue(train.hasPassenger(passenger));
    } catch (IllegalStateException e) {
      fail("Board event failed: " + e.getMessage());
    }
  }

  @Test
  public void testReplayDeboardEvent() {
    MBTA mbta = new MBTA();
    mbta.loadConfig("sample.json");
    
    Passenger passenger = Passenger.make("Alice");
    Train train = Train.make("red");
    Station station = Station.make("Davis");
    train.moveTo(station); // Ensure the train is at the initial station
    passenger.moveTo(station); // Ensure the passenger is at the station
    BoardEvent boardEvent = new BoardEvent(passenger, train, station);
    boardEvent.replayAndCheck(mbta);
    
    Station nextStation = Station.make("Kendall");
    MoveEvent moveEvent = new MoveEvent(train, station, nextStation);
    moveEvent.replayAndCheck(mbta);
    
    DeboardEvent deboardEvent = new DeboardEvent(passenger, train, nextStation);
    
    try {
      deboardEvent.replayAndCheck(mbta);
      assertFalse(train.hasPassenger(passenger));
    } catch (IllegalStateException e) {
      fail("Deboard event failed: " + e.getMessage());
    }
  }

  @Test
  public void testFinalConditions() {
    MBTA mbta = new MBTA();
    mbta.loadConfig("sample.json");

    // Simulate the full journey for a passenger
    Passenger passenger = Passenger.make("Bob");
    List<Station> journey = mbta.getJourneys().get("Bob");

    Train train = Train.make("green");
    train.moveTo(journey.get(0)); // Ensure the train starts at the first station
    for (int i = 0; i < journey.size() - 1; i++) {
      Station currentStation = journey.get(i);
      Station nextStation = journey.get(i + 1);
      
      BoardEvent boardEvent = new BoardEvent(passenger, train, currentStation);
      boardEvent.replayAndCheck(mbta);
      
      MoveEvent moveEvent = new MoveEvent(train, currentStation, nextStation);
      moveEvent.replayAndCheck(mbta);
      
      DeboardEvent deboardEvent = new DeboardEvent(passenger, train, nextStation);
      deboardEvent.replayAndCheck(mbta);
    }
    
    // Verify final conditions
    try {
      mbta.checkEnd();
    } catch (IllegalStateException e) {
      fail("Final conditions are not met: " + e.getMessage());
    }
  }

  @Test
public void testVerifier() {
    MBTA mbta = new MBTA();
    mbta.loadConfig("sample.json");

    // Verify the initial conditions
    try {
        mbta.checkStart();
    } catch (IllegalStateException e) {
        fail("Verification failed: " + e.getMessage());
    }

    Log log = new Log();

    // Simulate the events in the log
    for (Event event : log.events()) {
        event.replayAndCheck(mbta);
    }

    // Verify the final conditions
    try {
        mbta.checkEnd();
    } catch (IllegalStateException e) {
        fail("Verification failed: " + e.getMessage());
    }
}

}
