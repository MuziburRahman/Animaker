
package graphics;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public class Cursor extends StackPane{

    public Cursor() {
        Line l1 = new Line(25,0,25,50), l2=new Line(0,25,50,25);
        l1.setStroke(Color.WHITE);
        l2.setStroke(Color.WHITE);
        getChildren().addAll(l1, l2);
    }
    
}
