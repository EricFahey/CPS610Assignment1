import java.util.ArrayList;

/**
 * @author Eric Fahey <eric.fahey@ryerson.ca>
 */
public class Replication {

    public static final int THREADS = 2;

    public static ArrayList<Transaction> transactions = new ArrayList<>();

    public static void main(String[] args) {
        try {
            //Create Transactions and create DB connections
            for (int i = 0; i < THREADS; i++) {
                Transaction transaction = new Transaction();
                transactions.add(transaction);
            }

            //Start the threads
            for (Transaction transaction : transactions) {
                transaction.start();
            }

            //Exit the threads
            for (Transaction transaction : transactions) {
                transaction.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
