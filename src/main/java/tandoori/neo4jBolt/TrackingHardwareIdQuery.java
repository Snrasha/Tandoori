package tandoori.neo4jBolt;

import java.io.IOException;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class TrackingHardwareIdQuery extends Query {
    private TrackingHardwareIdQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
    }

    public static TrackingHardwareIdQuery createTrackingHardwareIdQuery(QueryEngineBolt queryEngine) {
        return new TrackingHardwareIdQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (m1:Method  {app_key:" + (queryEngine.getKeyApp())) + "})-[:CALLS]->(:ExternalMethod { full_name:'getDeviceId#android.telephony.TelephonyManager'})   RETURN m1 as nod,m1.app_key as app_key";
            if (details) {
                query += ",m1.full_name as full_name";
            }else {
                query += ",count(m1) as THI";
            }
            StatementResult result = tx.run(query);
            queryEngine.resultToCSV(result, "THI");
            tx.success();
        }
    }
}

