package Messages;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class FileOwnerIDMessage extends Message {
    private InetAddress ownerIP;
    private int ownerID;

    // This is a constructor for the FileOwnerIDMessage class. It takes in the sender, fileID, ownerID, and ownerIP as
    // parameters. It then sets the type of the message to "FileOwnerIDMessage" and sets the content of the message to the
    // fileID. It then sets the ownerIP and ownerID to the parameters passed in.
    public FileOwnerIDMessage(int sender, int fileID, int ownerID ,InetAddress ownerIP) {
        super(sender);
        super.type="FileOwnerIDMessage";
        super.content = fileID;
        this.ownerIP = ownerIP;
        this.ownerID = ownerID;
    }

    // This is a constructor for the FileOwnerIDMessage class. It takes in the sender and fileID as parameters. It then
    // sets the type of the message to "FileOwnerIDMessage" and sets the content of the message to the fileID. It then sets
    // the ownerIP to 0.0.0.0.
    public FileOwnerIDMessage(int sender, int fileID) throws UnknownHostException {
        super(sender);
        super.type="FileOwnerIDMessage";
        super.content = fileID;
        this.ownerIP = InetAddress.getByName("0.0.0.0");
    }

    public InetAddress getOwnerIP() {
        return ownerIP;
    }

    public int getOwnerID() {
        return ownerID;
    }
}
