/**
 * @author Eric Fahey <eric.fahey@cgi.com>
 */
public class Replication {

    public static void main(String[] args) {
        new DatabaseConnection("localhost", 1522, "SYSTEM", "admin", ServiceType.XE).init();
        new DatabaseConnection("localhost", 1523, "SYSTEM", "admin", ServiceType.XE).init();
    }

}
