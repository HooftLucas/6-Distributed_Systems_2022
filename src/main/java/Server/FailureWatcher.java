package Server;

import Messages.FailureMessage;
import Messages.Message;
import Messages.PingMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class FailureWatcher extends Thread {

    private final InetAddress address;
    private final int nodeID;
    private final NamingServer server;
    public AtomicInteger timeOutCounter;
    public final int timeoutInterval = 2000;

    // The constructor of the FailureWatcher class. It is used to initialize the variables of the class.
    public FailureWatcher(NamingServer server, InetAddress nodeAddress, int nodeID) {
        this.address = nodeAddress;
        this.nodeID = nodeID;
        this.server = server;
        this.timeOutCounter = new AtomicInteger(3);
    }

    public int getTimeOutCounter() {
        return timeOutCounter.get();
    }


    /**
     * If the current value of the timeOutCounter is the same as the value I read, then set the timeOutCounter to the new
     * value. If the current value of the timeOutCounter is not the same as the value I read, then try again.
     */
    public void incrementTimeOutCounter() {
        while (true) {
            int existingValue = getTimeOutCounter();
            int newValue = existingValue + 1;
            if(timeOutCounter.compareAndSet(existingValue, newValue)) {
                return;
            }
        }
    }

    /**
     * If the current value of the timeOutCounter is equal to the existingValue, then set the timeOutCounter to the
     * newValue.
     */
    public void decrementTimeOutCounter() {
        while (true) {
            int existingValue = getTimeOutCounter();
            int newValue = existingValue - 1;
            if(timeOutCounter.compareAndSet(existingValue, newValue)) {
                return;
            }
        }
    }


    /**
     * The FailureWatcher thread sends a ping message to the node it is watching. If the node does not respond within a
     * certain time interval, the FailureWatcher thread sends a FailureMessage to all other nodes in the network
     */
    @Override
    public void run() {
        System.out.println("[NS UDP]: started FailureWatcher for node " + nodeID);
        while (true) {
            try {
                PingMessage ping = new PingMessage(server.getServerID());
                server.getUdpInterface().sendUnicast(ping, address, 8001);
                try {
                    Thread.sleep(timeoutInterval);
                } catch (InterruptedException e) {
                    return;
                }
                decrementTimeOutCounter();
                if(timeOutCounter.get() < 3)
                {
                    System.out.println("[NS FAIL]: Node " + nodeID + " unreachable for " + timeoutInterval* (3-timeOutCounter.get())
                    + "ms");

                }
                if(timeOutCounter.get() == 0) {

                    int belowFailed = server.getLowerNodeID(nodeID);
                    int aboveFailed = server.getUpperNodeID(nodeID);
                    FailureMessage mFailure = new FailureMessage(server.getServerID(), nodeID, belowFailed, aboveFailed);

                    server.getUdpInterface().sendMulticast(mFailure);

                    System.out.println("[NS FAIL]: Node " + nodeID + " failed \n");
                    String s = server.deleteFailedNode(nodeID);
                    System.out.println(s);
                    return;
                }

            } catch ( IOException e) {
                e.printStackTrace();
            }

        }

    }

}
