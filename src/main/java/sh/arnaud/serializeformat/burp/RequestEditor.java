package sh.arnaud.serializeformat.burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.EditorMode;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;
import burp.api.montoya.utilities.Base64EncodingOptions;
import burp.api.montoya.utilities.CompressionType;
import burp.api.montoya.utilities.CompressionUtils;
import sh.arnaud.serializeformat.de.Deserialize;
import sh.arnaud.serializeformat.ser.Serialize;

import java.awt.*;
import java.util.Arrays;

import static burp.api.montoya.core.ByteArray.byteArray;

public class RequestEditor implements ExtensionProvidedHttpRequestEditor {
    private final RawEditor editor;
    private final CompressionUtils compressionUtils;
    private HttpRequestResponse requestResponse;

    public RequestEditor(MontoyaApi api, EditorCreationContext creationContext) {
        if (creationContext.editorMode() == EditorMode.READ_ONLY) {
            editor = api.userInterface().createRawEditor(EditorOptions.READ_ONLY);
        } else {
            editor = api.userInterface().createRawEditor();
        }
        compressionUtils = api.utilities().compressionUtils();
    }

    @Override
    public HttpRequest getRequest() {
        if (editor.isModified()) {
            try {
                var buffer = byteArray(Serialize.serialize(editor.getContents().toString()).array());
                var body = compressionUtils.compress(buffer, CompressionType.GZIP);

                return requestResponse.request().withBody(body);
            } catch (Exception e) {
                return requestResponse.request();
            }
        }

        return requestResponse.request();
    }

    @Override
    public void setRequestResponse(HttpRequestResponse requestResponse) {
        this.requestResponse = requestResponse;

        try {
            var body1 = requestResponse.request().body();
            var body2 = compressionUtils.decompress(body1, CompressionType.GZIP);
            var stream = Deserialize.deserialize(body2.getBytes());

            editor.setContents(byteArray(stream));

            // TODO: Delete this is used to see if everything works correctly
            // TODO: Maybe warning about inconstitency
            if (!Arrays.equals(Serialize.serialize(stream).array(), body2.getBytes())) {
                SerializeFormat.err("Inconsitency");
                SerializeFormat.log("request");
                SerializeFormat.log(stream);
            }

        } catch (Exception e) {
            SerializeFormat.err(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEnabledFor(HttpRequestResponse requestResponse) {
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
