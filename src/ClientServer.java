import java.rmi.Remote;

public interface ClientServer extends Remote {
    void sendImageFiles(String[][] files) throws java.rmi.RemoteException;

    void receiveImageFiles(String[][] files) throws java.rmi.RemoteException;

    void sendProcessedImages(String[][] files) throws java.rmi.RemoteException;

    void receiveProcessedImages(String[][] files) throws java.rmi.RemoteException;
}
