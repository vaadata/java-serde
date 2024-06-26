package sh.arnaud.javaserde.burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class SerializeFormat implements BurpExtension {
    public static MontoyaApi api;

    @Override
    public void initialize(MontoyaApi api) {
        SerializeFormat.api = api;

        api.extension().setName("serialize-format");

        ExtensionProvider provider = new ExtensionProvider(api);

        api.userInterface().registerHttpRequestEditorProvider(provider);
        api.userInterface().registerHttpResponseEditorProvider(provider);

        api.logging().logToOutput("Serialize Format loaded successfully.");
    }
}