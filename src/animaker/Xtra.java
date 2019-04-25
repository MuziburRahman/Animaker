
package animaker;

import java.util.List;
import java.util.Random;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public class Xtra {
    
    public static final Double GOLDENRATIO = 1.61803398875;
    public static final Double GR = 1.61803398875;
    public static final Double GR_INVERSE = 0.61803398875;
    public static final Random RAN = new Random();
    
    public static<T> T chooseFrom (T... objects){
        return objects[RAN.nextInt(objects.length)];
    }
    public static<T> T chooseFrom (List<T> l){
        return l.get(RAN.nextInt(l.size()));
    }
    public static boolean inRange (double lowerlimit, double upperlimit, double num){
        return lowerlimit<upperlimit? num>=lowerlimit && num <= upperlimit : num<=lowerlimit && num >= upperlimit;
    }
    public static boolean inRangeWithPrecision(double lowerlimit, double upperlimit, double num, double precision){
        return lowerlimit<upperlimit? 
                num>=lowerlimit-precision && num <= upperlimit+precision : num<=lowerlimit+precision && num >= upperlimit-precision;
    }
    public static Double atan (double y, double x){
        if (x>=0 && y>=0)
            return Math.atan(y/x);
        else if (x<0 && y>=0)
            return Math.PI - Math.atan(y/Math.abs(x));
        else if (x<0 && y<0)
            return Math.PI + Math.atan(y/x);
        else
            return 2*Math.PI - Math.atan(Math.abs(y)/x);
    }
    public static double atan (double sourceX, double sourceY, double otherX, double otherY){
        return atan(otherY-sourceY, otherX-sourceX);
    }
    public static<T> Boolean addIfDoesnotExist(ObservableList<T> ol , T... obj){
        Boolean shouldInsert = true;
        for (T obj1 : obj) 
            if(ol.indexOf(obj1)==-1)
                ol.add(obj1);
            else
                shouldInsert = false;
        return shouldInsert;
    }
    public static<T> void removeIfExist(ObservableList<T> ol , T obj){
        if (ol.indexOf(obj)!=-1)
            ol.remove(obj);
    }
}
