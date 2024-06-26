import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class ForkJoinProcess extends RecursiveAction {
    protected String[][] files;
    protected int option;
    protected List<String[]> filteredFiles;

    public ForkJoinProcess(String[][] files, int option) {
        this.files = files;
        this.option = option;
        this.filteredFiles = new ArrayList<>();
    }

    @Override
    public void compute() {
        if (files.length <= 2) {
            SequentialProcess sequentialProcess = new SequentialProcess(files, option);
            filteredFiles = sequentialProcess.applyFilter();
        } else {
            int half = files.length / 2;
            String[][] first = new String[half][2];
            String[][] second = new String[files.length - half][2];
            System.arraycopy(files, 0, first, 0, half);
            System.arraycopy(files, half, second, 0, second.length);

            ForkJoinProcess firstTask = new ForkJoinProcess(first, option);
            ForkJoinProcess secondTask = new ForkJoinProcess(second, option);

            invokeAll(firstTask, secondTask);

            filteredFiles.addAll(firstTask.getFilteredFiles());
            filteredFiles.addAll(secondTask.getFilteredFiles());
        }
    }

    public List<String[]> getFilteredFiles() {
        return filteredFiles;
    }
}
