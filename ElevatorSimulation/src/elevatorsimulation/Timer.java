/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elevatorsimulation;

import java.util.ArrayList;

/**
 *
 * @author Qihang Wang
 */
public class Timer implements Runnable {

    private int time = -1;
    private ArrayList<ISimObject> tickObjects = new ArrayList<>();
    private ArrayList<ISimObject> afterTickObjects = new ArrayList<>();

    public void regTickObject(ISimObject tickObject) {
        this.tickObjects.add(tickObject);
    }
    
    public void regAfterTickObject(ISimObject afterTickObject){
        this.afterTickObjects.add(afterTickObject);
    }

    public void tick() {
        this.time++;
        for (ISimObject o : this.tickObjects) {
            o.tick(this.time);
        }
    }
    
    public void afterTick() {
        for (ISimObject o : this.afterTickObjects) {
            o.tick(this.time);
        }
    }
    
    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(Config.SEC_INTERVAL);
            } catch (InterruptedException ex) {
                System.err.println("Request generator interrupted: " + ex);
            }
            this.tick();
        }
    }
}
