
package graphics;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public class CheckField extends HBox{
    private final Text description = new Text();
    private final CheckBox checkbox = new CheckBox();

    public CheckField(String description) {
        //this.description.setFont(Font.font("Calibri Light", 13));
        this.description.setText(description);
        this.description.setStroke(Color.WHITE);
        this.description.setTextAlignment(TextAlignment.CENTER);
        getChildren().addAll(this.description, checkbox);
    }
    public CheckField(){
        this("Prop_Detail");
    }
    public void setDescription(String s){
        description.setText(s);
    }
    public void setOnAction (EventHandler<ActionEvent> value){
        checkbox.setOnAction(value);
    }
    public boolean getValue(){
        return checkbox.isSelected();
    }
    public BooleanProperty selectedProperty(){
        return checkbox.selectedProperty();
    }
    public boolean isSelected(){
        return checkbox.isSelected();
    }
    public void setSelected(Boolean v){
        checkbox.setSelected(v);
    }
    @Override
    public String toString() {
        return "Property{" +  description + '}';
    }
    
}
