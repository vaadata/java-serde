package sh.arnaud.serializeformat;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class SerializeFormat implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("serialize-format");

        ExtensionProvider provider = new ExtensionProvider(api);

        api.userInterface().registerHttpRequestEditorProvider(provider);
    }
}