package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class TypeObject extends TypeContent {
    private final int handle;

    @SerializedName("class")
    @Expose
    public final int classHandle;
    @SerializedName("data")
    @Expose
    public List<Map<String, Object>> classdata;

    // classdata


    public TypeObject(int handle, TypeClassDesc classDesc) {
        this.handle = handle;
        this.classHandle = classDesc.getHandle();
    }
}
