package sh.arnaud.serializeformat.next.stream.types.grammar;

import sh.arnaud.serializeformat.next.stream.types.FieldTypeCode;

public class GrammarNewClassDesc extends GrammarObject {
    public final String className;
    public final long serial;
    public GrammarClassDescInfo classDescInfo;

    public GrammarNewClassDesc(String className, long serialVersionUID) {
        this.className = className;
        this.serial = serialVersionUID;
    }

    public FieldTypeCode asTypeCode() throws Exception {
        return FieldTypeCode.fromByte(className.getBytes()[0])
                .orElseThrow(() -> new Exception("Can't find typecode from classname"));
    }
}