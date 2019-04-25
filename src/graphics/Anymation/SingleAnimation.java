
package graphics.Anymation;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.util.Duration;

/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public final  class SingleAnimation extends Anymation{
    private KeyFrame kf;
    private DoubleProperty observable;
    private Double endValue = 1.0;
    
    
    public SingleAnimation() {
        mainAnim = new Timeline();
    }
    public SingleAnimation(DoubleProperty obs, double endV, double time_in_sec) {
        mainAnim = new Timeline();
        singles.add(this);
        setObservable(obs);
        setEndValue(endV);
        setDurationInSecond(time_in_sec);
    }

    public SingleAnimation(DoubleProperty observable) {
        this(observable , 1 , 3);
    }
    
    
    public void setObservable(DoubleProperty obs){
        observable =obs;
        setKeyFrame();
    }

    public DoubleProperty getObservable() {
        return observable;
    }
    
    public void setEndValue(String value){
        setEndValue(Double.valueOf(value));
    }
    public void setEndValue(double value){
        endValue = value;
        setKeyFrame();
    }

    public Double getEndValue() {
        return endValue;
    }
    
    public Double getDuration(){
        return time;
    }
    
    private void setKeyFrame(){
        ((Timeline)mainAnim).getKeyFrames().clear();
        kf = new KeyFrame(Duration.seconds(time), new KeyValue(observable, endValue));
        ((Timeline)mainAnim).getKeyFrames().add(kf);
    }
    
    @Override
    public List<SingleAnimation> getSingleAnimations(){
        return singles;
    }
    @Override
    public void setDurationInSecond(double second){
        time = second;
        setKeyFrame();
    }
    
    @Override
    public void play(){
        mainAnim.play();
    }
    
    @Override
    public String toString() {
        return description+" End: "+endValue+" Duration: "+time+", duration = "+mainAnim.getTotalDuration() ;
    }
}
