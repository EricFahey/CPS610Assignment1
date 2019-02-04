import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * @author Eric Fahey <eric.fahey@ryerson.ca>
 */
public class DatabaseConnection extends Thread {

    private static Semaphore semaphore = new Semaphore(1);
    private static int threadCount = 0;


    private final String hostname;
    private final int port;
    private final String username;
    private final String password;
    private final ServiceType serviceType;

    private Connection connection;
    private int site;
    private Runnable task;
    private boolean success;

    public DatabaseConnection(String hostname, int port, String username, String password, ServiceType serviceType) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.serviceType = serviceType;
    }

    public void start() {
        try {
            this.connection = DriverManager.getConnection(getURL());
            this.site = ++threadCount;
            System.out.println("Successfully Created Connection " + site);
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
            task.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        semaphore.release();
    }

    public void insertName(final String name) {
        this.task = () -> {
            try {
                ArrayList<String> names = new ArrayList<>();
                Statement statement;
                ResultSet resultSet;
                try {
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery("SELECT NAME FROM STUDENT");
                    while (resultSet.next()) {
                        names.add(resultSet.getString("Name"));
                    }
                } catch (SQLException e) {
                    System.out.println("Unable to Query Student Table");
                    throw e;
                }
                if (names.contains(name)) {
                    System.out.println("Name Already Exists in Database Site " + site);
                } else {
                    try {
                        statement = connection.createStatement();
                        statement.executeQuery("INSERT INTO STUDENT VALUES('" + name + "')");
                        success = true;
//                        connection.commit();
                    } catch (SQLException e) {
                        System.out.println("Unable to Insert " + name + " in Student Table");
                        throw e;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                success = false;
            }
            //Gracefully Close
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Error Closing Connection for Site " + site);
                e.printStackTrace();
            }
        };
    }

    public static Semaphore getSemaphore() {
        return semaphore;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getURL() {
        return "jdbc:oracle:thin:" + username + "/" + password + "@" + hostname + ":" + port + ":" + serviceType.getSid();
    }
}
