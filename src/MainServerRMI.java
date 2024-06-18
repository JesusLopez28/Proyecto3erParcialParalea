import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainServerRMI {
    public static void main(String[] args) {
        try {
            Registry rmi = LocateRegistry.createRegistry(1099);
            rmi.rebind("3erParcial", new ImpMainServer());
            System.out.println("Servidor Principal RMI listo");
        } catch (Exception e) {
            System.out.println("Excepcion: " + e);
            e.printStackTrace();
        }
    }
}
