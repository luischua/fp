package util;

import model.CrossCheckStatus;
import model.Fingerprint;
import model.Person;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CrossCheckMain {

    private static CrossCheckMain main = new CrossCheckMain();
    public static CrossCheckMain getInstance(){
        return main;
    }

    private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
    public static final long SLEEP_DURATION = 3000;
    private int noOfThreads = 2;
    private CrossCheckMain(){
        for(int i = 1; i <= noOfThreads; i++)
        {
            new CrossCheckThread().start();
        }
    }
    public void addId(String id){
        System.out.println("Adding"+ id);
        queue.add(id);
    }
    class CrossCheckThread extends Thread {
        public void run(){
            while(true){
                try {
                    String id = queue.remove();
                    System.out.println("Checking"+id);
                    Fingerprint f = Fingerprint.find(id);
                    f.newCrossCheck();
                    f.save();
                }catch (NoSuchElementException e){
                    try {
                        Thread.sleep(SLEEP_DURATION);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }
    }
}
