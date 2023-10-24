package sh.arnaud.serializeformat.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.HandleManager;
import sh.arnaud.serializeformat.grammar.classdesc.ClassDesc;

import java.util.List;

public class TypeObject extends Managed {
    @Expose
    @SerializedName("@class")
    public final ClassDesc classDesc;

    @SerializedName("@data")
    @Expose
    public List<ClassData> classdata = null;

    // TODO: It's possible to get the manager from the classDesc ?
    public TypeObject(HandleManager manager, ClassDesc classDesc) {
        super(manager);
        this.classDesc = classDesc;
    }

    public TypeObject(HandleManager manager, int newHandle, ClassDesc classDesc) {
        super(manager, newHandle);
        this.classDesc = classDesc;
    }
}
