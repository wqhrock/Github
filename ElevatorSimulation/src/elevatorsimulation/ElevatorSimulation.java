/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elevatorsimulation;

/**
 *
 * @author Qihang Wang
 */
public class ElevatorSimulation {
    
    public static void main(String[] args) {
        Timer timer = new Timer();
        RequestPool requestPool = new RequestPool();
        requestPool.readReqFile(Config.FILE_NAME);
        Controller controller = new Controller();
        controller.regRequestPool(requestPool);
        timer.regTickObject(controller);
        timer.start();
    }
}
