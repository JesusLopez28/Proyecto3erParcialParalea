import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ImpMainServer extends UnicastRemoteObject implements MainServer {
    protected Map<String, ClientServer> clients;
    protected String[][] imageFiles;
    protected String[][] processedImages;
    protected String clientName;

    public ImpMainServer() throws RemoteException {
        super();
        clients = new HashMap<>();
        this.imageFiles = null;
        this.processedImages = null;
        this.clientName = null;
    }

    @Override
    public void registerClientServer(ClientServer clientServe, String name) throws RemoteException {
        clients.put(name, clientServe);
    }

    @Override
    public void receiveImageFiles(String[][] files, String name) throws RemoteException {
        this.clientName = name;
        if (imageFiles == null) {
            imageFiles = files;
        } else {
            String[][] temp = new String[imageFiles.length + files.length][];
            System.arraycopy(imageFiles, 0, temp, 0, imageFiles.length);
            System.arraycopy(files, 0, temp, imageFiles.length, files.length);
            imageFiles = temp;
        }
        sendImageFiles(files);
    }

    @Override
    public void sendImageFiles(String[][] files) throws RemoteException {
        for (Map.Entry<String, ClientServer> entry : clients.entrySet()) {
            try {
                entry.getValue().receiveImageFiles(files);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void receiveProcessedImages(String[][] files) throws RemoteException {
        if (processedImages == null) {
            processedImages = files;
        } else {
            String[][] temp = new String[processedImages.length + files.length][];
            System.arraycopy(processedImages, 0, temp, 0, processedImages.length);
            System.arraycopy(files, 0, temp, processedImages.length, files.length);
            processedImages = temp;
        }

        sendProcessedImages(files);
    }

    @Override
    public void sendProcessedImages(String[][] files) throws RemoteException {
        clients.get(clientName).receiveProcessedImages(files);
        this.clientName = null;
    }
}
