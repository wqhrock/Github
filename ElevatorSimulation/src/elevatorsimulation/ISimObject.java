/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elevatorsimulation;

/**
 *
 * @author Qihang Wang
 */
public interface ISimObject {

    public void tick(int time);
    
    public void afterTick();
}
