
package sh.arnaud.serializeformat.next.stream.types.objects;

import java.util.List;

public class GrammarNewEnum extends GrammarObject {
    public final GrammarNewClassDesc classDesc;
    public GrammarNewString enumConstantName;

    public GrammarNewEnum(GrammarNewClassDesc classDesc) {
        this.classDesc = classDesc;
    }
}
