package sh.arnaud.serializeformat.burp;

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
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpResponseEditor;
import burp.api.montoya.utilities.CompressionType;
import burp.api.montoya.utilities.CompressionUtils;
import sh.arnaud.serializeformat.de.Deserialize;
import sh.arnaud.serializeformat.ser.Serialize;

import java.awt.*;
import java.util.Arrays;

import static burp.api.montoya.core.ByteArray.byteArray;

public class RequestEditor extends Editor implements ExtensionProvidedHttpRequestEditor {
    RequestEditor(MontoyaApi api, EditorCreationContext context) {
        super(api, context);
    }

    protected HttpMessage getMessage(HttpRequestResponse requestResponse) {
        return requestResponse.request();
    }

    public HttpRequest getRequest() {
        return (HttpRequest) export(requestResponse.request());
    }
}
