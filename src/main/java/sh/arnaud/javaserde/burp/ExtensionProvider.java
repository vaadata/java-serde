package sh.arnaud.javaserde.burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.editor.extension.*;

public class ExtensionProvider implements HttpRequestEditorProvider, HttpResponseEditorProvider {
    private final MontoyaApi api;

    public ExtensionProvider(MontoyaApi api) {
        this.api = api;
    }

    @Override
    public ExtensionProvidedHttpRequestEditor provideHttpRequestEditor(EditorCreationContext creationContext) {
        return new RequestEditor(api, creationContext);
    }

    @Override
    public ExtensionProvidedHttpResponseEditor provideHttpResponseEditor(EditorCreationContext creationContext) {
        return new ResponseEditor(api, creationContext);
    }
}
