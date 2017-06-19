package tandoori.analyzer;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtField;
import tandoori.entities.PaprikaClass;
import spoon.reflect.declaration.CtInterface;
import java.net.URLClassLoader;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import tandoori.entities.PaprikaModifiers;
import java.net.URL;
import codesmells.annotations.LM;
import spoon.reflect.declaration.ModifierKind;
import tandoori.entities.PaprikaVariable;
import java.util.List;

public class InterfaceProcessor extends AbstractProcessor<CtInterface> {
    private static final URLClassLoader classloader;

    static {
        classloader = new URLClassLoader(MainProcessor.paths.toArray(new URL[MainProcessor.paths.size()]));
    }

    @Override
    @LM
    public void process(CtInterface ctInterface) {
        String qualifiedName = ctInterface.getQualifiedName();
        if (ctInterface.isAnonymous()) {
            String[] splitName = qualifiedName.split("\\$");
            qualifiedName = (((splitName[0]) + "$") + (((CtNewClass) (ctInterface.getParent())).getType().getQualifiedName())) + (splitName[1]);
        }
        String visibility = ((ctInterface.getVisibility()) == null) ? "null" : ctInterface.getVisibility().toString();
        PaprikaModifiers paprikaModifiers = DataConverter.convertTextToModifier(visibility);
        if (paprikaModifiers == null) {
            paprikaModifiers = PaprikaModifiers.DEFAULT;
        }
        PaprikaClass paprikaClass = PaprikaClass.createPaprikaClass(qualifiedName, MainProcessor.currentApp, paprikaModifiers);
        MainProcessor.currentClass = paprikaClass;
        handleProperties(ctInterface, paprikaClass);
        handleAttachments(ctInterface, paprikaClass);
        if (ctInterface.getQualifiedName().contains("$")) {
            paprikaClass.setInnerClass(true);
        }
        processMethods(ctInterface);
    }

    public void processMethods(CtInterface ctInterface) {
        MethodProcessor methodProcessor = new MethodProcessor();
        for (Object o : ctInterface.getMethods()) {
            methodProcessor.process(((CtMethod) (o)));
        }
    }

    @LM
    public void handleAttachments(CtInterface ctInterface, PaprikaClass paprikaClass) {
        if ((ctInterface.getSuperclass()) != null) {
            paprikaClass.setParentName(ctInterface.getSuperclass().getQualifiedName());
        }
        for (CtTypeReference<?> ctTypeReference : ctInterface.getSuperInterfaces()) {
            paprikaClass.getInterfacesNames().add(ctTypeReference.getQualifiedName());
        }
        String modifierText;
        PaprikaModifiers paprikaModifiers1;
        for (CtField<?> ctField : ((List<CtField>) (ctInterface.getFields()))) {
            modifierText = ((ctField.getVisibility()) == null) ? "null" : ctField.getVisibility().toString();
            paprikaModifiers1 = DataConverter.convertTextToModifier(modifierText);
            if (paprikaModifiers1 == null) {
                paprikaModifiers1 = PaprikaModifiers.PROTECTED;
            }
            PaprikaVariable.createPaprikaVariable(ctField.getSimpleName(), ctField.getType().getQualifiedName(), paprikaModifiers1, paprikaClass);
        }
    }

    @LM
    public void handleProperties(CtInterface ctInterface, PaprikaClass paprikaClass) {
        int doi = 0;
        boolean isStatic = false;
        for (ModifierKind modifierKind : ctInterface.getModifiers()) {
            if (modifierKind.toString().toLowerCase().equals("static")) {
                isStatic = true;
                break;
            }
        }
        CtType myClass = ctInterface;
        boolean noSuperClass = false;
        if ((ctInterface.getSuperclass()) != null) {
            Class myRealClass;
            CtTypeReference reference = null;
            while (myClass != null) {
                doi++;
                if ((myClass.getSuperclass()) != null) {
                    reference = myClass.getSuperclass();
                    myClass = myClass.getSuperclass().getDeclaration();
                }else {
                    noSuperClass = true;
                    myClass = null;
                }
            } 
            if (!noSuperClass) {
                try {
                    myRealClass = InterfaceProcessor.classloader.loadClass(reference.getQualifiedName());
                    while ((myRealClass.getSuperclass()) != null) {
                        doi++;
                        myRealClass = myRealClass.getSuperclass();
                    } 
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoClassDefFoundError e) {
                    e.printStackTrace();
                }
            }
        }
        paprikaClass.setInterface(true);
        paprikaClass.setDepthOfInheritance(doi);
        paprikaClass.setStatic(isStatic);
    }
}

