package Operations;

import Data.DataGroup;
import Data.Line;
import Data.Point;
import Windows.BezierWindow;
import java.util.ArrayList;

public class Bezier {
    private final BezierWindow bw;
    
    public Bezier(BezierWindow bw) {
        this.bw = bw;
    }
    
    public void reset() {
        bw.bezierList.clear();
        bw.qList = new DataGroup<>();
        bw.t = 0.0;
        
        createBezier();
    }
    
    public void resetLine() {
        bw.bezierList.clear();
        bw.qList = new DataGroup<>();
        bw.t = 0.0;
        
        bw.hasStart = false;
        bw.hasEnd = false;
    }
    
    public void createBezier() {
        //Add start
        bw.line = new DataGroup<>();
        bw.line.getPointList().add(bw.start);
        
        double dx = (bw.end.getX() - bw.start.getX()) / (bw.order - 1);
        double dy = (bw.end.getY() - bw.start.getY()) / (bw.order - 1);
        
        //Create the base line, ignoring the start and end points
        for (int i = 0; i < bw.order - 2; i++) {
            double x = bw.start.getX() + (i + 1) * dx;
            double y = bw.start.getY() + (i + 1) * dy;
            
            Point newPoint = new Point(x, y);
            bw.line.getPointList().add(newPoint);
        }
        
        //Add end
        bw.line.getPointList().add(bw.end);
        
        createBezierRecursive(bw.line, bw.order - 1);
        
        //Bezier point starts at start
        bw.B = new Point(bw.start.getX(), bw.start.getY());
    }
    
    public void createBezierRecursive(DataGroup<Point> group, int currentOrder) {
        if (currentOrder < 2) {
            return;
        }
        
        //create child group
        DataGroup<Point> childGroup = new DataGroup<>();
        group.setChildGroup(childGroup);
        
        ArrayList<Point> points = group.getPointList();
        ArrayList<Point> newPoints = new ArrayList<>();
        
        //child group points should start at the same spots as the points - 1
        for (int i = 0; i < currentOrder; i++) {
            double x = points.get(i).getX();
            double y = points.get(i).getY();
            
            newPoints.add(new Point(x, y));
        }
        
        childGroup.setPointList(newPoints);
        
        createBezierRecursive(childGroup, currentOrder - 1);
    } 
    
    public void updateLine() {
        if (bw.line != null) {
            if (bw.line.getChildGroup() != null) {
                bw.bezierList.clear();
                bw.qList = new DataGroup<>();
                
                if (bw.t < bw.resolution) { //t is close to 0
                    recursiveUpdateLine(bw.line, bw.qList, bw.t);
                    
                    return;
                }
                
                for (double i = 0.0 + bw.resolution; i <= bw.t; i += bw.resolution) {
                    //Make sure it ends at 1.0
                    if (i != bw.t && i + bw.resolution > bw.t) {
                        i = bw.t;
                    }
                    
                    recursiveUpdateLine(bw.line, bw.qList, i);
                }
            } else {
                bw.bezierList.clear();
                
                if (bw.t < bw.resolution) { //t is close to 0
                    updateB(bw.t);
                    
                    return;
                }
                
                for (double i = 0.0 + bw.resolution; i <= bw.t; i += bw.resolution) {
                    //Make sure it ends at 1.0
                    if (i != bw.t && i + bw.resolution > bw.t) {
                        i = bw.t;
                    }
                    
                    updateB(i);
                }
            }
        }
    }
    
    private void updateB(double localT) {
        bw.B = updatePoint(bw.line.getPointList().get(0), bw.line.getPointList().get(1), localT);
        bw.bezierList.add(new Point(bw.B));
    }
    
    private void recursiveUpdateLine(DataGroup<Point> fatherGroup, DataGroup<Line> qCurrent, double localT) {
        if (fatherGroup.getChildGroup() == null) {
            //Will always be 2 points
            Point current = fatherGroup.getPointList().get(0);
            Point next = fatherGroup.getPointList().get(1);
            
            //Save to Q list
            qCurrent.getPointList().add(new Line(new Point(current), new Point(next)));
            
            //Update bezier
            bw.B = updatePoint(current, next, localT);
            
            //Make sure B goes back to p0
            if (localT < bw.resolution) {
                bw.B.setLocation(bw.line.getPointList().get(0).getX(), bw.line.getPointList().get(0).getY());
            }
            
            //Update bezier list
            if (localT >= bw.resolution) {
                bw.bezierList.add(new Point(bw.B));
            }
            
            return;
        }
        
        ArrayList<Point> fatherPoints = fatherGroup.getPointList();
        
        DataGroup<Point> currentGroup = fatherGroup.getChildGroup();
        ArrayList<Point> currentPointList = currentGroup.getPointList();
        
        for (int i = 0; i < currentPointList.size(); i++) {
            //Will always go to the last - 1 of father list
            Point currentPoint = fatherPoints.get(i);
            Point nextPoint = fatherPoints.get(i + 1);
            
            //Update point position
            currentPointList.set(i, updatePoint(currentPoint, nextPoint, localT));
            
            //Reset to original position
            if (localT < bw.resolution) {
                currentPointList.get(i).copyPoint(currentPoint);
            }
            
            //Save to Q list
            qCurrent.getPointList().add(new Line(new Point(currentPoint), new Point(nextPoint)));
        }
        
        //Create a child before calling the recursion
        if (qCurrent.getChildGroup() == null) {
            qCurrent.createChildGroup();
        }
        
        recursiveUpdateLine(currentGroup, qCurrent.getChildGroup(), localT);
    }
    
    private Point updatePoint(Point a, Point b, double localT) {
        double x = a.getX() + localT * (b.getX() - a.getX());
        double y = a.getY() + localT * (b.getY() - a.getY());
        
        return new Point(x, y);
    }
}