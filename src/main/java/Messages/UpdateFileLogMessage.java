package Messages;

import Node.FileLog;

public class UpdateFileLogMessage extends Message{

    private final FileLog log;

    // A constructor for the UpdateFileLogMessage class. It is calling the constructor of the super class Message.
    public UpdateFileLogMessage(int sender, int fileID, FileLog log) {
        super(sender);
        super.type = "UpdateFileLogMessage";
        super.content = fileID;
        this.log = log;
    }

    public FileLog getLog() {
        return log;
    }
}
