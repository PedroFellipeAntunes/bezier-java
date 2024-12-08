package FileManager;

import Data.Point;
import Windows.BezierWindow;
import java.io.*;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSaver {
    private final BezierWindow bw;

    public FileSaver(BezierWindow bw) {
        this.bw = bw;
    }

    public void saveFile() {
        try {
            String jarPath = FileSaver.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File jarFile = new File(jarPath);
            String directory = jarFile.getParent();
            
            String filePath = directory + File.separator + "bezier_line.txt";
            
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            
            writer.println("resolution;" + bw.resolution);
            
            int qCount = 0;
            
            for (Point point : bw.line.getPointList()) {
                if (point.equals(bw.start)) {
                    writer.println("start;" + point.getX() + ";" + point.getY());
                } else if (point.equals(bw.end)) {
                    writer.println("end;" + point.getX() + ";" + point.getY());
                } else {
                    writer.println("q" + qCount + ";" + point.getX() + ";" + point.getY());
                    qCount++;
                }
            }
            
            writer.close();
            
            System.out.println("File saved to: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while writing save the data to file.");
        } catch (URISyntaxException ex) {
            Logger.getLogger(FileSaver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}