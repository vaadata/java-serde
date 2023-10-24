package sh.arnaud.serializeformat.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.HandleManager;
import sh.arnaud.serializeformat.grammar.classdesc.ClassDesc;

public class TypeEnum extends Managed {
    @Expose
    @SerializedName("@class")
    public final ClassDesc classDesc;

    @Expose
    @SerializedName("@variant")
    public final String enumConstantName;

    public TypeEnum(HandleManager manager, int handle, ClassDesc classDesc, String enumConstantName) {
        super(manager, handle);

        this.classDesc = classDesc;
        this.enumConstantName = enumConstantName;
    }
}
