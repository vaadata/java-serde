package sh.arnaud.javaserde.types.primitives;

import com.google.gson.JsonPrimitive;

public class PrimitiveShort extends Primitive {
    public final short value;

    public PrimitiveShort(short value) {
        this.value = value;
    }

    @Override
    public short asShort() {
        return value;
    }

    @Override
    public JsonPrimitive asJson() {
        return new JsonPrimitive(value);
    }
}
