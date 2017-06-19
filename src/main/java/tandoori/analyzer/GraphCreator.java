package tandoori.analyzer;

import java.util.ArrayList;
import tandoori.entities.Entity;
import codesmells.annotations.LM;
import tandoori.entities.PaprikaApp;
import tandoori.entities.PaprikaClass;
import tandoori.entities.PaprikaExternalClass;
import tandoori.entities.PaprikaExternalMethod;
import tandoori.entities.PaprikaMethod;
import tandoori.entities.PaprikaVariable;

public class GraphCreator {
    PaprikaApp paprikaApp;

    public GraphCreator(PaprikaApp paprikaApp) {
        this.paprikaApp = paprikaApp;
    }

    @LM
    public void createCallGraph() {
        Entity targetClass;
        Entity targetMethod;
        PaprikaClass paprikaClass;
        PaprikaVariable paprikaVariable;
        ArrayList<PaprikaMethod> paprikaMethods = paprikaApp.getMethods();
        for (PaprikaMethod paprikaMethod : paprikaMethods) {
            for (InvocationData invocationData : paprikaMethod.getInvocationData()) {
                targetClass = paprikaApp.getPaprikaClass(invocationData.getTarget());
                if (targetClass instanceof PaprikaClass) {
                    targetMethod = ((PaprikaClass) (targetClass)).getPaprikaMethod(invocationData.getMethod());
                }else {
                    targetMethod = PaprikaExternalMethod.createPaprikaExternalMethod(invocationData.getMethod(), invocationData.getType(), ((PaprikaExternalClass) (targetClass)));
                }
                paprikaMethod.callMethod(targetMethod);
            }
            for (VariableData variableData : paprikaMethod.getUsedVariablesData()) {
                paprikaClass = paprikaApp.getPaprikaInternalClass(variableData.getClassName());
                if (paprikaClass != null) {
                    paprikaVariable = paprikaClass.findVariable(variableData.getVariableName());
                    if (paprikaVariable != null) {
                        paprikaMethod.useVariable(paprikaVariable);
                    }
                }
            }
        }
    }

    @LM
    public void createClassHierarchy() {
        for (PaprikaClass paprikaClass : paprikaApp.getPaprikaClasses()) {
            String parentName = paprikaClass.getParentName();
            PaprikaClass implementedInterface;
            if (parentName != null) {
                PaprikaClass parentClass = paprikaClass.getPaprikaApp().getPaprikaInternalClass(parentName);
                paprikaClass.setParent(parentClass);
                if (parentClass != null) {
                    parentClass.addChild();
                    paprikaClass.setParentName(null);
                }
            }
            for (String interfaceName : paprikaClass.getInterfacesNames()) {
                implementedInterface = paprikaClass.getPaprikaApp().getPaprikaInternalClass(interfaceName);
                if (implementedInterface != null) {
                    paprikaClass.implement(implementedInterface);
                }
            }
        }
    }
}

