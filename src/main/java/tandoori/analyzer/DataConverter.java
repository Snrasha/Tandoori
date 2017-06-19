package tandoori.analyzer;

import tandoori.entities.PaprikaModifiers;

public class DataConverter {
    public static PaprikaModifiers convertTextToModifier(String text) {
        if (text.toLowerCase().equals("public")) {
            return PaprikaModifiers.PUBLIC;
        }else
            if (text.toLowerCase().equals("private")) {
                return PaprikaModifiers.PRIVATE;
            }else
                if (text.toLowerCase().equals("protected")) {
                    return PaprikaModifiers.PROTECTED;
                }else {
                    return null;
                }
            
        
    }
}

