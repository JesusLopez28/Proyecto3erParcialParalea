import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ImpClientServer extends UnicastRemoteObject implements ClientServer {
    protected String name;
    protected MainServer mainServer;

    public ImpClientServer(MainServer mainServer, String name) throws RemoteException {
        super();
        this.mainServer = mainServer;
        this.name = name;
        try {
            mainServer.registerClientServer(this, name);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendImageFiles(String[][] files) throws RemoteException {
        try {
            mainServer.receiveImageFiles(files, name);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveImageFiles(String[][] files) throws RemoteException {

    }

    @Override
    public void sendProcessedImages(String[][] files) throws RemoteException {
        try {
            mainServer.receiveProcessedImages(files);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveProcessedImages(String[][] files) throws RemoteException {

    }
}
