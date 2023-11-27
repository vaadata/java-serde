package sh.arnaud.serializeformat.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.HandleManager;
import sh.arnaud.serializeformat.grammar.classdesc.ClassDesc;

import java.util.List;

public class TypeArray extends Managed {
    @Expose
    @SerializedName("@class")
    public final ClassDesc classDesc;

    @Expose
    @SerializedName("@items")
    public List<TypeContent> items = null;

    public TypeArray(HandleManager manager, int handle, ClassDesc classDesc) {
        super(manager, handle);
        this.classDesc = classDesc;
    }
}
