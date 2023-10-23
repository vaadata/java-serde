package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.langs.java.HandleManager;
import sh.arnaud.serializeformat.langs.java.grammar.classdesc.ClassDesc;

public class TypeClass extends Managed {
    @Expose
    @SerializedName("@class")
    public final ClassDesc classDesc;

    public TypeClass(HandleManager manager, ClassDesc classDesc) {
        super(manager);
        this.classDesc = classDesc;
    }
}
