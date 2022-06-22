package Messages;

public class DiscoveryMessage extends Message{
    // Calling the constructor of the super class Message and setting the type of the message to "DiscoveryMessage"
    public DiscoveryMessage(int sender) {
        super(sender);
        super.type = "DiscoveryMessage";

    }
}
