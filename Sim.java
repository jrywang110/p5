import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.Thread;

public class Sim {
  private static Lock lock = new ReentrantLock();

  private static void moveTrainHelper(Train t, MBTA mbta, Log log) {
    List<Station> stationList = mbta.lines.get(t.toString());
    Station currStation = mbta.train_position.get(t.toString());

    if (((stationList.indexOf(currStation) == stationList.size() - 1) && t.isRight()) || (stationList.indexOf(currStation) == 0 && !t.isRight())) {
          t.changeDir();
    }

    boolean occupied = false;
    if (t.isRight()) {
        for (String trainName : mbta.train_position.keySet()) {
            if (mbta.train_position.get(trainName) == stationList.get(stationList.indexOf(currStation) + 1)) {
              occupied = true;
            }
        }
        if (!occupied) {
          Event e = new MoveEvent(t, currStation, stationList.get(stationList.indexOf(currStation) + 1));
          e.replayAndCheck(mbta);
          log.train_moves(t, currStation, stationList.get(stationList.indexOf(currStation) + 1));
        }
    } else {
        for (String trainName : mbta.train_position.keySet()) {
            if (mbta.train_position.get(trainName) == stationList.get(stationList.indexOf(currStation) - 1)) {
              occupied = true;
            }
        }
        if (!occupied) {
          Event e = new MoveEvent(t, currStation, stationList.get(stationList.indexOf(currStation) - 1));
          e.replayAndCheck(mbta);
          log.train_moves(t, currStation, stationList.get(stationList.indexOf(currStation) - 1));
        }
    } 
  }

  private static void boardDeboardHelper(Passenger p, MBTA mbta, Log log) {
    boolean boarded = p.isBoarded();
    if (boarded) {
      int journeyIndex = mbta.journeys.get(p.toString()).indexOf(p.get_station());
      int maxJourneyIndex = mbta.journeys.get(p.toString()).size() - 1;
      for (String trainName : mbta.train_position.keySet()) {
        if (journeyIndex < maxJourneyIndex) {
          if (mbta.train_position.get(trainName) == mbta.journeys.get(p.toString()).get(journeyIndex + 1)) {
            Event e = new DeboardEvent(p, Train.make(trainName), mbta.train_position.get(trainName));
            e.replayAndCheck(mbta);
            log.passenger_deboards(p, Train.make(trainName), mbta.train_position.get(trainName));
            boarded = false;
          }
        }
      }
    } else {
      for (String trainName : mbta.train_position.keySet()) {
        int journeyIndex = mbta.journeys.get(p.toString()).indexOf(p.get_station());
        int maxJourneyIndex = mbta.journeys.get(p.toString()).size() - 1;
        if (mbta.train_position.get(trainName) == p.get_station() && (mbta.train_position.get(trainName) != mbta.journeys.get(p.toString()).get(maxJourneyIndex))) {
          Event e = new BoardEvent(p, Train.make(trainName), mbta.train_position.get(trainName));
          e.replayAndCheck(mbta);
          log.passenger_boards(p, Train.make(trainName), mbta.train_position.get(trainName));
          boarded = true;
        }
      }
    }
  }
  public static void run_sim(MBTA mbta, Log log) {
    for (String trainName : mbta.lines.keySet()) {
      Train t = Train.make(trainName);
      Thread thr = new Thread() {
            public void run() {
              Train t = Train.make(trainName);
              while (!mbta.simOver()) {
                try { sleep(500); }
                catch (InterruptedException e){ }
                lock.lock();
                moveTrainHelper(t, mbta, log);
                lock.unlock();
              }
              Thread.currentThread().interrupt();
            }
      };
      thr.start();
    }

    for (String pName : mbta.journeys.keySet()) {
      Thread thr = new Thread() {
        public void run() {
          while (!mbta.simOver()) {
            lock.lock();
            boardDeboardHelper(Passenger.make(pName), mbta, log);
            lock.unlock();  
          }
        }
      };
      thr.start();
    }
    
    // Map<Station, ReentrantLock> stationLocks = new HashMap<Station, ReentrantLock>();
    // for (List<Station> stationList : mbta.lines.values()) {
    //   for (Station s : stationList) {
    //     stationLocks.put(s, new ReentrantLock());
    //   }
    // }

    // for (String trainName : mbta.lines.keySet()) {
    //   Thread thr = new Thread() {
    //     private Train t = Train.make(trainName);
    //     private List<Station> stationList = mbta.lines.get(trainName);
    //     private Station currStation = mbta.train_position.get(trainName);

    //     public void run() {
    //       for (int i = 0; i < 10; i++) {
    //         try {
    //           synchronized (this) {
    //             Lock currLock = stationLocks.get(currStation);
    //             currLock.lock();

    //             if (((stationList.indexOf(currStation) == stationList.size() - 1) && t.isRight()) || (stationList.indexOf(currStation) == 0 && !t.isRight())) {
    //                   t.changeDir();
    //             }

    //             if (t.isRight()) {
    //               if (!stationLocks.get(stationList.get(stationList.indexOf(currStation) + 1)).isLocked()) {
    //                 Lock newLock = stationLocks.get(stationList.get(stationList.indexOf(currStation) + 1));
    //                 newLock.lock();
    //                 currLock.unlock();
    //                 Event e = new MoveEvent(t, currStation, stationList.get(stationList.indexOf(currStation) + 1));
    //                 e.replayAndCheck(mbta);
    //                 log.train_moves(t, currStation, stationList.get(stationList.indexOf(currStation) + 1));
    //               }
    //             } else {
    //               if (!stationLocks.get(stationList.get(stationList.indexOf(currStation) - 1)).isLocked()) {
    //                 Lock newLock = stationLocks.get(stationList.get(stationList.indexOf(currStation) + 1));
    //                 newLock.lock();
    //                 currLock.unlock();
    //                 Event e = new MoveEvent(t, currStation, stationList.get(stationList.indexOf(currStation) - 1));
    //                 e.replayAndCheck(mbta);
    //                 log.train_moves(t, currStation, stationList.get(stationList.indexOf(currStation) - 1));
    //               }
    //             } 
    //           }
    //         } catch (Exception e){
    //           throw new RuntimeException(e);
    //         }
    //       }
    //     }
    //   };
    //   thr.start();
    // }

    // for (String pName : mbta.journeys.keySet()) {
    //   Thread thr = new Thread() {
    //     private boolean boarded = false;
    //     private Passenger p = Passenger.make(pName);

    //     public void run() {
    //       while (!mbta.simOver()) {
    //         try { 
    //           synchronized (this) {
    //             if (boarded) {
    //               int journeyIndex = mbta.journeys.get(p.toString()).indexOf(p.get_station());
    //               int maxJourneyIndex = mbta.journeys.get(p.toString()).size() - 1;
    //               for (String trainName : mbta.train_position.keySet()) {
    //                 if (journeyIndex < maxJourneyIndex) {
    //                   if (mbta.train_position.get(trainName) == mbta.journeys.get(p.toString()).get(journeyIndex + 1)) {
    //                     Event e = new DeboardEvent(Passenger.make(pName), Train.make(trainName), mbta.train_position.get(trainName));
    //                     e.replayAndCheck(mbta);
    //                     log.passenger_deboards(Passenger.make(pName), Train.make(trainName), mbta.train_position.get(trainName));
    //                     boarded = false;
    //                   }
    //                 }
    //               }
    //             } else {
    //               for (String trainName : mbta.train_position.keySet()) {
    //                 if (mbta.train_position.get(trainName) == Passenger.make(pName).get_station()) {
    //                   Event e = new BoardEvent(Passenger.make(pName), Train.make(trainName), mbta.train_position.get(trainName));
    //                   e.replayAndCheck(mbta);
    //                   log.passenger_boards(Passenger.make(pName), Train.make(trainName), mbta.train_position.get(trainName));
    //                   boarded = true;
    //                 }
    //               }
    //             }
    //           }
    //         } catch (Exception e) {
    //           throw new RuntimeException(e);
    //         }  
    //       }
    //     }
    //   };
    //   thr.start();
    // }
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
