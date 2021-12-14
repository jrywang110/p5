import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class Sim {

  public static void run_sim(MBTA mbta, Log log) {
    for (String trainName : mbta.train_position.keySet()) {
      Thread thr = new Thread() {
        private static Lock lock = new ReentrantLock();
        public void run() {
          while (!mbta.simOver()) {
            try { 
              sleep(500);
            } catch (InterruptedException e){}
            moveTrainHelper(trainName, mbta, log);
          }
        }
      };
      thr.start();
    }

    for (String pName : mbta.journeys.keySet()) {
      Thread thr = new Thread(pName) {
        private static Lock lock = new ReentrantLock();
        private Passenger p = Passenger.make(pName);
        public void run() {
          while (!mbta.simOver()) {
            try { 
              synchronized (this) {
                for (String trainName : mbta.train_position.keySet()) {
                  if (mbta.train_position.get(trainName) == Passenger.make(pName).get_station()) {
                    Event e = new BoardEvent(Passenger.make(pName), Train.make(trainName), mbta.train_position.get(trainName));
                    e.replayAndCheck(mbta);
                    log.passenger_boards(Passenger.make(pName), Train.make(trainName), mbta.train_position.get(trainName));
                  }
                  int journeyIndex = mbta.journeys.get(p.toString()).indexOf(p.get_station());
                  int maxJourneyIndex = mbta.journeys.get(p.toString()).size();
                  if (journeyIndex < maxJourneyIndex) {
                    if (mbta.train_position.get(trainName) == mbta.journeys.get(p.toString()).get(journeyIndex + 1)) {
                      Event e = new DeboardEvent(Passenger.make(pName), Train.make(trainName), mbta.train_position.get(trainName));
                      e.replayAndCheck(mbta);
                      log.passenger_deboards(Passenger.make(pName), Train.make(trainName), mbta.train_position.get(trainName));
                    }
                  }
                }
              }
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        }
      };
      thr.start();
    }
  }

  public static void moveTrainHelper(String trainName, MBTA mbta, Log log) {
    Train t = Train.make(trainName);
    Station startStation = mbta.train_position.get(trainName);
    List<Station> stationList = mbta.lines.get(trainName);
    int maxIndex = stationList.size() - 1;
    int startIndex = stationList.indexOf(startStation);

    if ((startIndex == maxIndex && t.isRight()) || (startIndex == 0 && !t.isRight())) {
          t.changeDir();
    }

    if (t.isRight()) {
      Event e = new MoveEvent(t, startStation, stationList.get(startIndex + 1));
      e.replayAndCheck(mbta);
      log.train_moves(t, startStation, stationList.get(startIndex + 1));
    } else {
      Event e = new MoveEvent(t, startStation, stationList.get(startIndex - 1));
      e.replayAndCheck(mbta);
      log.train_moves(t, startStation, stationList.get(startIndex - 1));
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
