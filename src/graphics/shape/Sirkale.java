
package graphics.shape;

import animaker.Property;
import animaker.AppScreen;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.converter.NumberStringConverter;
/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public class Sirkale extends Circle{
    
    public final DoubleProperty mouseDistance = new SimpleDoubleProperty();
    public boolean isHovered = false;
    
    private final Property prop_radius= new Property("Radius:", Property.SIZE_MEDIUM);
    public Boolean hasAnim = false;
    
    public Sirkale(double cx, double cy) {
        setCenter(cx, cy);
        setFill(Color.TRANSPARENT);
        setStroke(Color.GREEN);
        setStrokeWidth(2);
        setPickOnBounds(false);
        prop_radius.getTextProperty().bindBidirectional(radiusProperty(), new NumberStringConverter());
        AppScreen.MOUSE_X.addListener((c,n,o)-> mouseDistance.set(distanceFromCenter((Double) n, AppScreen.MOUSE_Y.get())));
        AppScreen.MOUSE_Y.addListener((c,n,o)-> mouseDistance.set(distanceFromCenter(AppScreen.MOUSE_X.get(), (Double) n)));
        radiusProperty().addListener((c,o,n)->{
            if((double)n <=0 )
                setVisible(false);
            else if (!isVisible())
                setVisible(true);
        });
    }
    public Sirkale(double radius){
        this(0, 0);
        setRadius(radius);
    }
    public final void setCenter(double x, double y){
        this.centerXProperty().set(x);
        this.centerYProperty().set(y);
    }
    public double distanceFromCenter (double x, double y){
        return Math.sqrt(Math.pow(getCenterX()-x, 2)+Math.pow(getCenterY()-y, 2));
    }
}