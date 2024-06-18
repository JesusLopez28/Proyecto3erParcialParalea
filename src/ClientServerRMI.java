import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientServerRMI {
    static final String IP = "localhost";

    public static void main(String[] args) {
        try {
            String nombre = JOptionPane.showInputDialog("Introduce tu nombre: ");
            String nom = nombre;
            Registry rmii = LocateRegistry.getRegistry(IP, 1099);
            MainServer servidor = (MainServer) rmii.lookup("3erParcial");
            new Thread(new ImpClientServer(servidor, nom)).start();
        } catch (Exception e) {
            System.out.println("Excepcion en clienteRMI: " + e);
            e.printStackTrace();
        }
    }

}
