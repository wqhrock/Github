package elevatorsimulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Qihang Wang
 */
enum MoveDirection {

    UP,
    DOWN,
    NONE
}

enum EleStatus {

    MOVE,
    STOP,
    WAIT
}

public class Elevator {

    public int ID;
    
    public int stopCount;
    public Controller controller;
    public SortedMap<Integer, ArrayList<Request>> upWaitReqsMap;
    public SortedMap<Integer, ArrayList<Request>> downWaitReqsMap;
    public SortedMap<Integer, ArrayList<Request>> inDestReqsMap;
    
    public EleStatus eleStatus;
    public EleStatus eleStatusPrev;
    public MoveDirection moveDirection;
    public MoveDirection moveDirectionPrev;
    public int currFloor;
    public int currDestFloor;
    
    public Elevator(int id){
        this.ID = id;
        this.currFloor = Config.ELE_INIT_FLOOR;
        this.eleStatus = EleStatus.WAIT;
        this.moveDirection = MoveDirection.UP;
        this.inDestReqsMap = new TreeMap<>();
        this.upWaitReqsMap = new TreeMap<>();
        this.downWaitReqsMap = new TreeMap<>();
    }
    
    public void regController(Controller controller){
        this.controller = controller;
    }
    
    public void addRequestsToStartReqsMap(ArrayList<Request> requests){
        for (Request req : requests) {
            SortedMap<Integer, ArrayList<Request>> toAddMap = null;
            int direction = req.destFloor - req.startFloor;
            toAddMap = direction > 0 ? this.upWaitReqsMap : this.downWaitReqsMap;
            ArrayList<Request> reqs = toAddMap.get(req.startFloor);
            if (reqs == null) {
                reqs = new ArrayList<>();
            }
            reqs.add(req);
            toAddMap.put(req.startFloor, reqs);
            //System.out.println("Enter: " + req);
        }
    }
    
    public boolean hasRequestsWaiting(MoveDirection moveDirection){
        boolean result = false;
        if(moveDirection == MoveDirection.UP && this.upWaitReqsMap.get(this.currFloor) != null){
            result = true;
        }
        if(moveDirection == MoveDirection.DOWN && this.downWaitReqsMap.get(this.currFloor) != null){
            result = true;
        }
        return result;
    }
    
    public boolean hasRequestsGettingOff(){
        return this.inDestReqsMap.get(this.currFloor) == null ? false : true;
    }
    
    public void getInRequests(MoveDirection moveDirection){
        SortedMap<Integer, ArrayList<Request>> fromMap = 
                moveDirection == MoveDirection.UP ? this.upWaitReqsMap : this.downWaitReqsMap;
        for (Request req : fromMap.get(this.currFloor)) {
            ArrayList<Request> reqs = this.inDestReqsMap.get(req.destFloor);
            if (reqs == null) {
                reqs = new ArrayList<>();
            }
            reqs.add(req);
            req.start(this.controller.time);
            this.inDestReqsMap.put(req.destFloor, reqs);
        }
        fromMap.remove(this.currFloor);
    }
    
    public void getOffRequests(){
        ArrayList<Request> requestsToGetOff = this.inDestReqsMap.get(this.currFloor);
        for (Request req : requestsToGetOff) {
            req.end(this.controller.time);
            this.controller.addFinishedRequest(req);
            //System.out.println("Exit: " + req);
        }
        this.inDestReqsMap.remove(this.currFloor);
    }
    
    public void operate(){
        
        if(this.eleStatus == EleStatus.STOP){
            continueStop();
        }
        
        if(this.eleStatus == EleStatus.WAIT){
            if(this.isInEmpty() && this.isUpWaitEmpty() && this.isDownWaitEmpty() && this.isAtBusyFloor()){
                this.eleStatus = EleStatus.WAIT;
            }else{
                this.eleStatus = EleStatus.MOVE;
                this.moveDirection = this.chooseDirection();
            }
        }
        
        if(this.eleStatus == EleStatus.MOVE){
            if(this.hasRequestsGettingOff() 
               || this.hasRequestsWaiting(MoveDirection.UP)
               || this.hasRequestsWaiting(MoveDirection.DOWN)){
                this.startStop();
            }else{
                if(this.moveDirection == MoveDirection.UP){
                    this.currFloor++;
                    if(this.currFloor == Config.MAX_FLOOR_NUM - 1){
                        this.moveDirection = MoveDirection.DOWN;
                    }
                }
                if(this.moveDirection == MoveDirection.DOWN){
                    this.currFloor--;
                    if(this.currFloor == 0){
                        this.moveDirection = MoveDirection.UP;
                    }
                }
            }
        }
    }
    
    public void startStop(){
        this.eleStatus = EleStatus.STOP;
        this.stopCount = Config.STOP_TIME;
    }
    
    public void continueStop(){
        this.stopCount--;
        if(this.stopCount == 0){
            this.endStop();
            return;
        }
        
        if(this.hasRequestsGettingOff()){
            this.getOffRequests();
        }
        if(this.hasRequestsWaiting(MoveDirection.UP)){
            this.getInRequests(MoveDirection.UP);
        }
        if (this.hasRequestsWaiting(MoveDirection.DOWN)) {
            this.getInRequests(MoveDirection.DOWN);
        }
    }
    
    public void endStop(){
        this.eleStatus = eleStatus.WAIT;
        this.operate();
    }
    
    public MoveDirection chooseDirection(){
        MoveDirection direction = this.moveDirection;
        if(this.isInEmpty()){
            if(this.hasRequestsWaiting(MoveDirection.UP) || this.hasRequestsWaiting(MoveDirection.DOWN)){
                direction = this.waitNum(MoveDirection.UP) > this.waitNum(MoveDirection.DOWN) ?
                        MoveDirection.UP : MoveDirection.DOWN;
            }else{
                int cloestBusyFloor = this.closestBusyFloor();
                direction = this.currFloor > cloestBusyFloor ? MoveDirection.DOWN : MoveDirection.UP;
            }
        }else{
            //Continue prev move direction;
        }
        return direction;
    }
    
    public int closestBusyFloor(){
        int[] diffs = new int[Config.BUSY_FLOORS.length];
        for(int i = 0; i < Config.BUSY_FLOORS.length; i++){
            diffs[i] = Math.abs(Config.BUSY_FLOORS[i] - this.currFloor);
        }
        int index = 0;
        int min = diffs[0];
        for(int i = 0; i < diffs.length; i++){
            if(diffs[i] < min){
                min = diffs[i];
                index = i;
            }
        }
        return Config.BUSY_FLOORS[index];
    }
    
    public boolean isAtBusyFloor(){
        boolean result = false;
        for(int busyFloor : Config.BUSY_FLOORS){
            if(this.currFloor == busyFloor){
                result = true;
                break;
            }
        }
        return result;
    }
    
    public int reqsNum(){
        int sum = 0;
        for (Map.Entry<Integer, ArrayList<Request>> entry : this.inDestReqsMap.entrySet()) {
            sum += entry.getValue().size();
        }
        return sum;
    }
    
    public boolean isInEmpty() {
        return this.inDestReqsMap.isEmpty();
    }
        
    public int waitNum(MoveDirection direction){
        int sum = 0;
        SortedMap<Integer, ArrayList<Request>> waitMap = null;
        if(direction == MoveDirection.UP){
            waitMap = this.upWaitReqsMap;
        }else if(direction == MoveDirection.DOWN){
            waitMap = this.downWaitReqsMap;
        }
        for(Map.Entry<Integer, ArrayList<Request>> entry : waitMap.entrySet()){
            sum+= entry.getValue().size();
        }
        return sum;
    }
    
    public boolean isUpWaitEmpty(){
        return this.upWaitReqsMap.isEmpty();
    }
    
    public boolean isDownWaitEmpty(){
        return this.downWaitReqsMap.isEmpty();
    }
    
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append("Elevator: ").append(this.ID).append("\n");
        result.append("    Current floor: ").append(this.currFloor).append("\n");
        result.append("    Passengers:");
        for (Map.Entry<Integer, ArrayList<Request>> entry : this.inDestReqsMap.entrySet()) {
            for(Request req : entry.getValue()){
                result.append(req).append(", ");
            }
        }
        return result.toString();
    }
}
