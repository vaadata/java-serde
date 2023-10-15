package sh.arnaud.serializeformat.langs.java.grammar.classdesc;

import sh.arnaud.serializeformat.langs.java.HandleManager;

public class TypeReferenceClassDesc extends ClassDesc {
    public TypeReferenceClassDesc(HandleManager manager, int handle) throws Exception {
        super(manager, handle);

        if (!(manager.fetchResource(handle) instanceof ClassDesc)) {
            throw new Exception("Handle does not reference a ClassDesc");
        }
    }

    public ClassDesc superClassDesc() throws Exception {
        return getAsNormalClassDesc(manager).superClassDesc();
    }

    @Override
    public TypecodeClassDesc getAsNormalClassDesc(HandleManager manager) throws Exception {
        var resource = manager.fetchResource(handle);

        if (resource instanceof TypecodeClassDesc classDesc) {
            return classDesc;
        }

        // TODO: Maybe move in constructor ?
        throw new Exception("Not a reference to a TypeNormalClassDesc object");
    }
}
