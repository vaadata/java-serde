package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.langs.java.HandleManager;
import sh.arnaud.serializeformat.langs.java.grammar.classdesc.ClassDesc;

import java.util.List;
import java.util.Map;

public class TypeObject extends Managed {

    @SerializedName("class")
    @Expose
    public final int classHandle;
    @SerializedName("data")
    @Expose
    public List<Map<String, Object>> classdata = null;

    public Map<String, Object> classdataflatten = null;

    // classdata
    public final ClassDesc classDesc;

    public TypeObject(HandleManager manager, ClassDesc classDesc) throws Exception {
        super(manager);
        this.classDesc = classDesc;
        this.classHandle = classDesc.handle;
    }
}
