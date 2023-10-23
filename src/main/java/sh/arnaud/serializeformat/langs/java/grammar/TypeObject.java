package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.langs.java.HandleManager;
import sh.arnaud.serializeformat.langs.java.grammar.classdesc.ClassDesc;

import java.util.List;
import java.util.Map;

public class TypeObject extends Managed {
    @Expose
    @SerializedName("@class")
    public final ClassDesc classDesc;

    @SerializedName("@data")
    @Expose
    public List<ClassData> classdata = null;

    public TypeObject(HandleManager manager, ClassDesc classDesc) {
        super(manager);
        this.classDesc = classDesc;
    }
}
