package app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GenericHelper {

    public static void writeToCSV(String outputFile, double[][] results) {
        try (FileOutputStream fw = new FileOutputStream(outputFile, false)) {
            for (double[] x : results) {
                for (double y : x) {
                    fw.write(String.valueOf(y).getBytes(StandardCharsets.UTF_8));
                    fw.write(",".getBytes(StandardCharsets.UTF_8));
                }
                fw.write("\n".getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
