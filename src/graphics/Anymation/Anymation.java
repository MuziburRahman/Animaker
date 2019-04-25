
package graphics.Anymation;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public abstract class Anymation{
    public Animation mainAnim;
    public static final int SEQUENTIAL=1, PARALLEL=2, SINGLE=3;
    public String description = "null";
    protected Double time = 3.0;
    protected final List<SingleAnimation> singles = new ArrayList();

    public abstract void play();
    
    public abstract void setDurationInSecond(double t);
    
    public abstract List<SingleAnimation> getSingleAnimations();
    public void setDurationInSecond(String s){
        setDurationInSecond(Double.valueOf(s));
    }
    public void setDescription(String s){
        description = s;
    }
    public Status getStatus(){
        return mainAnim.getStatus();
    }
    
    public void setOnFinished(EventHandler<ActionEvent> eh){
        mainAnim.setOnFinished(eh);
    }
}
