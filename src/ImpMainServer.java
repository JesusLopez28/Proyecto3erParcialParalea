import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ImpMainServer extends UnicastRemoteObject implements MainServer {
    protected Map<String, ClientServer> clients;
    protected String[][] imageFiles;
    protected String[][] processedImages;
    protected String clientName;
    protected int option;
    protected String process;

    public ImpMainServer() throws RemoteException, IOException {
        super();
        clients = new HashMap<>();
        this.imageFiles = null;
        this.processedImages = null;
        this.clientName = null;
        this.option = 0;
        this.process = null;
    }

    @Override
    public void registerClientServer(ClientServer clientServe, String name) throws RemoteException {
        clients.put(name, clientServe);
        System.out.println("Client " + name + " registered");
    }

    @Override
    public void receiveImageFiles(String[][] files) throws RemoteException {
        if (imageFiles == null) {
            imageFiles = files;
        } else {
            List<String[]> combinedFiles = new ArrayList<>();
            Set<String> fileNames = new HashSet<>();

            for (String[] file : imageFiles) {
                String fileName = file[0];
                if (!fileNames.contains(fileName)) {
                    combinedFiles.add(file);
                    fileNames.add(fileName);
                }
            }

            for (String[] file : files) {
                String fileName = file[0];
                String baseName = getBaseFileName(fileName);
                String extension = getFileExtension(fileName);
                if (!fileNames.contains(fileName)) {
                    combinedFiles.add(file);
                    fileNames.add(fileName);
                } else {
                    String newFileName;
                    int counter = 1;
                    do {
                        newFileName = baseName + "_" + counter + "." + extension;
                        counter++;
                    } while (fileNames.contains(newFileName));
                    String[] newFile = new String[file.length];
                    newFile[0] = newFileName;
                    System.arraycopy(file, 1, newFile, 1, file.length - 1);
                    combinedFiles.add(newFile);
                    fileNames.add(newFileName);
                }
            }

            imageFiles = combinedFiles.toArray(new String[combinedFiles.size()][]);
        }

        System.out.println("Received " + files.length + " images");
    }

    @Override
    public void getOption(int option, String process, String name) throws RemoteException {
        this.option = option;
        this.process = process;
        this.clientName = name;

        System.out.println("Received option " + option + " and process " + process + " from " + name);
        sendImageFiles(imageFiles);
    }

    @Override
    public void sendImageFiles(String[][] files) throws RemoteException {
        int numClients = clients.size();
        int numFiles = files.length;
        int filesPerClient = numFiles / numClients;
        int remainingFiles = numFiles % numClients;

        int start = 0;
        for (Map.Entry<String, ClientServer> entry : clients.entrySet()) {
            int numFilesToSend = filesPerClient + (remainingFiles > 0 ? 1 : 0);
            String[][] filesToSend = new String[numFilesToSend][];
            System.arraycopy(files, start, filesToSend, 0, numFilesToSend);
            start += numFilesToSend;
            remainingFiles--;

            System.out.println("Sent " + filesToSend.length + " images to " + entry.getKey());
            entry.getValue().receiveImageFiles(filesToSend, option, process);
        }

        System.out.println("Sent " + files.length + " images to clients");
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

        if (processedImages.length == imageFiles.length) {
            System.out.println("All images have been processed");
            sendProcessedImages(processedImages);
            processedImages = null;
        }
    }

    @Override
    public void sendProcessedImages(String[][] files) throws RemoteException {
        System.out.println("Sent " + files.length + " processed images to " + this.clientName);
        clients.get(this.clientName).receiveProcessedImages(files);
    }

    private String getBaseFileName(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index != -1 ? fileName.substring(0, index) : fileName;
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index != -1 ? fileName.substring(index + 1) : "";
    }
}
