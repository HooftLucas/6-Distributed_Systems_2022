import Server.NamingServer;
import Node.Node;

import java.io.IOException;


public class Main {
    // arguments have to be given in main
    /**
     * If the first argument is "namingserver", then start a naming server. If the first argument is "node", then start a
     * node
     */
    public static void main(String[] args) throws IOException {
        if (args[0].equalsIgnoreCase("namingserver")) {
            NamingServer namingServer = new NamingServer();

        } else if (args[0].equalsIgnoreCase("node")) {
            System.out.println("[NODE]: Node starting...");
            Node node = new Node(args[1]);

        }
    }
}