package sh.arnaud.serializeformat;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;

import java.awt.*;

public class RequestEditor implements ExtensionProvidedHttpRequestEditor {
    public RequestEditor(MontoyaApi api, EditorCreationContext creationContext) {
    }

    @Override
    public HttpRequest getRequest() {
        return null;
    }

    @Override
    public void setRequestResponse(HttpRequestResponse requestResponse) {

    }

    @Override
    public boolean isEnabledFor(HttpRequestResponse requestResponse) {
        return false;
    }

    @Override
    public String caption() {
        return null;
    }

    @Override
    public Component uiComponent() {
        return null;
    }

    @Override
    public Selection selectedData() {
        return null;
    }

    @Override
    public boolean isModified() {
        return false;
    }
}
