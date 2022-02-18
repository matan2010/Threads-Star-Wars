
package bgu.spl.mics.application.passiveObjects;



import java.util.ArrayList;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add as you want (as said in forum by yairva - Saturday, 28 November 2020 22:31:45)
 */
public class Ewoks {

    private List<Ewok> ewokArrayList;

    //SINGLETON WAS BUILT AS EXPLAINED BY ONE OF THE LECTURERS.
    private static class singleEwoksInstance{
        private static final Ewoks singleEwoksInstance = new Ewoks();
    }

    private Ewoks() {
        this.ewokArrayList = new ArrayList<>();
    }

    public static Ewoks getInstance() {
            return singleEwoksInstance.singleEwoksInstance;
    }

    public void setEwokArrayList(List<Ewok> ewokArrayList){    //will be set at main function
        this.ewokArrayList = ewokArrayList;
    }

    public List<Integer> acquire (List<Integer> ewoksSerials){
        for (Integer serialNumber : ewoksSerials) {
            ewokArrayList.get(serialNumber).acquire();           //notice: acquire() is Ewok.acquire() that leads to Semaphore.acquire().
        }
        return ewoksSerials;
    }

    //release for list of ewoks
    public boolean release(List<Integer> ewoksSerials) {
        for (Integer serialNumber : ewoksSerials) {
            ewokArrayList.get(serialNumber).release();         //notice: release() is Ewok.release() that leads to Semaphore.release().
        }
        return true;
    }

}
