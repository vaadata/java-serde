package sh.arnaud.serializeformat.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.HandleManager;
import sh.arnaud.serializeformat.grammar.classdesc.ClassDesc;

public class TypeClass extends Managed {
    @Expose
    @SerializedName("@class")
    public final ClassDesc classDesc;

    public TypeClass(HandleManager manager, ClassDesc classDesc) {
        super(manager);
        this.classDesc = classDesc;
    }

    public TypeClass(HandleManager manager, int handle, ClassDesc classDesc) {
        super(manager, handle);
        this.classDesc = classDesc;
    }
}
