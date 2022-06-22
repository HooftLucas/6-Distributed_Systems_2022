package Server;

import Utils.HashFunction;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class NamingServer {
    private final CustomMap nodeMap;
    private HashMap<Integer, Integer> fileMap;
    private HashMap<Integer, FailureWatcher> failureMap;

    private NamingServerUDPInterface udpInterface;

    // The constructor of the NamingServer class. It initializes the nodeMap, fileMap and failureMap. It also starts the
    // UDP interface.
    public NamingServer()  {
        System.out.println("[NAMINGSERVER]: Starting...");
        nodeMap = new CustomMap();
        fileMap = new HashMap<>();
        failureMap = new HashMap<>();
        try {
            this.udpInterface = new NamingServerUDPInterface(this);
            new Thread(this.udpInterface).start();
        } catch (UnknownHostException | SocketException e) {
            System.err.println("Failed to start NSUDPinterface " + e);
        }

    }

    /**
     * If the nodeMap doesn't already contain the nodeID, add it to the map and export the map to the file
     *
     * @param name the name of the node
     * @param IP The IP address of the node.
     * @return The IP address of the node.
     */
    @PostMapping("/NamingServer/Nodes/{node}")
    public String addNodeREST(@PathVariable(value = "node") String name, @RequestBody String IP) throws IOException {
        int nodeID = HashFunction.hash(name);
        if (nodeMap.putIfAbsent(nodeID, IP) == null) {
            nodeMap.exportMap();
            return "[NS REST] Node added with ID=" + nodeID + "!";
        } else {
            return "[NS REST] This name is not available!\n";
        }
    }


    /**
     * It removes a node from the nodeMap and exports the nodeMap to a file
     *
     * @param name The name of the node to be removed
     * @return The return value is a string.
     */
    @DeleteMapping("/NamingServer/Nodes/{node}")
    public String removeNodeREST(@PathVariable(value = "node") String name) throws IOException {
        int nodeID = HashFunction.hash(name);
        if(nodeMap.remove(nodeID) == null) {
            return "[NS REST] Node " + name + " does not exist\n" ;

        } else {
            nodeMap.exportMap();
            return "[NS REST] Removed node " + name + "\n";
        }
    }


    /**
     * It takes in a node name, hashes it, and returns the hash value, the number of nodes in the network, and a list of
     * all the nodes in the network
     *
     * @param name The name of the node you want to check
     * @return The return is a JSON object that contains the status of the node, the hash of the node, the number of nodes
     * in the network, and a list of all the nodes in the network.
     */
    @GetMapping("/NamingServer/Nodes/{node}")
    public String getNodes(@PathVariable(value = "node") String name){
        int nodeID = HashFunction.hash(name);
        String send;
        Set<Map.Entry<Integer,String>> entries = nodeMap.entrySet();
        if(nodeMap.containsKey(nodeID)) {
            int i = 1;
            StringBuilder nodes = new StringBuilder();

            for (Map.Entry<Integer, String> entry : entries) {
                nodes.append("Node #");
                nodes.append(i);
                nodes.append(": ");
                nodes.append(entry.getKey().toString());
                nodes.append(" with IP ");
                nodes.append(entry.getValue());
                nodes.append(",");
                i++;
            }

            send = "{\"Node status\":\"node exists\"," + "\"Node hash\":" + nodeID + "," +
                    "\"Nodes in network\":" + nodeMap.size() +
                    "\"All nodes\":\"" + nodes + "\"}";
        }
        else{
            send = "{\"Node does not exist\"}";
        }
        return send;
    }

    /**
     * If the nodeID is not already in the nodeMap, add it and start a FailureWatcher thread for it
     *
     * @param nodeID The hash of the node's name
     * @param IP The IP address of the node to be added
     * @return The return value is a string that is either "Added node with hash " + nodeID + " and IP" + IP + " to
     * database" or "Name with hash " + nodeID + " not available"
     */
    public String addNode(int nodeID, InetAddress IP) throws IOException {
        String ip = IP.getHostAddress();
        if (nodeMap.putIfAbsent(nodeID, ip) == null) {

            FailureWatcher f = new FailureWatcher(this, IP, nodeID);
            failureMap.put(nodeID, f);
            f.start();
            nodeMap.exportMap();
            return "Added node with hash " + nodeID + " and IP" + IP + " to database";
        } else {
            return "Name with hash " + nodeID + " not available";
        }
    }

    /**
     * If the node exists, remove it from the nodeMap and interrupt the thread that is monitoring it
     *
     * @param nodeID The hash of the node to be deleted
     * @return The nodeID of the node that was deleted.
     */
    public String deleteNode(int nodeID) throws IOException {
        if(nodeMap.remove(nodeID) == null) {
            return "Node with hash " + nodeID + " does not exist";
        } else {
            failureMap.get(nodeID).interrupt();
            failureMap.remove(nodeID);
            nodeMap.exportMap();
            return "Node with hash " + nodeID + " is deleted";
        }
    }

    /**
     * If the node exists, remove it from the nodeMap and failureMap and export the nodeMap to the file system
     *
     * @param nodeID The hash of the node to be deleted
     * @return The return value is a string that is either a confirmation that the node was deleted or a message that the
     * node does not exist.
     */
    public String deleteFailedNode(int nodeID) throws IOException {
        if(nodeMap.remove(nodeID) == null) {
            return " [NAMINGSERVER] Node with hash " + nodeID + " does not exist";
        } else {
            failureMap.remove(nodeID);
            nodeMap.exportMap();
            return "[NAMINGSERVER] Node with hash " + nodeID + " is deleted";
        }
    }

    /**
     * If the nodeID is the lowest nodeID, return the highest nodeID, otherwise return the nodeID that is one less than the
     * nodeID
     *
     * @param nodeID The nodeID of the node you want to find the lower node of.
     * @return The lower key of the nodeID.
     */
    public int getLowerNodeID(int nodeID) throws IOException {
        if(nodeMap.lowerKey(nodeID) ==null) {
            return nodeMap.lastKey();
        } else {
            return nodeMap.lowerKey(nodeID);
        }
    }

    /**
     * If the nodeID is the highest nodeID, return the lowest nodeID, otherwise return the next highest nodeID
     *
     * @param nodeID The ID of the node you want to get the upper node ID of.
     * @return The nodeID of the next node in the nodeMap.
     */
    public int getUpperNodeID(int nodeID) throws IOException {
        if(nodeMap.higherKey(nodeID) == null ) {
            return nodeMap.firstKey();
        } else {
            return nodeMap.higherKey(nodeID);
        }
    }


    public int getServerID() {
        return 0;
    }

    public int getNodeCount() {
        return nodeMap.size();
    }

    public NamingServerUDPInterface getUdpInterface() {
        return udpInterface;
    }

    public FailureWatcher getNodeFailureWatcher(int nodeID) {
        return this.failureMap.get(nodeID);
    }

    /**
     * > Given a fileID and a senderID, return the owner of the file
     *
     * @param fileID The ID of the file you want to find the owner of.
     * @param senderID The ID of the node that is sending the request
     * @return The ID of the node that owns the file.
     */
    public int getFileOwner(int fileID, int senderID) throws UnknownHostException {
        int owner = senderID;
        for(int key : this.nodeMap.keySet()) {
            if(key < fileID) {
                owner = key;
            }
        }
        return owner;
    }

    public String getNodeIP(int nodeID) {
        return nodeMap.get(nodeID);
    }

    public HashMap<Integer, Integer> getFileMap() {
        return fileMap;
    }

    // testing purposes
    /**
     * It creates a new NamingServer object
     */
    public static void main(String[] args) {
        System.out.println("[NAMINGSERVER]: Starting...");
        NamingServer namingServer = new NamingServer();

    }
}
