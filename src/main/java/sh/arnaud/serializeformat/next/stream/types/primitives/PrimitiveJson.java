package sh.arnaud.serializeformat.next.stream.types.primitives;

import com.google.gson.JsonPrimitive;

/**
 * This class is used when deserializing a JSON document when we don't know yet the kind of value we have.
 * The real information comes from a class description we don't have access to yet.
 */
public class PrimitiveJson extends Primitive {
    public final JsonPrimitive primitive;

    public PrimitiveJson(JsonPrimitive primitive) {
        this.primitive = primitive;
    }

    public byte asByte() {
        return primitive.getAsByte();
    }

    public char asChar() {
        return primitive.getAsString().charAt(0);
    }

    public double asDouble() {
        return primitive.getAsDouble();
    }

    public float asFloat() {
        return primitive.getAsFloat();
    }

    public int asInt() {
        return primitive.getAsInt();
    }

    public long asLong() {
        return primitive.getAsLong();
    }

    public short asShort() {
        return primitive.getAsShort();
    }

    public boolean asBoolean() {
        return primitive.getAsBoolean();
    }

    @Override
    public JsonPrimitive asJson() {
        return primitive;
    }
}
