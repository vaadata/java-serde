package sh.arnaud.serializeformat.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlockData extends TypeContent {
    @SerializedName("@data")
    @Expose
    public byte[] buffer;

    public BlockData(byte[] buffer) {
        this.buffer = buffer;
    }
}
