package sh.arnaud.serializeformat;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpResponseEditor;
import burp.api.montoya.utilities.CompressionType;
import burp.api.montoya.utilities.CompressionUtils;
import sh.arnaud.serializeformat.de.Deserialize;
import sh.arnaud.serializeformat.de.FromStream;
import sh.arnaud.serializeformat.ser.Serialize;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ResponseEditor implements ExtensionProvidedHttpResponseEditor {
    private final RawEditor editor;
    private final CompressionUtils compressionUtils;
    private HttpRequestResponse requestResponse;

    public ResponseEditor(MontoyaApi api, EditorCreationContext creationContext) {
        editor = api.userInterface().createRawEditor(EditorOptions.READ_ONLY);
        compressionUtils = api.utilities().compressionUtils();
    }

    @Override
    public HttpResponse getResponse() {
        return requestResponse.response();
    }

    @Override
    public void setRequestResponse(HttpRequestResponse requestResponse) {
        this.requestResponse = requestResponse;

        try {
            var body1 = requestResponse.response().body();
            var body2 = compressionUtils.decompress(body1, CompressionType.GZIP);
            var stream = Deserialize.deserialize(body2.getBytes());
            editor.setContents(ByteArray.byteArray(stream));

            // TODO: Delete this is used to see if everything works correctly
            SerializeFormat.log("response");
            SerializeFormat.log(stream);
            if (!Arrays.equals(Serialize.serialize(stream).array(), body2.getBytes())) {
                throw new Exception("Differential with JSON and value");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEnabledFor(HttpRequestResponse requestResponse) {
        return requestResponse.response().hasHeader("Content-Type", "application/x-java-serialized-object");
    }

    @Override
    public String caption() {
        return "Java serializable";
    }

    @Override
    public Component uiComponent() {
        return editor.uiComponent();
    }

    @Override
    public Selection selectedData() {
        return editor.selection().orElse(null);
    }

    @Override
    public boolean isModified() {
        return editor.isModified();
    }
}
