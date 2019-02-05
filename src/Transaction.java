import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * @author Eric Fahey <eric.fahey@ryerson.ca>
 */
public class Transaction extends Thread {

    private static int count = 1;
    private static int SITES = 2;
    private static Semaphore semaphore = new Semaphore(1);
    private static boolean deletionMode = true;
    private static boolean insertionMode = true;

    public ArrayList<DatabaseConnection> connections = new ArrayList<>();
    private int threadId;

    public Transaction() {
        super();
        this.threadId = getNextId();
        for (int i = 0; i < SITES; i++) {
            DatabaseConnection connection =
                    new DatabaseConnection("localhost", 1522 + i, "SYSTEM", "admin", ServiceType.XE);
            if (connection.connect()) {
                connections.add(connection);
            }
        }
    }

    synchronized static int getNextId() {
        return count++;
    }

    public void run() {
        try {
            semaphore.acquire();
            process();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    public void process() {
        System.out.println("---------START-------------");
        int uniqueCount = 0;
        for (DatabaseConnection connection : connections) {
            if (connection.isUnique(getUniqueName())) {
                uniqueCount++;
            }
        }
        //These modes are strictly for demonstration purposes.
        if (insertionMode && deletionMode) {
            if (uniqueCount == 2) {
                System.out.println("Both sites vote 'yes' to INSERTING " + getUniqueName());
                for (DatabaseConnection connection : connections) {
                    if (!connection.insertName(getUniqueName())) {
                        return;
                    }
                }
            } else if (uniqueCount == 0) {
                System.out.println("Both sites vote 'yes' to DELETING " + getUniqueName());
                for (DatabaseConnection connection : connections) {
                    if (!connection.deleteName(getUniqueName())) {
                        return;
                    }
                }
            }
        } else if (insertionMode) {
            if (uniqueCount == 2) {
                System.out.println("Both sites vote 'yes' to INSERTING " + getUniqueName());
                for (DatabaseConnection connection : connections) {
                    if (!connection.insertName(getUniqueName())) {
                        return;
                    }
                }
            } else if (uniqueCount == 0) {
                return;
            }
        } else if (deletionMode) {
            if (uniqueCount == 0) {
                System.out.println("Both sites vote 'yes' to DELETING " + getUniqueName());
                for (DatabaseConnection connection : connections) {
                    if (!connection.deleteName(getUniqueName())) {
                        return;
                    }
                }
            } else if (uniqueCount == 2) {
                return;
            }
        }
        System.out.println("Transaction successful, ready to commit!");
        //Transaction successful, time to commit changes :D
        try {
            for (DatabaseConnection connection : connections) {
                connection.getConnection().commit();
            }
            System.out.println("Commit Successful!");
        } catch (Exception e) {
            System.out.println("Commit failed!");
            e.printStackTrace();
        }
    }

    /**
     * Dividing Threads by 2 will ensure we can get transactions failures.
     *
     * @return a Unique Name based on the threadId
     */
    public String getUniqueName() {
        return "Eric" + threadId % (Replication.THREADS / 2);
    }
}
