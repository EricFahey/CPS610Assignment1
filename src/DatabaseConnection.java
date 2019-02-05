import java.sql.*;

/**
 * @author Eric Fahey <eric.fahey@ryerson.ca>
 */
public class DatabaseConnection {

    private final String hostname;
    private final int port;
    private final String username;
    private final String password;
    private final ServiceType serviceType;

    private Connection connection;

    public DatabaseConnection(String hostname, int port, String username, String password, ServiceType serviceType) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.serviceType = serviceType;
    }

    public boolean connect() {
        try {
            String url = getURL();
            this.connection = DriverManager.getConnection(url);
            System.out.println("Successfully Created Connection to site " + getSite() + "!");
            this.connection.setAutoCommit(false);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  false;
    }

    public boolean isUnique(String name) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT NAME FROM STUDENT");
            boolean found = false;
            while (resultSet.next()) {
                if (resultSet.getString("Name").equals(name)) {
                    found = true;
                }

            }
            if (!found) {
                System.out.println(name + " doesn't exist in site " + getSite() + "!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Unable to Query Student Table");
            e.printStackTrace();
        }
        System.out.println(name + " already exists in site " + getSite() + "!");
        return false;
    }

    public boolean insertName(String name) {
        try {
            Statement statement = connection.createStatement();
            statement.executeQuery("INSERT INTO STUDENT VALUES('" + name + "')");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Unable to Insert " + name + " to site " + getSite() + "!");
        return false;
    }

    public boolean deleteName(String name) {
        try {
            Statement statement = connection.createStatement();
            statement.executeQuery("DELETE FROM STUDENT WHERE NAME = '" + name + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Unable to delete " + name + " from site " + getSite() + "!");
        return false;
    }

    public Connection getConnection() {
        return connection;
    }

    public int getSite() {
        if (port == 1522)
            return 1;
        else if (port == 1523)
            return 2;
        return 0;
    }

    public String getURL() {
        return "jdbc:oracle:thin:" + username + "/" + password + "@" + hostname + ":" + port + ":" + serviceType.getSid();
    }
}
