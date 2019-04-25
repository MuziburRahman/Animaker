
package graphics;

import animaker.AppScreen;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public class Pointer extends Circle{
    
    public static final Integer START=1;
    public static final Integer MIDDLE=2;
    public static final Integer END=3;
    public BooleanProperty active = new SimpleBooleanProperty(false);
    public DoubleProperty distance = new SimpleDoubleProperty();
    private ChangeListener mouse_x_listener , mouse_y_Listener; 
    public Pointer(){/*by default, any pointer is active*/
        setFill(Color.TRANSPARENT);
        setStroke(Color.RED);
        setRadius(8);
        setVisible(false);
        mouse_x_listener = (c,n,o)->{
            double dis = new Point2D(getCenterX(), getCenterY()).distance((Double) n, AppScreen.MOUSE_Y.get());
            distance.set(dis);
        };
        mouse_y_Listener = (c,n,o)->{
            double dis = new Point2D(getCenterX(), getCenterY()).distance(AppScreen.MOUSE_X.get(), (Double) n);
            distance.set(dis);
        };
        active.addListener((c,o,n)->{
            if (n){
                AppScreen.MOUSE_X.addListener(mouse_x_listener);
                AppScreen.MOUSE_Y.addListener(mouse_y_Listener);
            }else {
                AppScreen.MOUSE_X.removeListener(mouse_x_listener);
                AppScreen.MOUSE_Y.removeListener(mouse_y_Listener);
            }
        });
        active.set(true);
    }
    public void setActive(Boolean v){
        active.set(v);
    }
    public Boolean isActive(){
        return active.get();
    }
    public Pointer (double center_x, double center_y){
        this();
        setCenterX(center_x);
        setCenterY(center_y);
    }
    public Pointer(Sirkale c) {
        this();
        centerXProperty().bindBidirectional(c.centerXProperty());
        centerYProperty().bindBidirectional(c.centerYProperty());
    }
    public Pointer (Liein l, Integer type){
        this();
        if (Objects.equals(type, START)) {
            centerXProperty().bindBidirectional(l.startX);
            centerYProperty().bindBidirectional(l.startY);
        }
        else if (Objects.equals(type, END)) {
            centerXProperty().bindBidirectional(l.endX);
            centerYProperty().bindBidirectional(l.endY);
        }
        else if (Objects.equals(type, MIDDLE)) {
            centerXProperty().bind(l.endX.divide(2).add(l.startX.divide(2)));
            centerYProperty().bind(l.endY.divide(2).add(l.startY.divide(2)));
        }
    }

    @Override
    public String toString() {
        return "Pointer{" + "active=" + active + " x=" + getCenterX()+" y=" + getCenterY()+ '}';
    }
    
}
