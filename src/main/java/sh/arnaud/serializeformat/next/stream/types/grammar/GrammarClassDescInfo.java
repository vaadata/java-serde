package sh.arnaud.serializeformat.next.stream.types.grammar;

import java.util.List;

import static java.io.ObjectStreamConstants.SC_SERIALIZABLE;
import static java.io.ObjectStreamConstants.SC_WRITE_METHOD;

public record GrammarClassDescInfo(
        byte classDescFlags,
        List<GrammarFieldDesc> fields,
        List<GrammarContent> annotations,
        GrammarNewClassDesc superClassDesc
) {
    public boolean isNowrclass() {
        return (SC_SERIALIZABLE & classDescFlags) == SC_SERIALIZABLE && (SC_WRITE_METHOD & classDescFlags) == 0;
    }

    public boolean isWrclass() {
        return (SC_SERIALIZABLE & classDescFlags) == SC_SERIALIZABLE && (SC_WRITE_METHOD & classDescFlags) == SC_WRITE_METHOD;
    }
}
