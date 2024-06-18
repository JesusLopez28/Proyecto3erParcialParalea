import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ImpClientServer extends UnicastRemoteObject implements ClientServer {
    private String name;
    private MainServer mainServer;

    protected ImpClientServer(MainServer mainServer, String name) throws RemoteException {
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

    }

    @Override
    public void receiveImageFiles(String[][] files) throws RemoteException {

    }

    @Override
    public void sendProcessedImages(String[][] files) throws RemoteException {

    }

    @Override
    public void receiveProcessedImages(String[][] files) throws RemoteException {

    }
}
