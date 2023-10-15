package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;

public class TypeGeneric extends TypeContent {
    @Expose
    public final Object value;

    public TypeGeneric(Object value) {
        this.value = value;
    }
}
