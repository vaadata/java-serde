package sh.arnaud.serializeformat.next.stream.types.objects;

import sh.arnaud.serializeformat.next.stream.types.GrammarContent;

import java.util.List;

import static java.io.ObjectStreamConstants.SC_SERIALIZABLE;
import static java.io.ObjectStreamConstants.SC_WRITE_METHOD;

public class GrammarClassDescInfo {

    public final byte classDescFlags;

    public final List<GrammarFieldDesc> fields;
    public final List<GrammarContent> annotations;

    public final GrammarNewClassDesc superClassDesc;

    public GrammarClassDescInfo(byte classDescFlags, List<GrammarFieldDesc> fields, List<GrammarContent> annotations, GrammarNewClassDesc superClassDesc) {
        this.classDescFlags = classDescFlags;
        this.fields = fields;
        this.annotations = annotations;
        this.superClassDesc = superClassDesc;
    }

    public boolean isNowrclass() {
        return (SC_SERIALIZABLE & classDescFlags) == SC_SERIALIZABLE && (SC_WRITE_METHOD & classDescFlags) == 0;
    }

    public boolean isWrclass() {
        return (SC_SERIALIZABLE & classDescFlags) == SC_SERIALIZABLE && (SC_WRITE_METHOD & classDescFlags) == SC_WRITE_METHOD;
    }
}
