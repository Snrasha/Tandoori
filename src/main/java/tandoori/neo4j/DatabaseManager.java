package tandoori.neo4j;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;


public class DatabaseManager {
    private final String DB_PATH;
    private GraphDatabaseService graphDatabaseService;

    public DatabaseManager(String DB_PATH) {
        this.DB_PATH = DB_PATH;

    }

    public void start(){
        //graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        File dbFile = new File(DB_PATH);
        graphDatabaseService = new GraphDatabaseFactory().
                newEmbeddedDatabaseBuilder(DB_PATH).
                newGraphDatabase();
        registerShutdownHook(graphDatabaseService);
    }

    public void deleteDB(){
        shutDown();
        deleteFileOrDirectory(new File( DB_PATH));
    }

    public void shutDown()
    {
        graphDatabaseService.shutdown();
    }

    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }

    public GraphDatabaseService getGraphDatabaseService(){
        return graphDatabaseService;
    }

    private static void deleteFileOrDirectory( File file )
    {
        if ( file.exists() )
        {
            if ( file.isDirectory() )
            {
                for ( File child : file.listFiles() )
                {
                    deleteFileOrDirectory( child );
                }
            }
            file.delete();
        }
    }
}
