package sh.arnaud.serializeformat.langs.java.grammar;

import com.google.gson.annotations.Expose;
import sh.arnaud.serializeformat.langs.java.HandleManager;
import sh.arnaud.serializeformat.langs.java.grammar.classdesc.ClassDesc;

public class TypeClass extends Managed {
    public final ClassDesc classDesc;

    public TypeClass(HandleManager manager, ClassDesc classDesc) {
        super(manager);
        this.classDesc = classDesc;
    }
}
