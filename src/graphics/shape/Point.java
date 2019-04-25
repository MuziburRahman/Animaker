
package graphics.shape;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public class Point extends Circle{

    public Point() {
        this(0, 0);
    }

    public Point(double centerX, double centerY ) {
        super(centerX, centerY, 3 ,  Color.RED);
    }
}
