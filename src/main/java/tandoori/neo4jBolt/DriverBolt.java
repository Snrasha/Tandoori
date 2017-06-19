package tandoori.neo4jBolt;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import java.net.InetAddress;
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;
import org.neo4j.driver.v1.Session;

public class DriverBolt {
    private static String port = "7687";

    private static String user = "neo4j";

    private static String pwd = "paprika";

    private static String containerDocker = "neo4j-paprika";

    private static String hostname = null;

    private static Driver driver = null;

    private DriverBolt() {
    }

    private static String getHostName() {
        if ((DriverBolt.hostname) != null)
            return DriverBolt.hostname;
        
        try {
            String str = InetAddress.getByName(DriverBolt.containerDocker).getHostAddress();
            return str;
        } catch (final Exception e) {
            return "localhost";
        }
    }

    public static void setValue(String port, String user, String pwd) {
        if (port != null)
            DriverBolt.port = port;
        
        if (user != null)
            DriverBolt.user = user;
        
        if (pwd != null)
            DriverBolt.pwd = pwd;
        
    }

    public static void setDockerNeo4j(String nameContainer) {
        if (nameContainer != null)
            DriverBolt.containerDocker = nameContainer;
        
        DriverBolt.hostname = null;
    }

    public static void setHostName(String hostname) {
        if (hostname != null)
            DriverBolt.hostname = hostname;
        
        DriverBolt.containerDocker = null;
    }

    public static void updateDriver() {
        if ((DriverBolt.driver) != null)
            DriverBolt.driver.close();
        
        DriverBolt.driver = GraphDatabase.driver(((("bolt://" + (DriverBolt.getHostName())) + ":") + (DriverBolt.port)), AuthTokens.basic(DriverBolt.user, DriverBolt.pwd));
    }

    public static Session getSession() {
        Session session = null;
        if ((DriverBolt.driver) == null)
            DriverBolt.updateDriver();
        
        try {
            session = DriverBolt.driver.session();
        } catch (ServiceUnavailableException e) {
            DriverBolt.updateDriver();
            session = DriverBolt.driver.session();
        }
        return session;
    }
}

