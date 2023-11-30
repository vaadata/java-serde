package sh.arnaud.serializeformat.burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpMessage;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpResponseEditor;

public class ResponseEditor extends Editor implements ExtensionProvidedHttpResponseEditor {
    ResponseEditor(MontoyaApi api, EditorCreationContext context) {
        super(api, context);
    }

    protected HttpMessage getMessage(HttpRequestResponse requestResponse) {
        return requestResponse.response();
    }

    public HttpResponse getResponse() {
        return (HttpResponse) export(requestResponse.response());
    }
}
