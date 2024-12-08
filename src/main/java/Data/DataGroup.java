package Data;

import java.util.ArrayList;

public class DataGroup<Generic> {
    private ArrayList<Generic> pointList;
    private DataGroup childGroup;
    
    public DataGroup() {
        this.pointList = new ArrayList<>();
    }
    
    @Override
    public String toString() {
        return "DataGroup{" + "pointList: " + pointList + ", childGroup: " + childGroup + '}';
    }
    
    public void createChildGroup() {
        childGroup = new DataGroup();
    }
    
    public ArrayList<Generic> getPointList() {
        return pointList;
    }
    
    public void setPointList(ArrayList<Generic> pointList) {
        this.pointList = pointList;
    }
    
    public DataGroup getChildGroup() {
        return childGroup;
    }
    
    public void setChildGroup(DataGroup childGroup) {
        this.childGroup = childGroup;
    }
}