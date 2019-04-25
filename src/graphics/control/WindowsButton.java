
package graphics.control;

import graphics.ColorX;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;

/**@author thisismuzib@gmail.com **/

public final class WindowsButton extends Control{
    
    private WindowsButtonSkin mySkin ;
    protected Integer boundaryCircleRadius=7;
    private ColorX fill= ColorX.rgb(200, 200, 200);
    protected Label text = new Label("Button");
    protected Path innerBoundary = new Path();
    private EventHandler<MouseEvent> handleMouseEnterEvent, handleMouseExitEvent, handleMousePressedEvent, handleMouseReleasedEvent;
    private final InnerShadow shadow = new InnerShadow();
    private static Double FONTSIZEDEF=10.0;
    private Boolean graphics_as_description = false;
    
    // constructors
    public WindowsButton() {
        mySkin = new WindowsButtonSkin(this);
        setMinWidth(20);
        setText("Button");
        text.setFont(Font.font("eurofurence bold",FONTSIZEDEF));
        refreshEventHandlers();
    }
    public WindowsButton(String name){
        this();
        setText(name);
    }
    public WindowsButton(Shape s){
        this();
        this.setPrefSize(s.getBoundsInLocal().getWidth()+10, s.getBoundsInParent().getHeight()+4);
        graphics_as_description = true;
        mySkin.setDescription(s);
    }
    public WindowsButton(Double width, Double height){
        this();
        this.resize(width, height);
    }
    // property getters and setters
    public Integer getBackgroundRadius() {
        return boundaryCircleRadius;
    }
    public void setBackgroundRadius(Integer r) {    
        this.boundaryCircleRadius = r;
        mySkin.update();
    }

    public Label getText() {
        return text;
    }
    public void setText(String button_text) {
        setPrefSize(16+button_text.length()*FONTSIZEDEF*0.55- Math.log(button_text.length())/Math.log(1.2),25);
        text.setText( button_text );
    }
    public Color getFill() {
        return fill.toColor();
    }
    public void setFill(Color fill) {
        this.fill = new ColorX(fill);
        innerBoundary.setFill(fill);
        //clearEventHandlers();
        refreshEventHandlers();
    }
    
    public void setTextFont(Font font){
        text.setFont(font);
    }

    public ColorX getTextFill() {
        return new ColorX((Color)this.text.getTextFill());
    }

    public void setTextFill(ColorX textFill) {
        this.text.setTextFill(textFill.toColor());
    }
    
    @Override
    protected Skin<?> createDefaultSkin() {
        return mySkin;
    }
    
    private void clearEventHandlers(){
        this.removeEventHandler(MouseEvent.MOUSE_ENTERED, handleMouseEnterEvent);
        this.removeEventHandler(MouseEvent.MOUSE_EXITED, handleMouseExitEvent);
        this.removeEventHandler(MouseEvent.MOUSE_PRESSED, handleMousePressedEvent);
        this.removeEventHandler(MouseEvent.MOUSE_RELEASED, handleMouseReleasedEvent);
    }
    private void refreshEventHandlers(){
        
        handleMouseEnterEvent = event -> shadow.setColor(new ColorX((Color)text.getTextFill()).brighterBy(0).toColor());
        handleMouseExitEvent = event -> shadow.setColor(Color.BLACK);
        handleMousePressedEvent = event -> innerBoundary.setEffect(shadow);
        handleMouseReleasedEvent = event-> innerBoundary.setEffect(null);
        
        this.addEventHandler(MouseEvent.MOUSE_ENTERED, handleMouseEnterEvent);
        this.addEventHandler(MouseEvent.MOUSE_EXITED, handleMouseExitEvent);
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, handleMousePressedEvent);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, handleMouseReleasedEvent);
    }
}
