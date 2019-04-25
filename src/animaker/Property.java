
package animaker;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.converter.NumberStringConverter;

/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public class Property extends HBox{
    public static final Integer SIZE_SMALL=3, SIZE_BIG=4, SIZE_MEDIUM =5;
    private final DoubleProperty Observable = new SimpleDoubleProperty();
    
    private final Text description = new Text();
    private final TextField input = new TextField();

    public Property(){
        this("Prop_Detail", SIZE_SMALL);
    }
    public Property(String description, int size) {
        this.setAlignment(Pos.CENTER_LEFT);
        this.description.setText(description);
        this.description.setStroke(Color.WHITE);
        input.setMaxWidth(size==SIZE_SMALL? 25:(size==SIZE_MEDIUM? 39:55));
        input.textProperty().bindBidirectional(Observable, new NumberStringConverter());
        getChildren().addAll(this.description, input);
    }
    public DoubleProperty getObservable(){
        return Observable;
    }
    public final void setObservable(DoubleProperty v){
        this.Observable.bindBidirectional(v);
    }
    public void setDescription(String s){
        description.setText(s);
    }
    public void setPromptText(String s){
        input.setPromptText(s);
    }
    public void setOnAction (EventHandler<ActionEvent> value){
        input.setOnAction(value);
    }
    public String getText(){
        return input.getText();
    }
    public void setText(String s){
        input.setText(s);
    }
    public StringProperty getTextProperty(){
        return input.textProperty();
    }
    public ReadOnlyBooleanProperty getFocusProperty(){
        return input.focusedProperty();
    }
    public void setmaxWidth(double w){
        input.setMaxWidth(w);
    }
    @Override
    public String toString() {
        return "Property{" +  description + '}';
    }
}
