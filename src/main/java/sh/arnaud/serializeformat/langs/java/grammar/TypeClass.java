package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;

public class TypeClass extends TypeContent {
    public final TypeClassDesc classDesc;

    @Expose
    public final int handle;

    public TypeClass(int handle, TypeClassDesc classDesc) {
        this.classDesc = classDesc;
        this.handle = handle;
    }
}
