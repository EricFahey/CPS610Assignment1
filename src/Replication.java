import javax.xml.crypto.Data;
import java.util.ArrayList;

/**
 * @author Eric Fahey <eric.fahey@cgi.com>
 */
public class Replication {

    public static ArrayList<DatabaseConnection> connections = new ArrayList<>();

    public static void main(String[] args) {
        connections.add(new DatabaseConnection("localhost", 1522, "SYSTEM", "admin", ServiceType.XE));
        connections.add(new DatabaseConnection("localhost", 1523, "SYSTEM", "admin", ServiceType.XE));

        for (DatabaseConnection connection : connections) {
            connection.insertName("Eric Fahey");
            connection.start();
        }

        for (DatabaseConnection connection : connections) {
            if
        }
    }

}
