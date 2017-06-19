package tandoori.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaprikaExternalMethod extends Entity {
    private PaprikaExternalClass paprikaExternalClass;

    private List<PaprikaExternalArgument> paprikaExternalArguments;

    private String returnType;

    private static Map<String, PaprikaExternalMethod> externalMethods = new HashMap<>();

    public String getReturnType() {
        return returnType;
    }

    public List<PaprikaExternalArgument> getPaprikaExternalArguments() {
        return paprikaExternalArguments;
    }

    private PaprikaExternalMethod(String name, String returnType, PaprikaExternalClass paprikaExternalClass) {
        this.setName(name);
        this.paprikaExternalClass = paprikaExternalClass;
        this.returnType = returnType;
        this.paprikaExternalArguments = new ArrayList<>();
    }

    public static PaprikaExternalMethod createPaprikaExternalMethod(String name, String returnType, PaprikaExternalClass paprikaClass) {
        String fullName = (name + "#") + paprikaClass;
        PaprikaExternalMethod paprikaMethod;
        if ((paprikaMethod = PaprikaExternalMethod.externalMethods.get(fullName)) != null) {
            return paprikaMethod;
        }
        paprikaMethod = new PaprikaExternalMethod(name, returnType, paprikaClass);
        PaprikaExternalMethod.externalMethods.put(fullName, paprikaMethod);
        paprikaClass.addPaprikaExternalMethod(paprikaMethod);
        return paprikaMethod;
    }

    public PaprikaExternalClass getPaprikaExternalClass() {
        return paprikaExternalClass;
    }

    public void setPaprikaExternalClass(PaprikaExternalClass paprikaClass) {
        this.paprikaExternalClass = paprikaClass;
    }

    @Override
    public String toString() {
        return ((this.getName()) + "#") + (paprikaExternalClass);
    }

    public void addExternalArgument(PaprikaExternalArgument paprikaExternalArgument) {
        this.paprikaExternalArguments.add(paprikaExternalArgument);
    }
}

