package sh.arnaud.serializeformat.langs.java.grammar.classdesc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.langs.java.HandleManager;

import java.util.Optional;

public class TypecodeClassDesc extends ClassDesc {
    @Expose
    public final String className;
    @Expose
    public final long serialVersionUID;

    // The handle need to be registered before parsing the info.
    @SerializedName("info")
    @Expose
    public ClassDescInfo classDescInfo;

    public TypecodeClassDesc(HandleManager manager, String className, long serialVersionUID) {
        super(manager);
        this.className = className;
        this.serialVersionUID = serialVersionUID;
    }

    public ClassDesc superClassDesc() {
        return classDescInfo.superClassDesc;
    }

    @Override
    public Optional<TypecodeClassDesc> asTypecodeClassDesc() {
        return Optional.of(this);
    }
}
