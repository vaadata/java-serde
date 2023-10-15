package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.langs.java.HandleManager;
import sh.arnaud.serializeformat.langs.java.grammar.classdesc.ClassDesc;

import java.util.List;

public class TypeArray extends TypeContent {
    public final ClassDesc classDesc;

    @SerializedName("class")
    @Expose
    public final int classHandle;

    public final int handle;

    @Expose
    public List<Object> items = null;

    public TypeArray(int handle, ClassDesc classDesc, HandleManager manager) throws Exception {
        this.classDesc = classDesc;
        this.handle = handle;
        this.classHandle = classDesc.handle;
    }
}
