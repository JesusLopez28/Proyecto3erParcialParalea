import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ImpMainServer extends UnicastRemoteObject implements MainServer{
    private Map<String, ClientServer> clients;

    protected ImpMainServer() throws RemoteException {
        super();
        clients = new HashMap<>();
    }

    @Override
    public void registerClientServer(ClientServer clientServe, String name) throws RemoteException {
        clients.put(name, clientServe);
    }

    @Override
    public void receiveImageFiles(String[][] files) throws RemoteException {

    }

    @Override
    public void sendImageFiles(String[][] files) throws RemoteException {

    }

    @Override
    public void receiveProcessedImages(String[][] files) throws RemoteException {

    }

    @Override
    public void sendProcessedImages(String[][] files) throws RemoteException {

    }
}
