import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

/**
 * @author Eric Fahey <eric.fahey@ryerson.ca>
 */
public class DatabaseConnection extends Thread {

    private final String hostname;
    private final int port;
    private final String username;
    private final String password;
    private final ServiceType serviceType;

    private Connection connection;

    private static Semaphore semaphore = new Semaphore(1);
    private static int count = 0;

    public DatabaseConnection(String hostname, int port, String username, String password, ServiceType serviceType) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.serviceType = serviceType;
    }

    public void init() {
        try {
            this.connection = DriverManager.getConnection(getURL());
            System.out.println("Successfully Created Connection " + (++count));
            this.connection.setAutoCommit(false);
            this.start();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Called by Thread.start()
    public void run() {
        try {
            semaphore.acquire();

        } catch (Exception e) {
            e.printStackTrace();
        }
        semaphore.release();
    }

    public String getURL() {
        return "jdbc:oracle:thin:" + username + "/" + password + "@" + hostname + ":" + port + ":" + serviceType.getSid();
    }
}
