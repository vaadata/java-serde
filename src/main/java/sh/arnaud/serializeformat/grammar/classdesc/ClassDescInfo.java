package sh.arnaud.serializeformat.grammar.classdesc;

import sh.arnaud.serializeformat.grammar.TypeContent;
import sh.arnaud.serializeformat.grammar.TypeFieldDesc;

import java.util.List;

import static java.io.ObjectStreamConstants.*;

public class ClassDescInfo {
    public final byte classDescFlags;

    public final List<TypeFieldDesc> fields;
    public final List<TypeContent> annotations;

    public final ClassDesc superClassDesc;

    public ClassDescInfo(byte classDescFlags, List<TypeFieldDesc> fields, List<TypeContent> annotations, ClassDesc superClassDesc) {
        this.classDescFlags = classDescFlags;
        this.fields = fields;
        this.annotations = annotations;
        this.superClassDesc = superClassDesc;
    }

    public boolean isNowrclass() {
        return ((SC_SERIALIZABLE & classDescFlags) != 0) && ((SC_WRITE_METHOD & classDescFlags) == 0);
    }

    public boolean isWrclass() {
        return ((SC_SERIALIZABLE & classDescFlags) != 0) && ((SC_WRITE_METHOD & classDescFlags) != 0);
    }
}
