
package graphics.Anymation;

import java.util.List;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public final  class SequentialAnymation extends Anymation{
    private final ObservableList<Anymation> children;
    
    public SequentialAnymation(Anymation... animations) {
        this(FXCollections.observableArrayList(animations)); 
    }
    public SequentialAnymation(ObservableList<Anymation> animations) {
        children = FXCollections.observableArrayList(animations);
        description = "Sequential";
        mainAnim = new SequentialTransition();
        animations.forEach((sa) ->{
            ((ParallelTransition) mainAnim).getChildren().add(sa.mainAnim);
            singles.addAll(sa.getSingleAnimations());
        });
    }
    public ObservableList<Anymation> getChieldren(){
        return children;
    }
    @Override
    public List<SingleAnimation> getSingleAnimations(){
        return singles;
    }
    @Override
    public void setDurationInSecond(double t){
        children.forEach(sa->sa.setDurationInSecond(t));
    }
    @Override
    public void play(){
        mainAnim.play();
    }
    @Override
    public String toString() {
        return description+", size = "+ children.size();
    }
}
