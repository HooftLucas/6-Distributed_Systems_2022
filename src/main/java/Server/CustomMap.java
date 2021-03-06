package Server;

import java.io.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class CustomMap extends ConcurrentSkipListMap<Integer, String> {

    /**
     * This function writes the current map to a file called map.txt.
     */
    public void exportMap() throws IOException {
        FileOutputStream fo = new FileOutputStream("./src/main/java/map.txt");
        ObjectOutputStream out = new ObjectOutputStream(fo);
        out.writeObject(this);
        out.close();
        fo.close();
        System.out.println("Current database saved to ./src/main/map.txt");
    }

    /**
     * It reads the file, converts it to a CustomMap object, and then adds all the entries from the CustomMap object to the
     * current CustomMap object
     */
    public void importMap() throws IOException, ClassNotFoundException {
        FileInputStream fi = new FileInputStream("./src/main/java/map.txt");
        ObjectInputStream in = new ObjectInputStream(fi);
        CustomMap c = (CustomMap) in.readObject();
        in.close();
        fi.close();

        for(ConcurrentSkipListMap.Entry<Integer,String> m :c.entrySet()){
            this.put(m.getKey(), m.getValue());
        }
        System.out.println("Database imported from ./src/main/map.txt");
    }

}
