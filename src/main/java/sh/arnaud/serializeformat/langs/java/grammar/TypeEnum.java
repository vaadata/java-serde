package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;

public class TypeEnum extends TypeContent {
    public final TypeClassDesc classDesc;

    @Expose
    public final int handle;

    @Expose
    public final String enumConstantName;

    public TypeEnum(int handle, TypeClassDesc classDesc, String enumConstantName) {
        this.classDesc = classDesc;
        this.handle = handle;
        this.enumConstantName = enumConstantName;
    }
}
