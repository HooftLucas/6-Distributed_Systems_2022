package Messages;

public class DeleteFileMessage extends Message {

    // A constructor for the DeleteFileMessage class. It takes in two parameters, sender and fileID. It calls the
    // superclass constructor with the sender parameter. It then sets the type of the message to "DeleteFileMessage" and
    // the content of the message to fileID.
    public DeleteFileMessage(int sender, int fileID) {
        super(sender);
        this.type = "DeleteFileMessage";
        this.content = fileID;
    }
}
