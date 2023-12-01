package sh.arnaud.serializeformat.types.grammar;

import java.util.List;

public class GrammarNewArray extends GrammarObject {
    public final GrammarNewClassDesc classDesc;
    public List<GrammarObject> values = null;

    public GrammarNewArray(GrammarNewClassDesc classDesc) {
        this.classDesc = classDesc;
    }
}
