package tandoori.neo4jBolt;

import java.io.IOException;
import codesmells.annotations.LM;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class UnsupportedHardwareAccelerationQuery extends Query {
    private UnsupportedHardwareAccelerationQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
    }

    public static UnsupportedHardwareAccelerationQuery createUnsupportedHardwareAccelerationQuery(QueryEngineBolt queryEngine) {
        return new UnsupportedHardwareAccelerationQuery(queryEngine);
    }

    @Override
    @LM
    public void execute(boolean details) throws IOException {
        StatementResult result;
        String[] uhas = new String[]{ "drawPicture#android.graphics.Canvas" , "drawVertices#android.graphics.Canvas" , "drawPosText#android.graphics.Canvas" , "drawTextOnPath#android.graphics.Canvas" , "drawPath#android.graphics.Canvas" , "setLinearText#android.graphics.Paint" , "setMaskFilter#android.graphics.Paint" , "setPathEffect#android.graphics.Paint" , "setRasterizer#android.graphics.Paint" , "setSubpixelText#android.graphics.Paint" };
        StringBuilder query = new StringBuilder((((("MATCH (m:Method  {app_key:" + (queryEngine.getKeyApp())) + "})-[:CALLS]->(e:ExternalMethod) WHERE e.full_name='") + (uhas[0])) + "'"));
        for (int i = 1; i < (uhas.length); i++) {
            query.append(((" OR e.full_name='" + (uhas[i])) + "' "));
        }
        query.append("return m as nod,m.app_key");
        if (details) {
            query.append(",m.full_name as full_name");
        }else {
            query.append(",count(m) as UHA");
        }
        try (Transaction tx = this.session.beginTransaction()) {
            result = tx.run(query.toString());
            queryEngine.resultToCSV(result, "UHA");
            tx.success();
        }
    }
}

