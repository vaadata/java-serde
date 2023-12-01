package sh.arnaud.serializeformat.types;

import java.util.Arrays;
import java.util.Optional;

public enum FieldTypeCode {
    Byte("B", true),
    Char("C", true),
    Double("D", true),
    Float("F", true),
    Integer("I", true),
    Long("J", true),
    Short("S", true),
    Boolean("Z", true),
    Array("[", false),
    Object("L", false);

    public final byte typecode;
    public final boolean isPrimitive;
    public final String typecodeString;

    FieldTypeCode(String typecodeString, boolean isPrimitive) {
        this.typecodeString = typecodeString;
        this.typecode = typecodeString.getBytes()[0];
        this.isPrimitive = isPrimitive;
    }

    public static Optional<FieldTypeCode> fromByte(byte b) {
        return Arrays.stream(FieldTypeCode.values())
                .filter(fieldTypeCode -> fieldTypeCode.typecode == b)
                .findFirst();
    }
}
