package sh.arnaud.serializeformat.next.stream.types.objects;

import java.util.List;

public class GrammarNewObject extends GrammarObject {
    public final GrammarNewClassDesc classDesc;
    public List<GrammarClassdata> classdata = null;

    public GrammarNewObject(GrammarNewClassDesc classDesc) {
        this.classDesc = classDesc;
    }
}
