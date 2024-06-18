import java.rmi.Remote;

public interface ClientServer extends Remote {
    void sendImageFiles(String[][] files) throws java.rmi.RemoteException;

    void receiveImageFiles(String[][] files, int option, String process) throws java.rmi.RemoteException;

    void sendOption(int option, String process) throws java.rmi.RemoteException;

    void sendProcessedImages(String[][] files) throws java.rmi.RemoteException;

    void receiveProcessedImages(String[][] files) throws java.rmi.RemoteException;
}
