package tandoori.neo4jBolt;

import java.util.HashMap;
import java.util.Iterator;
import codesmells.annotations.LM;
import codesmells.annotations.MIM;

public class LowNode {
    private String label;

    private HashMap<String, String> map;

    private long id;

    public LowNode(String label) {
        this.label = label;
        this.map = new HashMap<>();
        this.id = -1;
    }

    public String getLabel() {
        return this.label;
    }

    public long getID() {
        return this.id;
    }

    public void addParameter(String attributeName, String value) {
        this.map.put(attributeName, overString(value));
    }

    public void addParameter(String attributeName, Object value) {
        this.map.put(attributeName, value.toString());
    }

    public void addParameter(String attributeName, int value) {
        this.map.put(attributeName, Integer.toString(value));
    }

    public void addParameter(String attributeName, long value) {
        this.map.put(attributeName, Long.toString(value));
    }

    public void addParameter(String attributeName, double value) {
        this.map.put(attributeName, Double.toString(value));
    }

    public String getParameter(String attributeName) {
        return this.map.get(attributeName);
    }

    @MIM
    private String overString(String string) {
        return ("\"" + string) + "\"";
    }

    public String idfocus(String labelname) {
        if ((this.id) == (-1))
            return "";
        
        return (("WHERE ID(" + labelname) + ") = ") + (this.id);
    }

    public void setId(long id) {
        this.id = id;
    }

    @LM
    public StringBuilder parametertoData() {
        if ((this.map.size()) == 0)
            return new StringBuilder("");
        
        String begin;
        String end;
        begin = " {";
        end = "} ";
        String key;
        String value;
        Boolean onetime = false;
        StringBuilder data = new StringBuilder(begin);
        Iterator<String> iter = this.map.keySet().iterator();
        while (iter.hasNext()) {
            key = iter.next();
            value = this.map.get(key);
            if (onetime)
                data.append(" , ");
            else
                onetime = true;
            
            data.append(((key + ":") + value));
        } 
        data.append(end);
        return data;
    }
}

