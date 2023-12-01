package sh.arnaud.javaserde;

import sh.arnaud.javaserde.de.Deserialize;
import sh.arnaud.javaserde.ser.Serialize;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java-serde <encode|decode>");
            return;
        }

        switch (args[0]) {
            case "encode" -> {
                byte[] stdin = System.in.readAllBytes();
                var stream = Serialize.serialize(new String(stdin));
                System.out.writeBytes(stream.array());
            }

            case "decode" -> {
                byte[] stdin = System.in.readAllBytes();
                var json = Deserialize.deserialize(stdin);
                System.out.writeBytes(json.getBytes());
            }

            default -> System.out.println("Invalid mode, see usage.");
        }
    }
}
