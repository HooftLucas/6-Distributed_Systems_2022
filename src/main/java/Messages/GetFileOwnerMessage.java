package Messages;

public class GetFileOwnerMessage extends Message {

    // A constructor for the GetFileOwnerMessage class. It is calling the constructor of the super class Message and
    // setting the type and content of the message.
    public GetFileOwnerMessage(int sender, int fileID) {
        super(sender);
        super.type = "GetFileOwnerMessage";
        super.content = fileID;
    }
}
