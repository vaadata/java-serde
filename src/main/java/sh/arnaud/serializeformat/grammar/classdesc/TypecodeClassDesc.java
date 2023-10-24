package sh.arnaud.serializeformat.grammar.classdesc;

import sh.arnaud.serializeformat.HandleManager;

import java.util.Optional;

public class TypecodeClassDesc extends ClassDesc {
    public final String className;
    public final long serialVersionUID;

    // The handle need to be registered before parsing the info.
    public ClassDescInfo classDescInfo;

    public TypecodeClassDesc(HandleManager manager, String className, long serialVersionUID) {
        super(manager);
        this.className = className;
        this.serialVersionUID = serialVersionUID;
    }

    public TypecodeClassDesc(HandleManager manager, int handle, String className, long serialVersionUID) {
        super(manager, handle);
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
