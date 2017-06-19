package tandoori.analyzer;

import spoon.processing.AbstractProcessor;
import codesmells.annotations.CC;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import tandoori.entities.PaprikaClass;
import java.util.List;
import tandoori.entities.PaprikaModifiers;
import java.net.URL;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.CtField;
import codesmells.annotations.LM;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtType;
import java.net.URLClassLoader;
import spoon.reflect.reference.CtTypeReference;
import tandoori.entities.PaprikaVariable;

@CC
public class ClassProcessor extends AbstractProcessor<CtClass> {
    private static final URLClassLoader classloader;

    static {
        classloader = new URLClassLoader(MainProcessor.paths.toArray(new URL[MainProcessor.paths.size()]));
    }

    @LM
    public void process(CtClass ctClass) {
        String qualifiedName = ctClass.getQualifiedName();
        if (ctClass.isAnonymous()) {
            String[] splitName = qualifiedName.split("\\$");
            qualifiedName = (((splitName[0]) + "$") + (((CtNewClass) (ctClass.getParent())).getType().getQualifiedName())) + (splitName[1]);
        }
        String visibility = ((ctClass.getVisibility()) == null) ? "null" : ctClass.getVisibility().toString();
        PaprikaModifiers paprikaModifiers = DataConverter.convertTextToModifier(visibility);
        if (paprikaModifiers == null) {
            paprikaModifiers = PaprikaModifiers.DEFAULT;
        }
        PaprikaClass paprikaClass = PaprikaClass.createPaprikaClass(qualifiedName, MainProcessor.currentApp, paprikaModifiers);
        MainProcessor.currentClass = paprikaClass;
        handleProperties(ctClass, paprikaClass);
        handleAttachments(ctClass, paprikaClass);
        if (ctClass.getQualifiedName().contains("$")) {
            paprikaClass.setInnerClass(true);
        }
        processMethods(ctClass);
    }

    public void processMethods(CtClass ctClass) {
        MethodProcessor methodProcessor = new MethodProcessor();
        ConstructorProcessor constructorProcessor = new ConstructorProcessor();
        for (Object o : ctClass.getMethods()) {
            methodProcessor.process(((CtMethod) (o)));
        }
        CtConstructor ctConstructor;
        for (Object o : ctClass.getConstructors()) {
            ctConstructor = ((CtConstructor) (o));
            constructorProcessor.process(ctConstructor);
        }
    }

    @LM
    public void handleAttachments(CtClass ctClass, PaprikaClass paprikaClass) {
        if ((ctClass.getSuperclass()) != null) {
            paprikaClass.setParentName(ctClass.getSuperclass().getQualifiedName());
        }
        for (CtTypeReference<?> ctTypeReference : ctClass.getSuperInterfaces()) {
            paprikaClass.getInterfacesNames().add(ctTypeReference.getQualifiedName());
        }
        String modifierText;
        PaprikaVariable paprikaVariable;
        PaprikaModifiers paprikaModifiers1;
        boolean isStatic;
        for (CtField<?> ctField : ((List<CtField>) (ctClass.getFields()))) {
            modifierText = ((ctField.getVisibility()) == null) ? "null" : ctField.getVisibility().toString();
            paprikaModifiers1 = DataConverter.convertTextToModifier(modifierText);
            if (paprikaModifiers1 == null) {
                paprikaModifiers1 = PaprikaModifiers.DEFAULT;
            }
            paprikaVariable = PaprikaVariable.createPaprikaVariable(ctField.getSimpleName(), ctField.getType().getQualifiedName(), paprikaModifiers1, paprikaClass);
            isStatic = false;
            for (ModifierKind modifierKind : ctField.getModifiers()) {
                if (modifierKind.toString().toLowerCase().equals("static")) {
                    isStatic = true;
                    break;
                }
            }
            paprikaVariable.setStatic(isStatic);
        }
    }

    @LM
    public void handleProperties(CtClass ctClass, PaprikaClass paprikaClass) {
        int doi = 0;
        boolean isApplication = false;
        boolean isContentProvider = false;
        boolean isAsyncTask = false;
        boolean isService = false;
        boolean isView = false;
        boolean isActivity = false;
        boolean isBroadcastReceiver = false;
        boolean isInterface = ctClass.isInterface();
        boolean isStatic = false;
        for (ModifierKind modifierKind : ctClass.getModifiers()) {
            if (modifierKind.toString().toLowerCase().equals("static")) {
                isStatic = true;
                break;
            }
        }
        CtType myClass = ctClass;
        boolean noSuperClass = false;
        if ((ctClass.getSuperclass()) != null) {
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
            myRealClass = null;
            if (!noSuperClass) {
                try {
                    myRealClass = ClassProcessor.classloader.loadClass(reference.getQualifiedName());
                    while ((myRealClass.getSuperclass()) != null) {
                        doi++;
                        if (myRealClass.getSimpleName().equals("Activity")) {
                            isActivity = true;
                        }else
                            if (myRealClass.getSimpleName().equals("ContentProvider")) {
                                isContentProvider = true;
                            }else
                                if (myRealClass.getSimpleName().equals("AsyncTask")) {
                                    isAsyncTask = true;
                                }else
                                    if (myRealClass.getSimpleName().equals("View")) {
                                        isView = true;
                                    }else
                                        if (myRealClass.getSimpleName().equals("BroadcastReceiver")) {
                                            isBroadcastReceiver = true;
                                        }else
                                            if (myRealClass.getSimpleName().equals("Service")) {
                                                isService = true;
                                            }else
                                                if (myRealClass.getSimpleName().equals("Application")) {
                                                    isApplication = true;
                                                }
                                            
                                        
                                    
                                
                            
                        
                        myRealClass = myRealClass.getSuperclass();
                    } 
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoClassDefFoundError e) {
                    e.printStackTrace();
                }
            }
        }
        paprikaClass.setInterface(isInterface);
        paprikaClass.setActivity(isActivity);
        paprikaClass.setStatic(isStatic);
        paprikaClass.setAsyncTask(isAsyncTask);
        paprikaClass.setContentProvider(isContentProvider);
        paprikaClass.setBroadcastReceiver(isBroadcastReceiver);
        paprikaClass.setService(isService);
        paprikaClass.setView(isView);
        paprikaClass.setApplication(isApplication);
        paprikaClass.setDepthOfInheritance(doi);
    }
}

