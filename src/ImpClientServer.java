import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ImpClientServer extends UnicastRemoteObject implements ClientServer {
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

        IU();
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

    public void IU() {
        frame.setTitle(name);
        frame.setSize(720, 460);
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

        labelRutaGuardar = new JLabel("Ruta para guardar:");
        labelRutaGuardar.setBounds(20, 70, 200, 30);
        panelCentral.add(labelRutaGuardar);

        textFieldRutaGuardar = new JTextField(10);
        textFieldRutaGuardar.setBounds(190, 70, 320, 30);
        panelCentral.add(textFieldRutaGuardar);

        buttonRutaGuardar = new JButton("Seleccionar ruta");
        buttonRutaGuardar.setBounds(530, 70, 150, 30);
        panelCentral.add(buttonRutaGuardar);

        labelFiltro = new JLabel("Filtro a aplicar:");
        labelFiltro.setBounds(20, 120, 200, 30);
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
        comboBoxFiltro.setBounds(190, 120, 320, 30);
        panelCentral.add(comboBoxFiltro);

        buttonLimpiar = new JButton("Limpiar");
        buttonLimpiar.setBounds(530, 120, 150, 30);
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
        panelBotones.setBounds(20, 170, 660, 190);

        frame.add(panelCentral, BorderLayout.CENTER);
    }
}
