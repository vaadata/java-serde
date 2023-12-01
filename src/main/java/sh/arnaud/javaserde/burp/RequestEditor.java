package sh.arnaud.javaserde.burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpMessage;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;

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
