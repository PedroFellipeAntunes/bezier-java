package Data;

public class Point {
    private double x;
    private double y;
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return "Point{" + "x: " + x + ", y: " + y + '}';
    }
    
    //Construct copy point
    public Point(Point point) {
        x = point.x;
        y = point.y;
    }
    
    //copy method
    public void copyPoint(Point point) {
        x = point.x;
        y = point.y;
    }
    
    //Get distance between xy and current point
    public double distance(double x, double y) {
        double dx = this.x - x;
        double dy = this.y - y;
        
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double getX() {
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
    }
}