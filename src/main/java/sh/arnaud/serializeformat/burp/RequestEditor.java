package sh.arnaud.serializeformat.burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;
import burp.api.montoya.utilities.CompressionType;
import burp.api.montoya.utilities.CompressionUtils;
import sh.arnaud.serializeformat.de.Deserialize;
import sh.arnaud.serializeformat.ser.Serialize;

import java.awt.*;
import java.util.Arrays;

public class RequestEditor implements ExtensionProvidedHttpRequestEditor {
    private final RawEditor editor;
    private final CompressionUtils compressionUtils;
    private HttpRequestResponse requestResponse;

    public RequestEditor(MontoyaApi api, EditorCreationContext creationContext) {
        editor = api.userInterface().createRawEditor(EditorOptions.READ_ONLY);
        compressionUtils = api.utilities().compressionUtils();
    }

    @Override
    public HttpRequest getRequest() {
        return requestResponse.request();
    }

    @Override
    public void setRequestResponse(HttpRequestResponse requestResponse) {
        this.requestResponse = requestResponse;

        try {
            var body1 = requestResponse.request().body();
            var body2 = compressionUtils.decompress(body1, CompressionType.GZIP);
            var stream = Deserialize.deserialize(body2.getBytes());

            // TODO: Delete this is used to see if everything works correctly
            SerializeFormat.log("request");
            SerializeFormat.log(stream);
            if (!Arrays.equals(Serialize.serialize(stream).array(), body2.getBytes())) {
                throw new Exception("Differential with JSON and value");
            }

            editor.setContents(ByteArray.byteArray(stream));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEnabledFor(HttpRequestResponse requestResponse) {
        SerializeFormat.log(requestResponse.request().toString());
        SerializeFormat.log(requestResponse.request().headers().toString());
        return requestResponse.request().hasHeader("Content-Type", "application/x-java-serialized-object");
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