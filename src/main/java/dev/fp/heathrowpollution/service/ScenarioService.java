package dev.fp.heathrowpollution.service;

import dev.fp.heathrowpollution.model.scenario.ScenarioRow;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ScenarioService {

    public void writeCsv(List<ScenarioRow> rows, String outputFile) {
        try {
            Path path = Path.of(outputFile);
            Files.createDirectories(path.getParent());
            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))) {
                StringBuilder header = new StringBuilder("Date");
                for (int h = 0; h < 24; h++) header.append(",").append(h);
                pw.println(header);
                for (ScenarioRow row : rows) {
                    StringBuilder line = new StringBuilder(row.getDate().toString());
                    for (Double val : row.getPm25()) {
                        line.append(",");
                        if (val != null) line.append(val);
                    }
                    pw.println(line);
                }
            }
            System.out.printf("CSV written to %s%n", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
