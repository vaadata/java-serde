package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.langs.java.HandleManager;
import sh.arnaud.serializeformat.langs.java.grammar.classdesc.ClassDesc;

import java.util.List;

public class TypeArray extends Managed {
    @Expose
    @SerializedName("@class")
    public final ClassDesc classDesc;

    @Expose
    @SerializedName("@items")
    public List<Object> items = null;

    public TypeArray(HandleManager manager, int handle, ClassDesc classDesc) throws Exception {
        super(manager, handle);
        this.classDesc = classDesc;
    }
}
