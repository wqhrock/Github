
package elevatorsimulation;

enum RequestStatus{
    BeforeGetIn,
    InEvevator,
    AfterGetOff
}

enum RequestDirection{
    UP,
    DOWN
}

public class Request{

    public final int startFloor;
    public final int destFloor;
    
    public RequestStatus status;
    public RequestDirection direction;
    public int waitTime = -1;
    
    private int startTime;
    private int endTime;
    
    public Request(int startFloor, int endFloor){
        this.startFloor = startFloor;
        this.destFloor = endFloor;
        this.direction = this.destFloor > this.startFloor ? RequestDirection.UP : RequestDirection.DOWN;
    }
    
    public void start(int time){
        this.startTime = time;
        this.status = RequestStatus.BeforeGetIn;
    }
    
    public void getIn(){
        this.status = RequestStatus.InEvevator;
    }
    
    public void end(int time){
        this.endTime = time;
        this.status = RequestStatus.AfterGetOff;
    }
    
    public int waitTime(){
        if(this.status != RequestStatus.AfterGetOff)
            throw new RuntimeException("Not after get off");
        
        if(this.waitTime == -1)
            this.waitTime = this.endTime - this.startTime;

        return this.waitTime;
    }
    
    public String toString(){
        return "["+this.startFloor +" ---> " + this.destFloor+"]";
    }
}
