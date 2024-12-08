package FileManager;

import Data.DataGroup;
import Data.Point;
import Operations.Bezier;
import Windows.BezierWindow;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileReader {
    private final BezierWindow bw;
    private final Bezier bz;
    
    public FileReader(BezierWindow bw, Bezier bz) {
        this.bw = bw;
        this.bz = bz;
    }
    
    public void readFile() {
        try {
            /*
            For some reason when executed using CMD java -jar BezierCurves.jar
            It saves and reads to the same folder as the .jar
            But when double clicked executed it reads and saves to /home/user
            */
            
            // All this bullshit to make sure it reads and writes to the same folder as the .jar
            String jarPath = FileReader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File jarFile = new File(jarPath);
            String directory = jarFile.getParent();
            
            File file = new File(directory + File.separator + "bezier_line.txt");
            
            Scanner scanner = new Scanner(file);
            
            Map<String, List<Double>> map = new HashMap<>();
            List<String> orderList = new ArrayList<>();
            
            int orderCount = 0;
            
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(";");
                
                if (parts.length >= 2) {
                    List<Double> values = new ArrayList<>();
                    
                    for (int i = 1; i < parts.length; i++) {
                        values.add(Double.valueOf(parts[i]));
                    }
                    
                    map.put(parts[0], values);
                    orderList.add(parts[0]);
                    
                    if (parts[0].equals("start") || parts[0].equals("end") || parts[0].startsWith("q")) {
                        orderCount++;
                    }
                }
            }
            
            //Valid checks
            if (!map.containsKey("resolution") || !map.containsKey("start") || !map.containsKey("end")) {
                System.out.println("Missing data in file");
                return;
            }
            
            double resolution = map.get("resolution").get(0);
            
            if (resolution > 0.5 || resolution < 0.01) {
                System.out.println("Resolution is greater than 0.5 OR smaller than 0.01.");
                return;
            }
            
            //Update values and visuals
            bw.resolution = resolution;
            bw.resolutionLabel.setText("r = " + String.format("%.2f", resolution));
            bw.resolutionSlider.setValue((int) (resolution * 100.0));
            bw.order = orderCount;
            bw.orderLabel.setText("o = " + orderCount);
            bw.orderSlider.setValue(orderCount);
            bw.t = 0.0;
            
            //Create line
            bw.line = new DataGroup<>();
            
            for (String key : orderList) {
                List<Double> values = map.get(key);
                
                if (values.size() >= 2) {
                    Point newPoint = new Point(values.get(0), values.get(1));
                    bw.line.getPointList().add(newPoint);
                    
                    if (key.equals("start")) {
                        bw.start = newPoint;
                        bw.hasStart = true;
                    } else if (key.equals("end")) {
                        bw.end = newPoint;
                        bw.hasEnd = true;
                    }
                }
            }
            
            bz.createBezierRecursive(bw.line, bw.order - 1);
            
            bw.B = new Point(bw.start.getX(), bw.start.getY());
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (URISyntaxException ex) {
            Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}