import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ImpClientServer extends UnicastRemoteObject implements ClientServer {
    protected String name;
    protected MainServer mainServer;
    protected int option;
    protected String process;
    public ImpClientServer(MainServer mainServer, String name) throws RemoteException {
        super();
        this.mainServer = mainServer;
        this.name = name;
        this.option = 0;
        this.process = null;

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
    public void receiveImageFiles(String[][] files, int option, String process) throws RemoteException {
        this.option = option;
        this.process = process;
        String[][] processedImages = new String[files.length][2];

        if (process.equals("sequential")) {
            SequentialProcess sequentialProcess = new SequentialProcess(files, option);
            processedImages = sequentialProcess.applyFilter();
        } else if (process.equals("forkJoin")) {
            ForkJoinProcess forkJoinProcess = new ForkJoinProcess(files, option);
            processedImages = forkJoinProcess.getFilteredFiles();

        } else if (process.equals("executorService")) {
            ExecutorServiceProcess executorServiceProcess = new ExecutorServiceProcess(files, option);
            List<String[]> filteredFiles = executorServiceProcess.applyFilter();
            processedImages = filteredFiles.toArray(new String[filteredFiles.size()][2]);
        }

        sendProcessedImages(processedImages);
    }

    @Override
    public void sendOption(int option, String process) throws RemoteException {
        try {
            mainServer.getOption(option, process);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
