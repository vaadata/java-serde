package sh.arnaud.serializeformat.types.primitives;

import com.google.gson.JsonPrimitive;

public class PrimitiveLong extends Primitive {
    public final long value;

    public PrimitiveLong(long value) {
        this.value = value;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public JsonPrimitive asJson() {
        return new JsonPrimitive(value);
    }
}
