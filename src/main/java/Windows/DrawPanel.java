package Windows;

import Data.DataGroup;
import Data.Line;
import Data.Point;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;

public class DrawPanel extends JPanel {
    private final BezierWindow bw;
    
    public DrawPanel(BezierWindow bw) {
        this.bw = bw;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        //Draw mouse line or return
        if (bw.hasStart && !bw.hasEnd) {
            g.setColor(Color.red);
            
            g.drawLine((int) bw.start.getX(), (int) bw.start.getY(), (int) bw.mousePoint.getX(), (int) bw.mousePoint.getY());
            g.drawOval((int) bw.start.getX() - bw.circleSize / 2, (int) bw.start.getY() - bw.circleSize / 2, bw.circleSize, bw.circleSize);
            g.drawOval((int) bw.mousePoint.getX() - (bw.circleSize * 3) / 2, (int) bw.mousePoint.getY() - (bw.circleSize * 3) / 2, bw.circleSize * 3, bw.circleSize * 3);
            
            return;
        } else if (!bw.hasStart && !bw.hasEnd) {
            return;
        }
        
        //Draw guidelines
        if (bw.qList.getChildGroup() != null) {
            recursivePaintGuideLines(g, bw.line.getChildGroup(), bw.qList.getChildGroup(), 0.0f);
        } else {
            recursivePaintGuideLines(g, bw.line.getChildGroup(), null, 0.0f);
        }
        
        //Draw actual line
        paintLine(g, bw.line.getPointList());
        
        //Draw bezier curve
        Point p0 = bw.line.getPointList().get(0);
        paintBezierCurve(g, bw.bezierList, p0);
        
        //Draw bezier line
        if (!bw.bgColor) { //bg is black
            g.setColor(new Color(255, 255, 255, 127));
        } else {
            g.setColor(new Color(0, 0, 0, 127));
        }
        
        g.drawLine((int) p0.getX(), (int) p0.getY(), (int) bw.B.getX(), (int) bw.B.getY());
        
        //Draw bezier dot
        if (!bw.bgColor) { //bg is black
            g.setColor(Color.BLACK);
        } else {
            g.setColor(Color.WHITE);
        }
        
        g.fillOval((int) bw.B.getX() - bw.circleSize / 2, (int) bw.B.getY() - bw.circleSize / 2, bw.circleSize, bw.circleSize);
        
        if (!bw.bgColor) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.BLACK);
        }
        
        g.drawOval((int) bw.B.getX() - bw.circleSize / 2, (int) bw.B.getY() - bw.circleSize / 2, bw.circleSize, bw.circleSize);
    }
    
    private void paintBezierCurve(Graphics g, ArrayList<Point> list, Point p0) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));
        
        if (!bw.bgColor) { //bg is black
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.BLACK);
        }
        
        for (int i = 0; i < list.size(); i++) {
            Point currentPoint = list.get(i);
            
            if (i > 0) {
                Point previousPoint = list.get(i - 1);
                g.drawLine((int) previousPoint.getX(), (int) previousPoint.getY(), (int) currentPoint.getX(), (int) currentPoint.getY());
            } else {
                g.drawLine((int) p0.getX(), (int) p0.getY(), (int) currentPoint.getX(), (int) currentPoint.getY());
            }
        }
        
        g2d.setStroke(new BasicStroke(1));
    }
    
    private void paintLine(Graphics g, ArrayList<Point> points) {
        g.setColor(new Color(127, 127, 127));
        
        for (int i = 0; i < points.size(); i++) {
            Point currentPoint = points.get(i);
            
            if (i > 0) {
                Point previousPoint = points.get(i - 1);
                
                g.drawLine((int) previousPoint.getX(), (int) previousPoint.getY(), (int) currentPoint.getX(), (int) currentPoint.getY());
                g.drawOval((int) currentPoint.getX() - bw.circleSize / 2, (int) currentPoint.getY() - bw.circleSize / 2, bw.circleSize, bw.circleSize);
            } else {
                g.drawOval((int) currentPoint.getX() - bw.circleSize / 2, (int) currentPoint.getY() - bw.circleSize / 2, bw.circleSize, bw.circleSize);
            }
        }
    }
    
    private void recursivePaintGuideLines(Graphics g, DataGroup<Point> currentData, DataGroup<Line> qList, float hue) {
        if (currentData == null) {
            return;
        }
        
        //HSB to RGB
        int RGB = Color.HSBtoRGB(hue, 1.0f, 1.0f);
        
        //Get RGB
        int red = (RGB >> 16) & 0xFF;
        int green = (RGB >> 8) & 0xFF;
        int blue = RGB & 0xFF;
        
        //RGB with alpha
        g.setColor(new Color(red, green, blue, 255 / 2));
        
        //For previous lines
        if (bw.ghost && qList != null) {
            ArrayList<Line> ghostLines = qList.getPointList();
            
            for (int i = 0; i < ghostLines.size(); i++) {
                Point p1 = ghostLines.get(i).getP1();
                Point p2 = ghostLines.get(i).getP2();
                
                g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
            }
        }
        
        //Current lines
        g.setColor(new Color(red, green, blue, 255));
        
        ArrayList<Point> lines = currentData.getPointList();
        
        for (int i = 0; i < lines.size(); i++) {
            Point currentPoint = lines.get(i);
            
            if (i > 0) {
                Point previousPoint = lines.get(i - 1);
                
                g.drawLine((int) previousPoint.getX(), (int) previousPoint.getY(), (int) currentPoint.getX(), (int) currentPoint.getY());
                g.drawOval((int) currentPoint.getX() - bw.circleSize / 2, (int) currentPoint.getY() - bw.circleSize / 2, bw.circleSize, bw.circleSize);
            } else {
                g.drawOval((int) currentPoint.getX() - bw.circleSize / 2, (int) currentPoint.getY() - bw.circleSize / 2, bw.circleSize, bw.circleSize);
            }
        }
        
        if (qList != null) {
            recursivePaintGuideLines(g, currentData.getChildGroup(), qList.getChildGroup(), 1.0f / bw.order + hue);
        } else {
            recursivePaintGuideLines(g, currentData.getChildGroup(), qList, 1.0f / bw.order + hue);
        }
    }
}