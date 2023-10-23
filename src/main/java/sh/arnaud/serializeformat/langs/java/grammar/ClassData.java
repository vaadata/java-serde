package sh.arnaud.serializeformat.langs.java.grammar;

import java.util.List;
import java.util.Map;

public class ClassData {
    public final Map<String, Object> values;
    public final List<TypeContent> annotations;

    public ClassData(Map<String, Object> values, List<TypeContent> annotations) {
        this.values = values;
        this.annotations = annotations;
    }
}
