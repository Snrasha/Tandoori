package tandoori.neo4j;

import java.io.File;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import codesmells.annotations.LIC;

public class DatabaseManager {
    private final String DB_PATH;

    private GraphDatabaseService graphDatabaseService;

    public DatabaseManager(String DB_PATH) {
        this.DB_PATH = DB_PATH;
    }

    public void start() {
        File dbFile = new File(DB_PATH);
        graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(DB_PATH).newGraphDatabase();
        DatabaseManager.registerShutdownHook(graphDatabaseService);
    }

    public void deleteDB() {
        shutDown();
        DatabaseManager.deleteFileOrDirectory(new File(DB_PATH));
    }

    public void shutDown() {
        graphDatabaseService.shutdown();
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

    public GraphDatabaseService getGraphDatabaseService() {
        return graphDatabaseService;
    }

    private static void deleteFileOrDirectory(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    DatabaseManager.deleteFileOrDirectory(child);
                }
            }
            file.delete();
        }
    }
}

