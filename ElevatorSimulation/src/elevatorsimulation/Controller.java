
package elevatorsimulation;

import java.util.ArrayList;

/**
 *
 * @author Qihang Wang
 */
public class Controller implements ISimObject{
    
    public int time;
    
    private ArrayList<Request> finishedRequests;
    
    public ArrayList<Elevator> elevators;
    public RequestPool requestPool;
    
    public Controller(){        
        this.elevators = new ArrayList<>();
        for(int i = 0; i < Config.ELEVATOR_NUM; i++){
            Elevator elevator = new Elevator(i);
            this.elevators.add(elevator);
            elevator.regController(this);
        }
        this.finishedRequests = new ArrayList<>();
    }
    
    public void regRequestPool(RequestPool requestPool){
        this.requestPool = requestPool;
    }
    
    public void addReqsFromPool(){          
        if(this.requestPool.timeReqsMap.isEmpty())
            return;
        
        if(this.requestPool.time == this.requestPool.timeReqsMap.firstKey()){
            ArrayList<Request> reqsToController = this.requestPool.timeReqsMap.remove(this.requestPool.timeReqsMap.firstKey());
            for(Request req : reqsToController){
                MoveDirection direction = req.destFloor > req.startFloor ? MoveDirection.UP : MoveDirection.DOWN;
                ArrayList<Elevator> eleDirection = this.eleDirection(direction, this.elevators);
                if(eleDirection.isEmpty()){
                    eleDirection = this.elevators;
                }
                Elevator elevator = this.eleMinReqs(eleDirection).get(0);
                ArrayList<Request> requests = new ArrayList<>();
                requests.add(req);
                elevator.addRequestsToStartReqsMap(requests);
            }
        }
    }
    
    public ArrayList<Elevator> eleDirection(MoveDirection direction, ArrayList<Elevator> elevators){
        ArrayList<Elevator> eles = new ArrayList<>();
        for(Elevator ele : elevators){
            if(ele.moveDirection == direction){
                eles.add(ele);
            }
        }
        return eles;
    }
    
    public ArrayList<Elevator> eleMinReqs(ArrayList<Elevator> elevators) {
        ArrayList<Elevator> eles = new ArrayList<>();
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < elevators.size(); i++){
            int reqsNum = elevators.get(i).reqsNum();
            if(reqsNum < min){
                min = reqsNum;
            }
        }
        for (Elevator ele : elevators) {
            if (ele.reqsNum() == min) {
                eles.add(ele);
            }
        }
        return eles;
    }
    
    public void addFinishedRequest(Request request){
        this.finishedRequests.add(request);
    }

    public void setTime(int time){
        this.time = time;
        this.requestPool.time = time;
    }
    
    public void moveElevators(){
        for(Elevator ele : this.elevators){
            switch (ele.eleStatus) {
                case STOP:
                    ele.continueStop();
                    break;
                case MOVE:
                    ele.operate();
                    break;
                case WAIT:
                    break;
                default:
                    break;
            }
        }
    }
    
    public void avgDevWaitTime(){
        double avg = 0;
        for (Request request : this.finishedRequests) {
            avg+=request.waitTime();
        }
        avg /=this.finishedRequests.size();
        
        double dev = 0;
        double sum = 0;
        for (Request request : this.finishedRequests) {
            sum+= Math.pow(request.waitTime() - avg, 2);
        }
        dev = Math.pow(sum / this.finishedRequests.size(), 0.5);
        System.out.println("average wait time = " + avg/10);
        System.out.println("standard deviation = " + dev/100);
        System.exit(0);
    }
    
    public void tick(int time){
        if(this.finishedRequests.size() == this.requestPool.reqsNum){
            this.avgDevWaitTime();            
        }
        
        this.setTime(time);
        this.addReqsFromPool();
        
        for(Elevator ele : this.elevators){
            ele.operate();
        }
        System.out.println("----------------------------------------------------------");
        for (Elevator ele : this.elevators) {
            System.out.println(ele);
        }
        System.out.println("----------------------------------------------------------");
    }
    
    public void afterTick(){
    }
}
