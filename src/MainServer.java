import java.rmi.Remote;

public interface MainServer extends Remote {
    void registerClientServer(ClientServer clientServe, String name) throws java.rmi.RemoteException;

    void receiveImageFiles(String[][] files) throws java.rmi.RemoteException;

    void getOption(int option, String process, String name) throws java.rmi.RemoteException;

    void sendImageFiles(String[][] files) throws java.rmi.RemoteException;

    void receiveProcessedImages(String[][] files) throws java.rmi.RemoteException;

    void sendProcessedImages(String[][] files) throws java.rmi.RemoteException;
}
