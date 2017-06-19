package tandoori.neo4j;

import java.util.HashMap;
import codesmells.annotations.CC;
import java.util.Date;
import org.neo4j.graphdb.DynamicLabel;
import tandoori.entities.PaprikaVariable;
import tandoori.entities.Entity;
import org.neo4j.graphdb.GraphDatabaseService;
import tandoori.entities.PaprikaLibrary;
import tandoori.entities.PaprikaExternalArgument;
import codesmells.annotations.LM;
import org.neo4j.graphdb.Label;
import tandoori.entities.PaprikaApp;
import java.util.Map;
import tandoori.entities.PaprikaMethod;
import org.neo4j.graphdb.Transaction;
import tandoori.metrics.Metric;
import tandoori.entities.PaprikaExternalMethod;
import java.text.SimpleDateFormat;
import tandoori.entities.PaprikaArgument;
import org.neo4j.graphdb.Node;
import tandoori.entities.PaprikaClass;
import tandoori.entities.PaprikaExternalClass;

@CC
public class ModelToGraph {
    private GraphDatabaseService graphDatabaseService;

    private DatabaseManager databaseManager;

    private static final Label appLabel = DynamicLabel.label("App");

    private static final Label classLabel = DynamicLabel.label("Class");

    private static final Label externalClassLabel = DynamicLabel.label("ExternalClass");

    private static final Label methodLabel = DynamicLabel.label("Method");

    private static final Label externalMethodLabel = DynamicLabel.label("ExternalMethod");

    private static final Label variableLabel = DynamicLabel.label("Variable");

    private static final Label argumentLabel = DynamicLabel.label("Argument");

    private static final Label externalArgumentLabel = DynamicLabel.label("ExternalArgument");

    private static final Label libraryLabel = DynamicLabel.label("Library");

    private Map<Entity, Node> methodNodeMap;

    private Map<PaprikaClass, Node> classNodeMap;

    private Map<PaprikaVariable, Node> variableNodeMap;

    private String key;

    public ModelToGraph(String DatabasePath) {
        this.databaseManager = new DatabaseManager(DatabasePath);
        databaseManager.start();
        this.graphDatabaseService = databaseManager.getGraphDatabaseService();
        methodNodeMap = new HashMap<>();
        classNodeMap = new HashMap<>();
        variableNodeMap = new HashMap<>();
        IndexManager indexManager = new IndexManager(graphDatabaseService);
        indexManager.createIndex();
    }

    @LM
    public Node insertApp(PaprikaApp paprikaApp) {
        this.key = paprikaApp.getKey();
        Node appNode;
        try (Transaction tx = graphDatabaseService.beginTx()) {
            appNode = graphDatabaseService.createNode(ModelToGraph.appLabel);
            appNode.setProperty("app_key", key);
            appNode.setProperty("name", paprikaApp.getName());
            appNode.setProperty("version", paprikaApp.getVersionName());
            Date date = new Date();
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss.S");
            appNode.setProperty("date_analysis", simpleFormat.format(date));
            Node classNode;
            for (PaprikaClass paprikaClass : paprikaApp.getPaprikaClasses()) {
                classNode = insertClass(paprikaClass);
                appNode.createRelationshipTo(classNode, RelationTypes.APP_OWNS_CLASS);
            }
            for (PaprikaExternalClass paprikaExternalClass : paprikaApp.getPaprikaExternalClasses()) {
                insertExternalClass(paprikaExternalClass);
            }
            for (Metric metric : paprikaApp.getMetrics()) {
                insertMetric(metric, appNode);
            }
            for (PaprikaLibrary paprikaLibrary : paprikaApp.getPaprikaLibraries()) {
                appNode.createRelationshipTo(insertLibrary(paprikaLibrary), RelationTypes.APP_USES_LIBRARY);
            }
            tx.success();
        }
        try (Transaction tx = graphDatabaseService.beginTx()) {
            createHierarchy(paprikaApp);
            createCallGraph(paprikaApp);
            tx.success();
        }
        return appNode;
    }

    private void insertMetric(Metric metric, Node node) {
        node.setProperty(metric.getName(), metric.getValue());
    }

    @LM
    public Node insertClass(PaprikaClass paprikaClass) {
        Node classNode = graphDatabaseService.createNode(ModelToGraph.classLabel);
        classNodeMap.put(paprikaClass, classNode);
        classNode.setProperty("app_key", key);
        classNode.setProperty("name", paprikaClass.getName());
        classNode.setProperty("modifier", paprikaClass.getModifier().toString().toLowerCase());
        if ((paprikaClass.getParentName()) != null) {
            classNode.setProperty("parent_name", paprikaClass.getParentName());
        }
        for (PaprikaVariable paprikaVariable : paprikaClass.getPaprikaVariables()) {
            classNode.createRelationshipTo(insertVariable(paprikaVariable), RelationTypes.CLASS_OWNS_VARIABLE);
        }
        for (PaprikaMethod paprikaMethod : paprikaClass.getPaprikaMethods()) {
            classNode.createRelationshipTo(insertMethod(paprikaMethod), RelationTypes.CLASS_OWNS_METHOD);
        }
        for (Metric metric : paprikaClass.getMetrics()) {
            insertMetric(metric, classNode);
        }
        return classNode;
    }

    public Node insertLibrary(PaprikaLibrary paprikaLibrary) {
        Node libraryNode = graphDatabaseService.createNode(ModelToGraph.libraryLabel);
        libraryNode.setProperty("app_key", key);
        libraryNode.setProperty("name", paprikaLibrary.getName());
        return libraryNode;
    }

    public Node insertExternalClass(PaprikaExternalClass paprikaClass) {
        Node classNode = graphDatabaseService.createNode(ModelToGraph.externalClassLabel);
        classNode.setProperty("app_key", key);
        classNode.setProperty("name", paprikaClass.getName());
        if ((paprikaClass.getParentName()) != null) {
            classNode.setProperty("parent_name", paprikaClass.getParentName());
        }
        for (PaprikaExternalMethod paprikaExternalMethod : paprikaClass.getPaprikaExternalMethods()) {
            classNode.createRelationshipTo(insertExternalMethod(paprikaExternalMethod), RelationTypes.CLASS_OWNS_METHOD);
        }
        for (Metric metric : paprikaClass.getMetrics()) {
            insertMetric(metric, classNode);
        }
        return classNode;
    }

    public Node insertVariable(PaprikaVariable paprikaVariable) {
        Node variableNode = graphDatabaseService.createNode(ModelToGraph.variableLabel);
        variableNodeMap.put(paprikaVariable, variableNode);
        variableNode.setProperty("app_key", key);
        variableNode.setProperty("name", paprikaVariable.getName());
        variableNode.setProperty("modifier", paprikaVariable.getModifier().toString().toLowerCase());
        variableNode.setProperty("type", paprikaVariable.getType());
        for (Metric metric : paprikaVariable.getMetrics()) {
            insertMetric(metric, variableNode);
        }
        return variableNode;
    }

    @LM
    public Node insertMethod(PaprikaMethod paprikaMethod) {
        Node methodNode = graphDatabaseService.createNode(ModelToGraph.methodLabel);
        methodNodeMap.put(paprikaMethod, methodNode);
        methodNode.setProperty("app_key", key);
        methodNode.setProperty("name", paprikaMethod.getName());
        methodNode.setProperty("modifier", paprikaMethod.getModifier().toString().toLowerCase());
        methodNode.setProperty("full_name", paprikaMethod.toString());
        methodNode.setProperty("return_type", paprikaMethod.getReturnType());
        for (Metric metric : paprikaMethod.getMetrics()) {
            insertMetric(metric, methodNode);
        }
        Node variableNode;
        for (PaprikaVariable paprikaVariable : paprikaMethod.getUsedVariables()) {
            variableNode = variableNodeMap.get(paprikaVariable);
            if (variableNode != null) {
                methodNode.createRelationshipTo(variableNode, RelationTypes.USES);
            }else {
                System.out.println("problem");
            }
        }
        for (PaprikaArgument arg : paprikaMethod.getArguments()) {
            methodNode.createRelationshipTo(insertArgument(arg), RelationTypes.METHOD_OWNS_ARGUMENT);
        }
        return methodNode;
    }

    public Node insertExternalMethod(PaprikaExternalMethod paprikaMethod) {
        Node methodNode = graphDatabaseService.createNode(ModelToGraph.externalMethodLabel);
        methodNodeMap.put(paprikaMethod, methodNode);
        methodNode.setProperty("app_key", key);
        methodNode.setProperty("name", paprikaMethod.getName());
        methodNode.setProperty("full_name", paprikaMethod.toString());
        methodNode.setProperty("return_type", paprikaMethod.getReturnType());
        for (Metric metric : paprikaMethod.getMetrics()) {
            insertMetric(metric, methodNode);
        }
        for (PaprikaExternalArgument arg : paprikaMethod.getPaprikaExternalArguments()) {
            methodNode.createRelationshipTo(insertExternalArgument(arg), RelationTypes.METHOD_OWNS_ARGUMENT);
        }
        return methodNode;
    }

    public Node insertArgument(PaprikaArgument paprikaArgument) {
        Node argNode = graphDatabaseService.createNode(ModelToGraph.argumentLabel);
        argNode.setProperty("app_key", key);
        argNode.setProperty("name", paprikaArgument.getName());
        argNode.setProperty("position", paprikaArgument.getPosition());
        return argNode;
    }

    public Node insertExternalArgument(PaprikaExternalArgument paprikaExternalArgument) {
        Node argNode = graphDatabaseService.createNode(ModelToGraph.externalArgumentLabel);
        argNode.setProperty("app_key", key);
        argNode.setProperty("name", paprikaExternalArgument.getName());
        argNode.setProperty("position", paprikaExternalArgument.getPosition());
        for (Metric metric : paprikaExternalArgument.getMetrics()) {
            insertMetric(metric, argNode);
        }
        return argNode;
    }

    public void createHierarchy(PaprikaApp paprikaApp) {
        for (PaprikaClass paprikaClass : paprikaApp.getPaprikaClasses()) {
            PaprikaClass parent = paprikaClass.getParent();
            if (parent != null) {
                classNodeMap.get(paprikaClass).createRelationshipTo(classNodeMap.get(parent), RelationTypes.EXTENDS);
            }
            for (PaprikaClass pInterface : paprikaClass.getInterfaces()) {
                classNodeMap.get(paprikaClass).createRelationshipTo(classNodeMap.get(pInterface), RelationTypes.IMPLEMENTS);
            }
        }
    }

    public void createCallGraph(PaprikaApp paprikaApp) {
        for (PaprikaClass paprikaClass : paprikaApp.getPaprikaClasses()) {
            for (PaprikaMethod paprikaMethod : paprikaClass.getPaprikaMethods()) {
                for (Entity calledMethod : paprikaMethod.getCalledMethods()) {
                    methodNodeMap.get(paprikaMethod).createRelationshipTo(methodNodeMap.get(calledMethod), RelationTypes.CALLS);
                }
            }
        }
    }
}

