package sh.arnaud.serializeformat.langs.java.grammar.classdesc;

import sh.arnaud.serializeformat.langs.java.HandleManager;
import sh.arnaud.serializeformat.langs.java.grammar.Managed;

public abstract class ClassDesc extends Managed {
    public ClassDesc(HandleManager manager) {
        super(manager);
    }

    public ClassDesc(HandleManager manager, int handle) {
        super(manager, handle);
    }

    public abstract ClassDesc superClassDesc() throws Exception;

    public TypecodeClassDesc getAsNormalClassDesc(HandleManager manager) throws Exception {
        throw new UnsupportedOperationException("The current classdesc can't be represented as a normal classdesc");
    }
}
