package tandoori.entities;


public class PaprikaVariable extends Entity {
    private PaprikaClass paprikaClass;

    private String type;

    private PaprikaModifiers modifier;

    private boolean isStatic;

    public String getType() {
        return type;
    }

    public PaprikaModifiers getModifier() {
        return modifier;
    }

    private PaprikaVariable(String name, String type, PaprikaModifiers modifier, PaprikaClass paprikaClass) {
        this.type = type;
        this.name = name;
        this.modifier = modifier;
        this.paprikaClass = paprikaClass;
        this.isStatic = false;
    }

    public static PaprikaVariable createPaprikaVariable(String name, String type, PaprikaModifiers modifier, PaprikaClass paprikaClass) {
        PaprikaVariable paprikaVariable = new PaprikaVariable(name, type, modifier, paprikaClass);
        paprikaClass.addPaprikaVariable(paprikaVariable);
        return paprikaVariable;
    }

    public boolean isPublic() {
        return (modifier) == (PaprikaModifiers.PUBLIC);
    }

    public boolean isPrivate() {
        return (modifier) == (PaprikaModifiers.PRIVATE);
    }

    public boolean isProtected() {
        return (modifier) == (PaprikaModifiers.PROTECTED);
    }

    public PaprikaClass getPaprikaClass() {
        return paprikaClass;
    }

    public void setPaprikaClass(PaprikaClass paprikaClass) {
        this.paprikaClass = paprikaClass;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }
}

