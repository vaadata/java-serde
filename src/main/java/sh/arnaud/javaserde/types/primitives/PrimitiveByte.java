package sh.arnaud.javaserde.types.primitives;

import com.google.gson.JsonPrimitive;

public class PrimitiveByte extends Primitive {
    public final byte value;

    public PrimitiveByte(byte value) {
        this.value = value;
    }

    @Override
    public byte asByte() {
        return value;
    }

    @Override
    public JsonPrimitive asJson() {
        return new JsonPrimitive(value);
    }
}
