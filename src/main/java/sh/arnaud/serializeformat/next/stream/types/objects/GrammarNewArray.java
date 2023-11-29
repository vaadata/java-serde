package sh.arnaud.serializeformat.next.stream.types.objects;

import sh.arnaud.serializeformat.next.stream.types.objects.GrammarClassdata;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarNewClassDesc;
import sh.arnaud.serializeformat.next.stream.types.objects.GrammarObject;

import java.util.List;

public class GrammarNewArray extends GrammarObject {
    public final GrammarNewClassDesc classDesc;
    public List<GrammarObject> values = null;

    public GrammarNewArray(GrammarNewClassDesc classDesc) {
        this.classDesc = classDesc;
    }
}
