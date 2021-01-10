package Database;

/**
 * This class is used as a Monitor to synchronize access to the database by the different Threads
 */
public class DatabaseLock {

    private boolean dbWriting;
    private int readers;

    public DatabaseLock (){
        dbWriting=false;
        readers=0;
    }

    /**
     * Reading access granted unless there is someone writing
     * @throws InterruptedException
     */
    public synchronized void acquireRead () throws InterruptedException {
        while (dbWriting){
            wait();
        }
        readers++;
    }

    /**
     * Writing access is granted only if there is no one reading nor writing
     * @throws InterruptedException
     */
    public synchronized void acquireWrite() throws InterruptedException {
        while (dbWriting || readers > 0){
            wait();
        }
        dbWriting=true;
    }

    /**
     * Releases the reading lock. If the number of readers is 0 we can awake one writer (notify)
     */
    public synchronized void releaseRead(){
        readers--;
        if(readers == 0) notify();
    }

    /**
     * Releases the writing lock. It awakes all pending readers
     */
    public synchronized void releaseWrite(){
        dbWriting=false;
        notifyAll();
    }


}
