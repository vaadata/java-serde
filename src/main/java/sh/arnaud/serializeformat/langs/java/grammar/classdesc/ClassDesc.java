package sh.arnaud.serializeformat.langs.java.grammar.classdesc;

import sh.arnaud.serializeformat.langs.java.HandleManager;
import sh.arnaud.serializeformat.langs.java.grammar.Managed;

import java.util.Optional;

public abstract class ClassDesc extends Managed {
    public ClassDesc(HandleManager manager) {
        super(manager);
    }

    public ClassDesc(HandleManager manager, int handle) {
        super(manager, handle);
    }

    public abstract ClassDesc superClassDesc() throws Exception;

    public Optional<TypecodeClassDesc> asTypecodeClassDesc() {
        return Optional.empty();
    }
}
