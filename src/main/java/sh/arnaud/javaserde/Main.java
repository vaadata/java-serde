package sh.arnaud.javaserde;

import sh.arnaud.javaserde.codec.Decode;
import sh.arnaud.javaserde.codec.Encode;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java-serde <encode|decode>");
            return;
        }

        switch (args[0]) {
            case "encode" -> {
                byte[] stdin = System.in.readAllBytes();
                var stream = Encode.serialize(new String(stdin));
                System.out.writeBytes(stream.array());
            }

            case "decode" -> {
                byte[] stdin = System.in.readAllBytes();
                var json = Decode.decode(stdin);
                System.out.writeBytes(json.getBytes());
            }

            default -> System.out.println("Invalid mode, see usage.");
        }
    }
}
