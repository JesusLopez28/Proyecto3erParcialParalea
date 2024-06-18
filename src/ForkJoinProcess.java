import java.util.concurrent.RecursiveAction;

public class ForkJoinProcess extends RecursiveAction {
    protected String[][] files;
    protected int option;
    protected String[][] filteredFiles;

    public ForkJoinProcess(String[][] files, int option) {
        this.files = files;
        this.option = option;
        this.filteredFiles = new String[files.length][2];
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

            invokeAll(new ForkJoinProcess(first, option),
                    new ForkJoinProcess(second, option));
        }
    }

    public String[][] getFilteredFiles() {
        return filteredFiles;
    }
}
