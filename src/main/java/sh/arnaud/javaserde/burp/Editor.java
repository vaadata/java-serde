package sh.arnaud.javaserde.burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpMessage;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.EditorMode;
import burp.api.montoya.utilities.CompressionType;
import burp.api.montoya.utilities.CompressionUtils;
import sh.arnaud.javaserde.de.Deserialize;
import sh.arnaud.javaserde.ser.Serialize;

import java.awt.*;
import java.util.Arrays;

import static burp.api.montoya.core.ByteArray.byteArray;

public abstract class Editor {
    private final Logging logging;
    private final CompressionUtils compressionUtils;
    private final RawEditor editor;
    protected HttpRequestResponse requestResponse;

    Editor(MontoyaApi api, EditorCreationContext context) {
        logging = api.logging();
        compressionUtils = api.utilities().compressionUtils();
        editor = context.editorMode() == EditorMode.READ_ONLY
                ? api.userInterface().createRawEditor(EditorOptions.READ_ONLY)
                : api.userInterface().createRawEditor();
    }

    protected HttpMessage export(HttpMessage message) {
        if (!isModified()) {
            return message;
        }

        var content = editor.getContents().toString();

        if (content.isEmpty()) {
            return message;
        }

        try {
            var stream = Serialize.serialize(content);
            var streamBytes = byteArray(stream.array());
            var body = compressionUtils.compress(streamBytes, CompressionType.GZIP);

            // TODO: This can be removed if Burp implements these methods on HttpMessage.
            if (message instanceof HttpRequest request) {
                return request.withBody(body);
            } else if (message instanceof HttpResponse response) {
                return response.withBody(body);
            } else {
                throw new UnsupportedOperationException("Unreachable!");
            }
        } catch (Exception throwable) {
            logging.logToError("Can't export JSON to Java stream", throwable);
            return message;
        }
    }

    protected abstract HttpMessage getMessage(HttpRequestResponse requestResponse);

    private void load(HttpMessage message) {
        try {
            var binary = compressionUtils.decompress(message.body(), CompressionType.GZIP);
            var json = Deserialize.deserialize(binary.getBytes());

            editor.setContents(byteArray(json));

            // TODO: Toggleable for performance
            // This is used to detect inconsistency between the original stream and what we re-encode, this issue can
            // arise when different string definition are in the stream but with the same content (instead of using
            // references).
            if (!Arrays.equals(Serialize.serialize(json).array(), json.getBytes())) {
                logging.logToError("Inconsistency while loading stream");
            }
        } catch (Exception throwable) {
            editor.setContents(byteArray());
            logging.logToError("Can't import Java stream to JSON", throwable);
        }
    }

    public void setRequestResponse(HttpRequestResponse requestResponse) {
        this.requestResponse = requestResponse;

        logging.logToOutput(requestResponse.toString());

        load(getMessage(requestResponse));
    }

    public boolean isEnabledFor(HttpRequestResponse requestResponse) {
        return getMessage(requestResponse).hasHeader("Content-Type", "application/x-java-serialized-object");
    }

    public String caption() {
        return "Java serializable";
    }

    public Component uiComponent() {
        return editor.uiComponent();
    }

    public Selection selectedData() {
        return editor.selection().orElse(null);
    }

    public boolean isModified() {
        return editor.isModified();
    }
}
