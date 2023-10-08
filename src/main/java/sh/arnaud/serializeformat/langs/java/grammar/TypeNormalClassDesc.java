package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.langs.java.HandleManager;

public class TypeNormalClassDesc extends TypeClassDesc {
    @Expose
    public final int handle;
    @Expose
    public final String className;
    @Expose
    public final long serialVersionUID;

    // The handle need to be registered before parsing the info.
    @SerializedName("info")
    @Expose
    public TypeClassDescInfo classDescInfo;

    public TypeNormalClassDesc(int handle, String className, long serialVersionUID) {
        this.handle = handle;
        this.className = className;
        this.serialVersionUID = serialVersionUID;
    }

    public TypeClassDesc superClassDesc(HandleManager manager) {
        if (classDescInfo.superClassDesc != null) {
            return (TypeClassDesc) manager.fetchResource(classDescInfo.superClassDesc);
        } else {
            return null;
        }
    }

    @Override
    public int getHandle() {
        return handle;
    }
}
