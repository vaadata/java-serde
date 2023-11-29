package sh.arnaud.serializeformat.next.stream.types.objects;

import sh.arnaud.serializeformat.next.stream.types.FieldTypeCode;

public class GrammarFieldDesc {
    public final FieldTypeCode typeCode;
    public final String fieldName;
    public final GrammarNewString className1;

    public GrammarFieldDesc(FieldTypeCode typeCode, String fieldName, GrammarNewString className1) {
        this.typeCode = typeCode;
        this.fieldName = fieldName;
        this.className1 = className1;
    }
}
