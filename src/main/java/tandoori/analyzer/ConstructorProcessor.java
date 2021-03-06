package tandoori.analyzer;

import java.util.Arrays;
import java.util.List;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.visitor.filter.TypeFilter;
import tandoori.entities.PaprikaArgument;
import tandoori.entities.PaprikaMethod;
import tandoori.entities.PaprikaModifiers;

/**
 * Created by sarra on 08/03/17.
 */
public class ConstructorProcessor {
    public void process(CtConstructor ctConstructor) {

        String name = ctConstructor.getSimpleName();
        String returnType = ctConstructor.getType().getQualifiedName();

        String visibility = ctConstructor.getVisibility() == null ? "null" : ctConstructor.getVisibility().toString();
        PaprikaModifiers paprikaModifiers = DataConverter.convertTextToModifier(visibility);
        if (paprikaModifiers == null) {
            paprikaModifiers = PaprikaModifiers.PROTECTED;
        }
        int position = 0;
        String qualifiedName;
        PaprikaMethod paprikaMethod = PaprikaMethod.createPaprikaMethod(name, paprikaModifiers, returnType,
                MainProcessor.currentClass);
        MainProcessor.currentMethod = paprikaMethod;
        for (CtParameter<?> ctParameter : (List<CtParameter>) ctConstructor.getParameters()) {
            qualifiedName = ctParameter.getType().getQualifiedName();
            PaprikaArgument.createPaprikaArgument(qualifiedName, position, paprikaMethod);
            position++;
        }
        int numberOfDeclaredLocals = ctConstructor.getElements(new TypeFilter<CtLocalVariable>(CtLocalVariable.class)).size();
        paprikaMethod.setNumberOfLines(countEffectiveCodeLines(ctConstructor));
        paprikaMethod.setConstructor(true);
        handleUsedVariables(ctConstructor, paprikaMethod);
        handleInvocations(ctConstructor, paprikaMethod);
        paprikaMethod.setComplexity(getComplexity(ctConstructor));
        paprikaMethod.setNumberOfDeclaredLocals(numberOfDeclaredLocals);


    }

    private int countEffectiveCodeLines(CtConstructor ctMethod) {
        try {
            return ctMethod.getBody().toString().split("\n").length;
        }catch (NullPointerException npe){
            return ctMethod.getPosition().getEndLine()-ctMethod.getPosition().getLine();
        }

    }

    private void handleUsedVariables(CtConstructor ctConstructor, PaprikaMethod paprikaMethod) {
        List<CtFieldAccess> elements = ctConstructor.getElements(new TypeFilter<CtFieldAccess>(CtFieldAccess.class));
        String variableTarget = null;
        String variableName;


        for (CtFieldAccess ctFieldAccess : elements) {
            if (ctFieldAccess.getTarget() != null && ctFieldAccess.getTarget().getType() != null) {
                if(ctFieldAccess.getTarget().getType().getDeclaration() == ctConstructor.getDeclaringType()){
                    variableTarget = ctFieldAccess.getTarget().getType().getQualifiedName();
                    variableName = ctFieldAccess.getVariable().getSimpleName();
                    paprikaMethod.getUsedVariablesData().add(new VariableData(variableTarget, variableName));
                }

            }


        }

    }

    private void handleInvocations(CtConstructor ctConstructor, PaprikaMethod paprikaMethod) {
        String targetName;
        String executable;
        String type="Uknown";
        List<CtInvocation> invocations = ctConstructor.getElements(new TypeFilter<CtInvocation>(CtInvocation.class));
        for (CtInvocation invocation : invocations) {
            targetName = getTarget(invocation);
            executable = invocation.getExecutable().getSimpleName();
            if(invocation.getExecutable().getType()!=null){
                type=invocation.getExecutable().getType().getQualifiedName();
            }
            if (targetName != null) {
                paprikaMethod.getInvocationData().add(new InvocationData(targetName, executable,type));
            }
        }
    }

    private String getTarget(CtInvocation ctInvocation) {
        if (ctInvocation.getTarget() instanceof CtInvocation) {
            return getTarget((CtInvocation) ctInvocation.getTarget());
        } else {
            try {
                return ctInvocation.getExecutable().getDeclaringType().getQualifiedName();
            } catch (NullPointerException nullPointerException) {
                nullPointerException.printStackTrace();
            }
        }
        return null;
    }

    private int getComplexity(CtConstructor<?> ctConstructor) {
        int numberOfTernaries = ctConstructor.getElements(new TypeFilter<CtConditional>(CtConditional.class)).size();
        int numberOfIfs = ctConstructor.getElements(new TypeFilter<CtIf>(CtIf.class)).size();
        int numberOfCases = ctConstructor.getElements(new TypeFilter<CtCase>(CtCase.class)).size();
        int numberOfReturns = ctConstructor.getElements(new TypeFilter<CtReturn>(CtReturn.class)).size();
        int numberOfLoops = ctConstructor.getElements(new TypeFilter<CtLoop>(CtLoop.class)).size();
        int numberOfBinaryOperators = ctConstructor.getElements(new TypeFilter<CtBinaryOperator>(CtBinaryOperator.class) {
            private final List<BinaryOperatorKind> operators = Arrays.asList(BinaryOperatorKind.AND, BinaryOperatorKind.OR);

            @Override
            public boolean matches(CtBinaryOperator element) {
                return super.matches(element) && operators.contains(element.getKind());
            }
        }).size();
        int numberOfCatches = ctConstructor.getElements(new TypeFilter<CtCatch>(CtCatch.class)).size();
        int numberOfThrows = ctConstructor.getElements(new TypeFilter<CtThrow>(CtThrow.class)).size();
        int numberOfBreaks = ctConstructor.getElements(new TypeFilter<CtBreak>(CtBreak.class)).size();
        int numberOfContinues = ctConstructor.getElements(new TypeFilter<CtContinue>(CtContinue.class)).size();
        return numberOfBreaks + numberOfCases + numberOfCatches + numberOfContinues + numberOfIfs + numberOfLoops +
                numberOfReturns + numberOfTernaries + numberOfThrows + numberOfBinaryOperators + 1;
    }
}
