
package animaker;

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
            ((SequentialTransition) mainAnim).getChildren().add(sa.mainAnim);
            if(sa instanceof SingleAnimation)
                ((SingleAnimation) sa).removeListeners();
        });
    }
    public ObservableList<Anymation> getChieldren(){
        return children;
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
