package sh.arnaud.serializeformat.grammar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sh.arnaud.serializeformat.HandleManager;

public class Managed extends TypeContent {
    protected final HandleManager manager;

    @Expose
    @SerializedName("@handle")
    public final int handle;

    public Managed(HandleManager manager) {
        this.manager = manager;

        handle = manager.newHandle();
        manager.registerResource(handle, this);
    }

    public Managed(HandleManager manager, int handle) {
        this.manager = manager;
        this.handle = handle;
    }
}
