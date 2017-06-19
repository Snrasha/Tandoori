package tandoori.neo4jBolt;

import net.sourceforge.jFuzzyLogic.FIS;
import java.io.File;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import java.io.IOException;

public abstract class FuzzyQuery extends Query {
    protected String fclFile;

    public FuzzyQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
    }

    public abstract void executeFuzzy(boolean details) throws IOException;

    protected FunctionBlock fuzzyFunctionBlock() {
        File fcf = new File(("fcl" + (fclFile)));
        FIS fis;
        if ((fcf.exists()) && (!(fcf.isDirectory()))) {
            fis = FIS.load(("fcl" + (fclFile)), false);
        }else {
            fis = FIS.load(getClass().getResourceAsStream(("fcl" + (fclFile))), false);
        }
        return fis.getFunctionBlock(null);
    }
}

