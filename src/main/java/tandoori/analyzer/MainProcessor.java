package tandoori.analyzer;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import codesmells.annotations.LM;
import spoon.Launcher;
import tandoori.entities.PaprikaApp;
import tandoori.entities.PaprikaClass;
import tandoori.entities.PaprikaMethod;
import java.net.URL;

public class MainProcessor {
    public static PaprikaApp currentApp;

    public static PaprikaClass currentClass;

    public static PaprikaMethod currentMethod;

    public static ArrayList<URL> paths;

    protected String appPath;

    protected String jarsPath;

    protected String sdkPath;

    public MainProcessor(String appName, String appVersion, String appKey, String appPath, String sdkPath, String jarsPath) {
        this.currentApp = PaprikaApp.createPaprikaApp(appName, appVersion, appKey);
        MainProcessor.currentClass = null;
        MainProcessor.currentMethod = null;
        this.appPath = appPath;
        this.jarsPath = jarsPath;
        this.sdkPath = sdkPath;
    }

    @LM
    public void process() {
        Launcher launcher = new Launcher();
        launcher.addInputResource(appPath);
        launcher.getEnvironment().setNoClasspath(true);
        File folder = new File(jarsPath);
        try {
            MainProcessor.paths = this.listFilesForFolder(folder);
            MainProcessor.paths.add(new File(sdkPath).toURI().toURL());
            String[] cl = new String[MainProcessor.paths.size()];
            for (int i = 0; i < (MainProcessor.paths.size()); i++) {
                URL url = MainProcessor.paths.get(i);
                cl[i] = url.getPath();
            }
            launcher.getEnvironment().setSourceClasspath(cl);
            launcher.buildModel();
            ClassProcessor classProcessor = new ClassProcessor();
            InterfaceProcessor interfaceProcessor = new InterfaceProcessor();
            launcher.addProcessor(classProcessor);
            launcher.addProcessor(interfaceProcessor);
            launcher.process();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public ArrayList<URL> listFilesForFolder(final File folder) throws IOException {
        ArrayList<URL> jars = new ArrayList<>();
        if ((folder.listFiles()) == null) {
            return jars;
        }
        for (final File fileEntry : folder.listFiles()) {
            jars.add(fileEntry.toURI().toURL());
        }
        return jars;
    }
}

