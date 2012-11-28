
package elevatorsimulation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Qihang Wang
 */
public class Config {
    
    public static String FILE_NAME;
    
    public static int SEC_INTERVAL;
    
    public static int MAX_FLOOR_NUM;
    
    public static int ELEVATOR_NUM;
    
    public static int MAX_TIME;
    
    public static int STOP_TIME;
    
    public static int ELE_INIT_FLOOR;
    
    public static int[] BUSY_FLOORS;
    
    static {
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream("config.properties");
            properties.load(fis);
            fis.close();

        } catch (FileNotFoundException e) {
            System.err.println("Config file not found!");
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Loading config file failed!!");
            System.exit(-2);
        }
        
        FILE_NAME = properties.getProperty("FILE_NAME");
        SEC_INTERVAL = Integer.parseInt(properties.getProperty("SEC_INTERVAL"));
        MAX_FLOOR_NUM = Integer.parseInt(properties.getProperty("MAX_FLOOR_NUM"));
        ELEVATOR_NUM = Integer.parseInt(properties.getProperty("ELEVATOR_NUM"));
        MAX_TIME = Integer.parseInt(properties.getProperty("MAX_TIME"));
        STOP_TIME = Integer.parseInt(properties.getProperty("STOP_TIME"));
        ELE_INIT_FLOOR = Integer.parseInt(properties.getProperty("ELE_INIT_FLOOR"));
        String[] busyFloors = properties.getProperty("BUSY_FLOORS").split(",");
        BUSY_FLOORS = new int[busyFloors.length];
        for(int i = 0; i < busyFloors.length; i++){
            BUSY_FLOORS[i] = Integer.parseInt(busyFloors[i].trim());
        }
    }
}
