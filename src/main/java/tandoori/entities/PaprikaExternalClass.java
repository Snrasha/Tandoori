package tandoori.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PaprikaExternalClass extends Entity {
    private PaprikaApp paprikaApp;

    private String parentName;

    private Set<PaprikaExternalMethod> paprikaExternalMethods;

    private static Map<String, PaprikaExternalClass> externalClasses = new HashMap<>();

    public Set<PaprikaExternalMethod> getPaprikaExternalMethods() {
        return paprikaExternalMethods;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    private PaprikaExternalClass(String name, PaprikaApp paprikaApp) {
        this.setName(name);
        this.paprikaApp = paprikaApp;
        this.paprikaExternalMethods = new HashSet<>();
    }

    public static PaprikaExternalClass createPaprikaExternalClass(String name, PaprikaApp paprikaApp) {
        PaprikaExternalClass paprikaClass;
        if ((paprikaClass = PaprikaExternalClass.externalClasses.get(name)) != null) {
            return paprikaClass;
        }
        paprikaClass = new PaprikaExternalClass(name, paprikaApp);
        PaprikaExternalClass.externalClasses.put(name, paprikaClass);
        paprikaApp.addPaprikaExternalClass(paprikaClass);
        return paprikaClass;
    }

    public void addPaprikaExternalMethod(PaprikaExternalMethod paprikaMethod) {
        paprikaExternalMethods.add(paprikaMethod);
    }

    public PaprikaApp getPaprikaApp() {
        return paprikaApp;
    }

    public void setPaprikaApp(PaprikaApp paprikaApp) {
        this.paprikaApp = paprikaApp;
    }
}

