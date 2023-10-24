package sh.arnaud.serializeformat.grammar.classdesc;

import sh.arnaud.serializeformat.HandleManager;

import java.util.Optional;

public class TypeReferenceClassDesc extends ClassDesc {
    public TypeReferenceClassDesc(HandleManager manager, int handle) throws Exception {
        super(manager, handle);

        if (!(manager.fetchResource(handle) instanceof ClassDesc)) {
            throw new Exception("Handle does not reference a ClassDesc: " + handle);
        }
    }

    public ClassDesc superClassDesc() throws Exception {
        var resource = (ClassDesc) manager.fetchResource(handle);

        return resource.superClassDesc();
    }

    @Override
    public Optional<TypecodeClassDesc> asTypecodeClassDesc() {
        var resource = manager.fetchResource(handle);

        if (resource instanceof TypecodeClassDesc classDesc) {
            return Optional.of(classDesc);
        }

        return Optional.empty();
    }
}
