package elevatorsimulation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Qihang Wang
 */
public class RequestPool{
    
    public int time;
    public SortedMap<Integer, ArrayList<Request>> timeReqsMap;
    public int reqsNum;

    public RequestPool(){
        this.timeReqsMap = new TreeMap<>();
        this.time = 0;
    }
        
    public void readReqFile(String fileURL) {
        try {
            BufferedReader br;
            br = new BufferedReader(new FileReader(fileURL));
            String line;
            while ((line = br.readLine()) != null) {
                this.reqsNum++;
                String[] reqArr = line.split(",");
                Integer time = Integer.parseInt(reqArr[0].trim());
                Integer startFloor = Integer.parseInt(reqArr[1].trim());
                Integer endFloor = Integer.parseInt(reqArr[2].trim());
                Request req = new Request(startFloor, endFloor);
                ArrayList<Request> requests=this.timeReqsMap.get(time);
                if(requests == null)
                    requests = new ArrayList<>();
                requests.add(req);
                this.timeReqsMap.put(time, requests);
            }
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
