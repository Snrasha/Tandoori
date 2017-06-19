package tandoori.entities;

import java.util.ArrayList;
import codesmells.annotations.CC;
import java.util.HashSet;
import codesmells.annotations.IGS;
import codesmells.annotations.LM;
import java.util.Set;

@CC
public class PaprikaClass extends Entity {
    private PaprikaApp paprikaApp;

    private PaprikaClass parent;

    private String parentName;

    private int children;

    private int complexity;

    private Set<PaprikaClass> coupled;

    private Set<PaprikaMethod> paprikaMethods;

    private Set<PaprikaVariable> paprikaVariables;

    private Set<PaprikaClass> interfaces;

    private PaprikaModifiers modifier;

    private boolean isInterface;

    private boolean isStatic;

    private boolean isActivity;

    private boolean isBroadcastReceiver;

    private boolean isService;

    private boolean isContentProvider;

    private boolean isView;

    private boolean isAsyncTask;

    private boolean isApplication;

    private boolean isInnerClass;

    private int depthOfInheritance;

    private ArrayList<String> interfacesNames;

    public PaprikaModifiers getModifier() {
        return modifier;
    }

    public Set<PaprikaVariable> getPaprikaVariables() {
        return paprikaVariables;
    }

    public Set<PaprikaMethod> getPaprikaMethods() {
        return paprikaMethods;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @LM
    private PaprikaClass(String name, PaprikaApp paprikaApp, PaprikaModifiers modifier) {
        this.setName(name);
        this.paprikaApp = paprikaApp;
        this.children = 0;
        this.paprikaMethods = new HashSet<>();
        this.paprikaVariables = new HashSet<>();
        this.coupled = new HashSet<>();
        this.interfaces = new HashSet<>();
        this.modifier = modifier;
        this.isInterface = false;
        this.isStatic = false;
        this.isActivity = false;
        this.isApplication = false;
        this.isAsyncTask = false;
        this.isService = false;
        this.isContentProvider = false;
        this.isBroadcastReceiver = false;
        this.isInnerClass = false;
        this.isView = false;
        this.depthOfInheritance = 0;
        this.interfacesNames = new ArrayList<>();
        complexity = 0;
    }

    public static PaprikaClass createPaprikaClass(String name, PaprikaApp paprikaApp, PaprikaModifiers modifier) {
        PaprikaClass paprikaClass = new PaprikaClass(name, paprikaApp, modifier);
        paprikaApp.addPaprikaClass(paprikaClass);
        return paprikaClass;
    }

    public PaprikaClass getParent() {
        return parent;
    }

    public Set<PaprikaClass> getInterfaces() {
        return interfaces;
    }

    public void setParent(PaprikaClass parent) {
        this.parent = parent;
    }

    public void addPaprikaMethod(PaprikaMethod paprikaMethod) {
        paprikaMethods.add(paprikaMethod);
    }

    public PaprikaApp getPaprikaApp() {
        return paprikaApp;
    }

    public void setPaprikaApp(PaprikaApp paprikaApp) {
        this.paprikaApp = paprikaApp;
    }

    public void addChild() {
        children += 1;
    }

    @IGS
    public int computeComplexity() {
        for (PaprikaMethod paprikaMethod : this.getPaprikaMethods()) {
            this.complexity += paprikaMethod.getComplexity();
        }
        return this.complexity;
    }

    public int getChildren() {
        return children;
    }

    public void coupledTo(PaprikaClass paprikaClass) {
        coupled.add(paprikaClass);
    }

    public void implement(PaprikaClass paprikaClass) {
        interfaces.add(paprikaClass);
    }

    public int getCouplingValue() {
        return coupled.size();
    }

    public int computeLCOM() {
        Object[] methods = paprikaMethods.toArray();
        int methodCount = methods.length;
        int haveFieldInCommon = 0;
        int noFieldInCommon = 0;
        for (int i = 0; i < methodCount; i++) {
            for (int j = i + 1; j < methodCount; j++) {
                if (((PaprikaMethod) (methods[i])).haveCommonFields(((PaprikaMethod) (methods[j])))) {
                    haveFieldInCommon++;
                }else {
                    noFieldInCommon++;
                }
            }
        }
        int LCOM = noFieldInCommon - haveFieldInCommon;
        return LCOM > 0 ? LCOM : 0;
    }

    public double computeNPathComplexity() {
        return Math.pow(2.0, ((double) (this.complexity)));
    }

    public void addPaprikaVariable(PaprikaVariable paprikaVariable) {
        paprikaVariables.add(paprikaVariable);
    }

    public PaprikaVariable findVariable(String name) {
        for (PaprikaVariable paprikaVariable : paprikaVariables) {
            if (paprikaVariable.getName().equals(name))
                return paprikaVariable;
            
        }
        return null;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(boolean anInterface) {
        isInterface = anInterface;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public boolean isActivity() {
        return isActivity;
    }

    public void setActivity(boolean activity) {
        isActivity = activity;
    }

    public ArrayList<String> getInterfacesNames() {
        return interfacesNames;
    }

    public void setInterfacesNames(ArrayList<String> interfacesNames) {
        this.interfacesNames = interfacesNames;
    }

    @IGS
    public PaprikaMethod getPaprikaMethod(String methodName) {
        for (PaprikaMethod paprikaMethod : this.getPaprikaMethods()) {
            if (paprikaMethod.getName().equals(methodName)) {
                return paprikaMethod;
            }
        }
        return PaprikaMethod.createPaprikaMethod(methodName, PaprikaModifiers.PUBLIC, "Uknown", this);
    }

    public int getDepthOfInheritance() {
        return depthOfInheritance;
    }

    public void setDepthOfInheritance(int depthOfInheritance) {
        this.depthOfInheritance = depthOfInheritance;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public Set<PaprikaClass> getCoupled() {
        return coupled;
    }

    public void setCoupled(Set<PaprikaClass> coupled) {
        this.coupled = coupled;
    }

    public void setPaprikaMethods(Set<PaprikaMethod> paprikaMethods) {
        this.paprikaMethods = paprikaMethods;
    }

    public void setPaprikaVariables(Set<PaprikaVariable> paprikaVariables) {
        this.paprikaVariables = paprikaVariables;
    }

    public void setInterfaces(Set<PaprikaClass> interfaces) {
        this.interfaces = interfaces;
    }

    public void setModifier(PaprikaModifiers modifier) {
        this.modifier = modifier;
    }

    public boolean isBroadcastReceiver() {
        return isBroadcastReceiver;
    }

    public void setBroadcastReceiver(boolean broadcastReceiver) {
        isBroadcastReceiver = broadcastReceiver;
    }

    public boolean isService() {
        return isService;
    }

    public void setService(boolean service) {
        isService = service;
    }

    public boolean isContentProvider() {
        return isContentProvider;
    }

    public void setContentProvider(boolean contentProvider) {
        isContentProvider = contentProvider;
    }

    public boolean isView() {
        return isView;
    }

    public void setView(boolean view) {
        isView = view;
    }

    public boolean isAsyncTask() {
        return isAsyncTask;
    }

    public void setAsyncTask(boolean asyncTask) {
        isAsyncTask = asyncTask;
    }

    public boolean isApplication() {
        return isApplication;
    }

    public void setApplication(boolean application) {
        isApplication = application;
    }

    public boolean isInnerClass() {
        return isInnerClass;
    }

    public void setInnerClass(boolean innerClass) {
        isInnerClass = innerClass;
    }
}

