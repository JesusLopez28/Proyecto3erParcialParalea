import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.List;

public class ImpMainServer extends UnicastRemoteObject implements MainServer {
    protected Map<String, ClientServer> clients;
    protected String[][] imageFiles;
    protected String[][] processedImages;
    protected String clientName;
    protected int option;
    protected String process;
    protected JFrame frame;
    protected JTextArea consoleArea;

    public ImpMainServer() throws RemoteException, IOException {
        super();
        clients = new HashMap<>();
        this.imageFiles = null;
        this.processedImages = null;
        this.clientName = null;
        this.option = 0;
        this.process = null;

        IU();
    }

    @Override
    public void registerClientServer(ClientServer clientServe, String name) throws RemoteException {
        clients.put(name, clientServe);
        consoleArea.append("Cliente " + name + " registrado\n");
    }

    @Override
    public void receiveImageFiles(String[][] files, String name) throws RemoteException {
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

        consoleArea.append("Recibidas " + files.length + " imágenes de " + name + "\n");
    }

    @Override
    public void getOption(int option, String process, String name) throws RemoteException {
        this.option = option;
        this.process = process;
        this.clientName = name;

        consoleArea.append("Recibida opción " + option + " y proceso " + process + " de " + name + "\n");
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

            consoleArea.append("Enviando " + filesToSend.length + " imágenes a " + entry.getKey() + "\n");
            entry.getValue().receiveImageFiles(filesToSend, option, process);
        }

        consoleArea.append("Enviado " + files.length + " imágenes a " + numClients + " clientes\n");
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
            consoleArea.append("Todas las imágenes han sido procesadas\n");
            sendProcessedImages(processedImages);
            processedImages = null;
        }
    }

    @Override
    public void sendProcessedImages(String[][] files) throws RemoteException {
        consoleArea.append("Enviado " + files.length + " imágenes procesadas a " + this.clientName + "\n");
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

    private void IU() {
        frame = new JFrame("Servidor Principal");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        JButton button = new JButton("Limpiar arrays");
        topPanel.add(button);

        consoleArea = new JTextArea(10, 30);
        consoleArea.setEditable(false);
        JScrollPane consoleScroll = new JScrollPane(consoleArea);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(consoleScroll, BorderLayout.CENTER);

        button.addActionListener(e -> {
            imageFiles = null;
            processedImages = null;
            clients.forEach((name, client) -> {
                try {
                    client.getClear();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            });
            consoleArea.append("Arrays limpiados\n");
        });

        frame.setVisible(true);
    }
}
