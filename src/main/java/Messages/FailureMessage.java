package Messages;

public class FailureMessage extends Message{
    final int failedNext;
    final int failedPrev;

    // This is the constructor for the FailureMessage class. It is calling the constructor of the super class Message and
    // setting the type of the message to "FailureMessage".
    public FailureMessage(int sender, int failedNode, int failedPrevious, int failedNext) {
        super(sender);
        super.content = failedNode;
        super.type= "FailureMessage";
        this.failedNext = failedNext;
        this.failedPrev = failedPrevious;
    }


    // This is a getter method for the failedNext and failedPrev variables.
    public int getFailedNext() {
        return failedNext;
    }

    public int getFailedPrev() {
        return failedPrev;
    }
}
