package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;
import sh.arnaud.serializeformat.langs.java.HandleManager;
import sh.arnaud.serializeformat.langs.java.grammar.classdesc.ClassDesc;

public class TypeEnum extends Managed {
    @Expose
    public final ClassDesc classDesc;

    @Expose
    public final String enumConstantName;

    public TypeEnum(HandleManager manager, int handle, ClassDesc classDesc, String enumConstantName) {
        super(manager, handle);

        this.classDesc = classDesc;
        this.enumConstantName = enumConstantName;
    }
}
