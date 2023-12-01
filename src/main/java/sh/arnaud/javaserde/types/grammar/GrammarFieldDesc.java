package sh.arnaud.javaserde.types.grammar;

import sh.arnaud.javaserde.types.FieldTypeCode;

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
