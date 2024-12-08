package Windows;

import Operations.Bezier;
import Data.DataGroup;
import Data.Point;
import Data.Line;
import FileManager.FileReader;
import FileManager.FileSaver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;

public class BezierWindow {
    private Bezier bezier;
    private FileReader fr;
    private FileSaver fs;
    
    private final int windowH = 600;
    private final int windowW = 900;
    
    private final JFrame frame;
    private JPanel panel;
    public JLabel tLabel;
    public JSlider resolutionSlider;
    public JLabel resolutionLabel;
    public JSlider orderSlider;
    public JLabel orderLabel;
    private JLabel controlLabel;
    
    //visual config
    public final int circleSize = 10;
    public boolean ghost = false;
    public boolean bgColor = false;
    
    public int order = 3; //minimun of 2
    
    //Base dots for line
    public Point mousePoint;
    public Point start;
    public Point end;
    public boolean hasStart = false;
    public boolean hasEnd = false;
    
    //Current moving dot
    private Point selectedPoint = null;
    
    //bezier lines
    public DataGroup<Point> line;
    public ArrayList<Point> bezierList = new ArrayList<>();
    public DataGroup<Line> qList = new DataGroup<>();
    
    //tracing point
    public Point B;
    public double t = 0.0;
    public double resolution = 0.1;
    
    public BezierWindow() {
        bezier = new Bezier(this);
        fr = new FileReader(this, bezier);
        fs = new FileSaver(this);
        
        frame = new JFrame("Bezier Curve");
        frame.setSize(windowW, windowH);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.BLACK);
        
        tLabel = new JLabel();
        tLabel.setBackground(Color.BLACK);
        tLabel.setForeground(Color.WHITE);
        tLabel.setOpaque(true);
        tLabel.setBounds(10, 10, 60, 30);
        tLabel.setText("t = " + String.format("%.2f", t));
        
        resolutionLabel = new JLabel();
        resolutionLabel.setBackground(Color.BLACK);
        resolutionLabel.setForeground(Color.WHITE);
        resolutionLabel.setOpaque(true);
        resolutionLabel.setBounds(10, tLabel.getY() + tLabel.getHeight() + 5, 60, 30);
        resolutionLabel.setText("r = " + String.format("%.2f", resolution));
        
        resolutionSlider = new JSlider(JSlider.VERTICAL, 1, 50, 1);
        resolutionSlider.setValue((int) (resolution * 100.0));
        resolutionSlider.setBounds(10, resolutionLabel.getY() + resolutionLabel.getHeight() + 5, 30, 100);
        resolutionSlider.setBackground(Color.BLACK);
        resolutionSlider.setForeground(Color.WHITE);
        resolutionSlider.setPaintTicks(true);
        resolutionSlider.setMajorTickSpacing(5);
        
        resolutionSlider.addChangeListener((ChangeEvent e) -> {
            if (!hasStart || !hasEnd) {
                resolutionSlider.setValue((int) (resolution * 100.0));
                
                return;
            }
            
            resolution = (double) resolutionSlider.getValue() / 100.0;
            resolutionLabel.setText("r = " + String.format("%.2f", resolution));
            
            bezier.updateLine();
            panel.repaint();
            panel.requestFocus();
        });
        
        orderLabel = new JLabel();
        orderLabel.setBackground(Color.BLACK);
        orderLabel.setForeground(Color.WHITE);
        orderLabel.setOpaque(true);
        orderLabel.setBounds(10, resolutionSlider.getY() + resolutionSlider.getHeight() + 5, 60, 30);
        orderLabel.setText("o = " + order);
        
        orderSlider = new JSlider(JSlider.VERTICAL, 2, 12, 2);
        orderSlider.setValue(order);
        orderSlider.setBounds(10, orderLabel.getY() + orderLabel.getHeight() + 5, 30, 100);
        orderSlider.setBackground(Color.BLACK);
        orderSlider.setForeground(Color.WHITE);
        orderSlider.setPaintTicks(true);
        orderSlider.setMajorTickSpacing(2);
        
        orderSlider.addChangeListener((ChangeEvent e) -> {
            if (!hasStart || !hasEnd) {
                orderSlider.setValue(order);
                
                return;
            }
            
            order = orderSlider.getValue();
            orderLabel.setText("o = " + order);
            
            bezier.reset();
            
            panel.repaint();
            panel.requestFocus();
        });
        
        JButton ghostButton = new JButton("Ghost");
        ghostButton.setBackground(Color.BLACK);
        ghostButton.setForeground(Color.WHITE);
        ghostButton.setFocusable(false);
        ghostButton.setBounds(10, windowH - 40 - 10, 80, 40);
        
        ghostButton.addActionListener(e -> {
            ghost = !ghost;
            
            if (ghost) {
                ghostButton.setBackground(Color.WHITE);
                ghostButton.setForeground(Color.BLACK);
            } else {
                ghostButton.setBackground(Color.BLACK);
                ghostButton.setForeground(Color.WHITE);
            }
            
            panel.repaint();
            panel.requestFocus();
        });
        
        JButton backgroundButton = new JButton("Color");
        backgroundButton.setBackground(Color.BLACK);
        backgroundButton.setForeground(Color.WHITE);
        backgroundButton.setFocusable(false);
        backgroundButton.setBounds(ghostButton.getX() + ghostButton.getWidth() + 5, windowH - 40 - 10, 80, 40);
        
        backgroundButton.addActionListener(e -> {
            bgColor = !bgColor;
            
            if (bgColor) { //bg is white
                backgroundButton.setBackground(Color.WHITE);
                tLabel.setBackground(Color.WHITE);
                resolutionLabel.setBackground(Color.WHITE);
                resolutionSlider.setBackground(Color.WHITE);
                orderLabel.setBackground(Color.WHITE);
                orderSlider.setBackground(Color.WHITE);
                controlLabel.setBackground(Color.WHITE);
                
                backgroundButton.setForeground(Color.BLACK);
                tLabel.setForeground(Color.BLACK);
                resolutionLabel.setForeground(Color.BLACK);
                resolutionSlider.setForeground(Color.BLACK);
                orderLabel.setForeground(Color.BLACK);
                orderSlider.setForeground(Color.BLACK);
                controlLabel.setForeground(Color.BLACK);
                
                panel.setBackground(Color.WHITE);
            } else {
                backgroundButton.setBackground(Color.BLACK);
                tLabel.setBackground(Color.BLACK);
                resolutionLabel.setBackground(Color.BLACK);
                resolutionSlider.setBackground(Color.BLACK);
                orderLabel.setBackground(Color.BLACK);
                orderSlider.setBackground(Color.BLACK);
                controlLabel.setBackground(Color.BLACK);
                
                backgroundButton.setForeground(Color.WHITE);
                tLabel.setForeground(Color.WHITE);
                resolutionLabel.setForeground(Color.WHITE);
                resolutionSlider.setForeground(Color.WHITE);
                orderLabel.setForeground(Color.WHITE);
                orderSlider.setForeground(Color.WHITE);
                controlLabel.setForeground(Color.WHITE);
                
                panel.setBackground(Color.BLACK);
            }
            
            panel.repaint();
            panel.requestFocus();
        });
        
        //Label for controls
        controlLabel = new JLabel("<html>MOUSE click 1 == Start line<br>"
                + "MOUSE click 2 == End line<br>"
                + "MOUSE drag == Move dots<br>"
                + "LEFT || RIGHT == Create curve<br>"
                + "L == Load file<br>"
                + "S == Save file<br>"
                + "BACKSPACE == Erase line</html>");
        controlLabel.setBackground(Color.BLACK);
        controlLabel.setForeground(Color.WHITE);
        controlLabel.setOpaque(true);
        controlLabel.setSize(210, 100);
        controlLabel.setBounds(windowW - controlLabel.getWidth(), 0, controlLabel.getWidth(), controlLabel.getHeight());
        
        //Draw lines
        panel = new DrawPanel(this);
        panel.setOpaque(true);
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(windowW, windowH));
        
        //layers
        JLayeredPane layered = new JLayeredPane();
        layered.setPreferredSize(new Dimension(windowW, windowH));
        panel.setBounds(0, 0, windowW, windowH);
        
        layered.add(panel, Integer.valueOf(1));
        layered.add(tLabel, Integer.valueOf(2));
        layered.add(resolutionSlider, Integer.valueOf(2));
        layered.add(resolutionLabel, Integer.valueOf(2));
        layered.add(orderSlider, Integer.valueOf(2));
        layered.add(orderLabel, Integer.valueOf(2));
        layered.add(ghostButton, Integer.valueOf(2));
        layered.add(backgroundButton, Integer.valueOf(2));
        layered.add(controlLabel, Integer.valueOf(3));
        
        frame.add(layered);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        
        frame.setVisible(true);
        
        panel.requestFocus();
        
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                double mouseX = panel.getMousePosition().x;
                double mouseY = panel.getMousePosition().y;
                
                if (!hasStart) {
                    start = new Point(mouseX, mouseY);
                    mousePoint = new Point(start);
                    hasStart = true;
                    
                    return;
                } else if (!hasEnd) {
                    end = new Point(mouseX, mouseY);
                    hasEnd = true;
                    
                    bezier.createBezier();
                    
                    return;
                }
                
                for (Point p : line.getPointList()) {
                    if (p.distance(mouseX, mouseY) < circleSize) {
                        selectedPoint = p;
                        break;
                    }
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                selectedPoint = null;
                
                panel.repaint();
            }
        });
        
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (hasStart && !hasEnd) {
                    //Prevent error when mouse out of screen
                    if (panel.getMousePosition() != null) {
                        double mouseX = panel.getMousePosition().x;
                        double mouseY = panel.getMousePosition().y;
                        
                        mousePoint.setLocation(mouseX, mouseY);
                        panel.repaint();
                    }
                }
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedPoint != null && panel.getMousePosition() != null) {
                    selectedPoint.setLocation(e.getX(), e.getY());
                    
                    bezier.updateLine();
                    
                    panel.repaint();
                }
            }
        });
        
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean changed = false;
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_L -> {
                        fr.readFile();
                        
                        bezierList.clear();
                        qList = new DataGroup();
                        
                        panel.repaint();
                    }
                    case KeyEvent.VK_S -> {
                        fs.saveFile();
                    }
                    case KeyEvent.VK_BACK_SPACE -> {
                        bezier.resetLine();
                        panel.repaint();
                    }
                    case KeyEvent.VK_LEFT -> {
                        t -= resolution;
                        t = Math.max(0.0, t); //Make sure it can only go down to 0.0
                        
                        if (!bezierList.isEmpty()) {
                            changed = true;
                        } else if (order == 2) {
                            changed = true;
                        }
                    }
                    case KeyEvent.VK_RIGHT -> {
                        t += resolution; //before checking
                        t = Math.min(1.0, t); //Make sure it can only go up to 1.0
                        
                        if (bezierList.size() <= 1.0 / resolution) {
                            changed = true;
                        }
                    }
                }
                
                if (changed) {
                    bezier.updateLine();
                    panel.repaint();
                }
                
                tLabel.setText("t = " + String.format("%.2f", t));
            }
        });
    }
}