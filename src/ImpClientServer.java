import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class ImpClientServer extends UnicastRemoteObject implements ClientServer, Runnable {
    protected String name;
    protected MainServer mainServer;
    protected int option;
    protected String process;
    protected JFrame frame;
    protected JLabel titulo;
    protected JLabel labelRuta;
    protected JTextField textFieldRuta;
    protected JLabel labelRutaGuardar;
    protected JTextField textFieldRutaGuardar;
    protected JButton buttonRuta;
    protected JButton buttonEnviar;
    protected JButton buttonRutaGuardar;
    protected JLabel labelFiltro;
    protected JComboBox<String> comboBoxFiltro;
    protected JButton buttonLimpiar;
    protected JButton buttonSecuencial;
    protected JButton buttonForkJoin;
    protected JButton buttonExecutorService;
    protected JLabel labelTiempoSecuencial;
    protected JLabel labelTiempoForkJoin;
    protected JLabel labelTiempoExecutorService;
    protected JButton buttonRutaOriginal;
    protected JButton buttonRutaFiltrada;
    protected long startTimeSecuencial;
    protected long endTimeSecuencial;
    protected long startTimeForkJoin;
    protected long endTimeForkJoin;
    protected long startTimeExecutorService;
    protected long endTimeExecutorService;

    public ImpClientServer(MainServer mainServer, String name) throws RemoteException, IOException {
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

        IU();
    }

    @Override
    public void sendImageFiles(String[][] files) throws RemoteException {
        try {
            mainServer.receiveImageFiles(files);

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Imágenes enviadas", "Información", JOptionPane.INFORMATION_MESSAGE);
            });
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
            List<String[]> filteredFiles = sequentialProcess.applyFilter();
            processedImages = filteredFiles.toArray(new String[filteredFiles.size()][2]);
        } else if (process.equals("forkJoin")) {
            ForkJoinPool pool = ForkJoinPool.commonPool();
            ForkJoinProcess forkJoinProcess = new ForkJoinProcess(files, option);
            pool.invoke(forkJoinProcess);
            List<String[]> filteredFiles = forkJoinProcess.getFilteredFiles();
            processedImages = filteredFiles.toArray(new String[filteredFiles.size()][2]);
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
            mainServer.getOption(option, process, name);
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
        for (String[] file : files) {
            byte[] imageBytes = Base64.getDecoder().decode(file[1]);
            File imageFile = new File(textFieldRutaGuardar.getText() + "/" + file[0]);
            try {
                Files.write(imageFile.toPath(), imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SwingUtilities.invokeLater(() -> {
            if (process.equals("sequential")) {
                endTimeSecuencial = System.currentTimeMillis();
                labelTiempoSecuencial.setText("Tiempo: " + (endTimeSecuencial - startTimeSecuencial) + " ms");
            } else if (process.equals("forkJoin")) {
                endTimeForkJoin = System.currentTimeMillis();
                labelTiempoForkJoin.setText("Tiempo: " + (endTimeForkJoin - startTimeForkJoin) + " ms");
            } else if (process.equals("executorService")) {
                endTimeExecutorService = System.currentTimeMillis();
                labelTiempoExecutorService.setText("Tiempo: " + (endTimeExecutorService - startTimeExecutorService) + " ms");
            }
            JOptionPane.showMessageDialog(null, "Proceso finalizado", "Información", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void IU() {
        frame = new JFrame();
        frame.setTitle(name);
        frame.setSize(720, 510);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titulo = new JLabel("Aplicador de filtros", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelSuperior.add(titulo);
        frame.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(null);

        labelRuta = new JLabel("Ruta de las imágenes:");
        labelRuta.setBounds(20, 20, 200, 30);
        panelCentral.add(labelRuta);

        textFieldRuta = new JTextField(10);
        textFieldRuta.setBounds(190, 20, 320, 30);
        panelCentral.add(textFieldRuta);

        buttonRuta = new JButton("Seleccionar ruta");
        buttonRuta.setBounds(530, 20, 150, 30);
        panelCentral.add(buttonRuta);

        buttonEnviar = new JButton("Enviar");
        buttonEnviar.setBounds(20, 70, 660, 30);
        panelCentral.add(buttonEnviar);

        labelRutaGuardar = new JLabel("Ruta para guardar:");
        labelRutaGuardar.setBounds(20, 120, 200, 30);
        panelCentral.add(labelRutaGuardar);

        textFieldRutaGuardar = new JTextField(10);
        textFieldRutaGuardar.setBounds(190, 120, 320, 30);
        panelCentral.add(textFieldRutaGuardar);

        buttonRutaGuardar = new JButton("Seleccionar ruta");
        buttonRutaGuardar.setBounds(530, 120, 150, 30);
        panelCentral.add(buttonRutaGuardar);

        labelFiltro = new JLabel("Filtro a aplicar:");
        labelFiltro.setBounds(20, 170, 200, 30);
        panelCentral.add(labelFiltro);

        String[] filtros = {
                "Seleccione un filtro",
                "Escala de grises",
                "Invertir colores",
                "Binarizar",
                "Posterizar",
                "Brillo",
                "Sepia"
        };
        comboBoxFiltro = new JComboBox<>(filtros);
        comboBoxFiltro.setBounds(190, 170, 320, 30);
        panelCentral.add(comboBoxFiltro);

        buttonLimpiar = new JButton("Limpiar");
        buttonLimpiar.setBounds(530, 170, 150, 30);
        panelCentral.add(buttonLimpiar);

        JPanel panelBotones = new JPanel(new GridLayout(4, 2, 10, 20));
        buttonSecuencial = new JButton("Secuencial");
        buttonSecuencial.setEnabled(false);
        buttonForkJoin = new JButton("ForkJoin");
        buttonForkJoin.setEnabled(false);
        buttonExecutorService = new JButton("ExecutorService");
        buttonExecutorService.setEnabled(false);
        labelTiempoSecuencial = new JLabel();
        labelTiempoSecuencial.setText("Tiempo: ");
        labelTiempoForkJoin = new JLabel();
        labelTiempoForkJoin.setText("Tiempo: ");
        labelTiempoExecutorService = new JLabel();
        labelTiempoExecutorService.setText("Tiempo: ");

        buttonRutaOriginal = new JButton("Ver imágenes originales");
        buttonRutaFiltrada = new JButton("Ver imágenes filtradas");
        buttonRutaOriginal.setEnabled(false);
        buttonRutaFiltrada.setEnabled(false);

        panelBotones.add(buttonSecuencial);
        panelBotones.add(labelTiempoSecuencial);
        panelBotones.add(buttonForkJoin);
        panelBotones.add(labelTiempoForkJoin);
        panelBotones.add(buttonExecutorService);
        panelBotones.add(labelTiempoExecutorService);
        panelBotones.add(buttonRutaOriginal);
        panelBotones.add(buttonRutaFiltrada);

        panelCentral.add(panelBotones);
        panelBotones.setBounds(20, 220, 660, 190);

        frame.add(panelCentral, BorderLayout.CENTER);

        buttonRuta.addActionListener(e -> chooseDirectory(textFieldRuta, textFieldRutaGuardar, buttonRutaOriginal));
        buttonEnviar.addActionListener(e -> send());
        buttonRutaGuardar.addActionListener(e -> chooseDirectory(textFieldRutaGuardar, textFieldRuta, buttonRutaFiltrada));
        comboBoxFiltro.addActionListener(e -> checkComboBoxSelection());
        buttonLimpiar.addActionListener(e -> clearFields());
        buttonSecuencial.addActionListener(e -> secuencialProcess());
        buttonForkJoin.addActionListener(e -> forkJoinProcess());
        buttonExecutorService.addActionListener(e -> executorServiceProcess());
        buttonRutaOriginal.addActionListener(e -> showOriginalImages());
        buttonRutaFiltrada.addActionListener(e -> showFilteredImages());
    }

    private void send() {
        if (!textFieldRuta.getText().isEmpty()) {
            File[] files = new File(textFieldRuta.getText()).listFiles();
            try {
                String[][] base64 = new String[files.length][2];
                for (int i = 0; i < files.length; i++) {
                    base64[i][0] = files[i].getName();
                    base64[i][1] = Base64.getEncoder().encodeToString(Files.readAllBytes(files[i].toPath()));
                }
                sendImageFiles(base64);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            JOptionPane.showMessageDialog(null, "La ruta no puede estar vacía", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chooseDirectory(JTextField textField, JTextField textFieldRutaContra, JButton button) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int selection = fileChooser.showOpenDialog(null);

        if (selection == JFileChooser.APPROVE_OPTION && !textFieldRutaContra.getText().equals(fileChooser.getSelectedFile().getAbsolutePath())) {
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            enableButtons();
            button.setEnabled(true);
        } else {
            disableButtons();
            JOptionPane.showMessageDialog(null, "Las rutas no pueden ser iguales, ni estar vacías", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkComboBoxSelection() {
        if (comboBoxFiltro.getSelectedIndex() != 0 && !textFieldRuta.getText().isEmpty() && !textFieldRutaGuardar.getText().isEmpty()) {
            if (!textFieldRuta.getText().equals(textFieldRutaGuardar.getText())) {
                enableButtons();
            } else {
                disableButtons();
                JOptionPane.showMessageDialog(null, "Las rutas no pueden ser iguales", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            disableButtons();
        }
    }

    private void enableButtons() {
        buttonSecuencial.setEnabled(true);
        buttonForkJoin.setEnabled(true);
        buttonExecutorService.setEnabled(true);
    }

    private void disableButtons() {
        buttonSecuencial.setEnabled(false);
        buttonForkJoin.setEnabled(false);
        buttonExecutorService.setEnabled(false);
    }

    private void clearFields() {
        textFieldRuta.setText("");
        textFieldRutaGuardar.setText("");
        comboBoxFiltro.setSelectedIndex(0);
        labelTiempoSecuencial.setText("Tiempo: ");
        labelTiempoForkJoin.setText("Tiempo: ");
        labelTiempoExecutorService.setText("Tiempo: ");
    }

    private void showOriginalImages() {
        try {
            Desktop.getDesktop().open(new File(textFieldRuta.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showFilteredImages() {
        try {
            Desktop.getDesktop().open(new File(textFieldRutaGuardar.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateAllFields() {
        if (textFieldRuta.getText().isEmpty() || textFieldRutaGuardar.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Las rutas no pueden estar vacías", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (comboBoxFiltro.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un filtro", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (textFieldRuta.getText().equals(textFieldRutaGuardar.getText())) {
            JOptionPane.showMessageDialog(null, "Las rutas no pueden ser iguales", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void secuencialProcess() {
        if (validateAllFields()) {
            try {
                startTimeSecuencial = System.currentTimeMillis();
                sendOption(comboBoxFiltro.getSelectedIndex(), "sequential");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void forkJoinProcess() {
        if (validateAllFields()) {
            try {
                startTimeForkJoin = System.currentTimeMillis();
                sendOption(comboBoxFiltro.getSelectedIndex(), "forkJoin");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void executorServiceProcess() {
        if (validateAllFields()) {
            try {
                startTimeExecutorService = System.currentTimeMillis();
                sendOption(comboBoxFiltro.getSelectedIndex(), "executorService");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        frame.setVisible(true);
    }
}
