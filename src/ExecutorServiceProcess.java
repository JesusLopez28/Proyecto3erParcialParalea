import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorServiceProcess {
    protected String[][] files;
    protected int option;
    protected List<String[]> filteredFiles;

    public ExecutorServiceProcess(String[][] files, int option) {
        this.files = files;
        this.option = option;
        this.filteredFiles = new ArrayList<>();
    }

    public List<String[]> applyFilter() {
        int numProcesadores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numProcesadores);

        List<Future<?>> tareasFuturas = new ArrayList<>();

        for (String[] file : files) {
            TaskProcess tarea = new TaskProcess(file, option, filteredFiles);
            Future<?> tareaFutura = executorService.submit(tarea);
            tareasFuturas.add(tareaFutura);
        }

        for (Future<?> tareaFutura : tareasFuturas) {
            try {
                tareaFutura.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
        return filteredFiles;
    }

    public static class TaskProcess implements Runnable {
        private final String[] file;
        private final int option;
        private final List<String[]> filteredFiles;

        public TaskProcess(String[] file, int option, List<String[]> filteredFiles) {
            this.file = file;
            this.option = option;
            this.filteredFiles = filteredFiles;
        }

        @Override
        public void run() {
            try {
                SequentialProcess sequentialProcess = new SequentialProcess(new String[][]{file}, option);
                List<String[]> filteredFile = sequentialProcess.applyFilter();
                synchronized (filteredFiles) {
                    for (String[] f : filteredFile) {
                        filteredFiles.add(f);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
