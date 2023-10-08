package sh.arnaud.serializeformat.langs.java.grammar;

import java.util.EnumMap;

public enum FieldTypeCode {
    Byte((byte)'B'),
    Char((byte)'C'),
    Double((byte)'D'),
    Float((byte)'F'),
    Integer((byte)'I'),
    Long((byte)'J'),
    Short((byte)'S'),
    Boolean((byte)'Z'),
    Array((byte)'['),
    Object((byte)'L');

    private final byte typecode;

    FieldTypeCode(byte typecode) {
        this.typecode = typecode;
    }

    public static FieldTypeCode fromByte(byte b) throws Exception {
        for (FieldTypeCode variant : FieldTypeCode.values()) {
            if (variant.typecode == b) {
                System.out.println(b);
                return variant;
            }
        }

        throw new Exception("No FieldTypeCode found for the given typecode");
    }
}
