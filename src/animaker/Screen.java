
package animaker;

import graphics.CheckField;
import graphics.Liein;
import graphics.PointPolar;
import graphics.Pointer;
import graphics.Sirkale;
import graphics.WindowsButton;
import java.util.Objects;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public class Screen {
    public final BorderPane MAINSCREEN = new BorderPane();
    private final AnchorPane canvas = new AnchorPane();
    private final HBox topPane = new HBox(1), bottomPane = new HBox(1);
    private final ObservableList<Pointer> pointers = FXCollections.observableArrayList(), closestPointers = FXCollections.observableArrayList();
    private final ObservableList<Shape> closestShapes = FXCollections.observableArrayList(),screen_related_refreshable_nodes = FXCollections.observableArrayList();
    private final ObservableList<Shape> drawnShapes = FXCollections.observableArrayList();
    
    private final WindowsButton btn_circle = new WindowsButton("Circle");
    private final WindowsButton btn_line = new WindowsButton("Line");
    private final WindowsButton btn_anim_toggle = new WindowsButton("Toggle animation");
    private final CheckField prop_grid = new CheckField("Show gridlines");
    private final Property prop_stroke = new Property("Stroke: ", Property.SIZE_SMALL);
    private final ColorPicker prop_color = new ColorPicker(Color.RED);
    private final Property prop_length = new Property("Length: ", Property.SIZE_BIG);
    private final Property prop_angle = new Property("Angle: ", Property.SIZE_BIG);
    private final ComboBox<String> prop_linetype = new ComboBox(FXCollections.observableArrayList("line", "<-line","line->","<-line->"));
    
    private final Timeline anim_single_node = new Timeline();
    private WritableValue anim_single_node_target ;
    private final ComboBox<String> prop_animType_liein = new ComboBox(FXCollections.observableArrayList("Length", "Angle"));
    private final ComboBox<String> prop_animType_sirkale = new ComboBox(FXCollections.observableArrayList("Radius", "Position"));
    private final CheckField prop_bAnimation = new CheckField("Add animation: ");
    private final Property  prop_anim_to_value = new Property("To: ", Property.SIZE_MEDIUM);
    private final Property prop_anim_duration = new Property("Duration: ", Property.SIZE_MEDIUM);
    private final Property prop_radius = new Property("Radius: ", Property.SIZE_BIG);
    
    
    private final int  DRAWCIRCLE=1, DRAWELINE=2, DRAGDETECTED=3, DRAGENDED=4, GRIDGAPE=44 ;
    public static final int SCREENWIDTH = 1100;
    public static final DoubleProperty MOUSE_X = new SimpleDoubleProperty(), MOUSE_Y = new SimpleDoubleProperty();
    private int toDraw=0,  dragCondition=0 ;
    private Shape focusedShape, hoveredShape;
    private Pointer focusedPointer;
    private Point2D firstClick ;
    private Liein lastFocusedLiein;
    private Sirkale lastFocusedSirkale;

    public Screen() {
        btn_line.setOnMouseClicked(e->{
            toDraw=DRAWELINE;
            bottomPane.getChildren().clear();
            bottomPane.getChildren().add(prop_linetype);
            prop_linetype.setOnAction(f->{
                switch (prop_linetype.getValue()) {
                case "<-line":
                    Liein.curType = Liein.TYPE_START_ARROW;
                    break;
                case "line->":
                    Liein.curType = Liein.TYPE_END_ARROW;
                    break;
                case "<-line->":
                    Liein.curType = Liein.TYPE_TWO_ARROW;
                    break;
                case "line":
                    Liein.curType = Liein.TYPE_NO_ARROW;
                    break;
                }
            });
        });
        btn_circle.setOnMouseClicked(e->{
            toDraw = DRAWCIRCLE;
            bottomPane.getChildren().clear();
        });
        prop_animType_liein.setOnAction(e->{
            String s = prop_animType_liein.getValue();
            if (s!=null){
                prop_anim_to_value.setVisible(true);
                prop_anim_duration.setVisible(true);
                anim_single_node_target = "Length".equals(s)? lastFocusedLiein.length : lastFocusedLiein.angle;
            }
        });
        prop_anim_duration.getTextProperty().addListener((c,o,n)->{
            if (Integer.valueOf(n)>0) {
                btn_anim_toggle.setVisible(true);
            }
        });
        btn_anim_toggle.setOnMouseClicked(e->{
            KeyValue kv = new KeyValue(anim_single_node_target, Double.valueOf(prop_anim_to_value.getText()));
            anim_single_node.getKeyFrames().clear();
            anim_single_node.getKeyFrames().add(new KeyFrame(Duration.seconds(Integer.valueOf(prop_anim_duration.getText())), kv));
            anim_single_node.play();
        });
        prop_animType_liein.visibleProperty().addListener((c,o,n)->{
            if(!n){
                prop_anim_duration.setVisible(false);
                prop_anim_to_value.setVisible(false);
            }
        });
        
        bottomPane.setStyle("-fx-background-color: #333333");
        topPane.setStyle("-fx-background-color: #333333");
        bottomPane.setMinHeight(25);
        prop_grid.setSelected(true);
        prop_color.setMaxWidth(30);
        prop_linetype.setMaxWidth(80);
        prop_linetype.setPromptText("Type:");
        topPane.getChildren().addAll(btn_circle,btn_line,prop_grid,btn_anim_toggle);
        prop_animType_liein.visibleProperty().bind(prop_bAnimation.selectedProperty());
        prop_anim_to_value.setVisible(false);
        prop_anim_duration.setVisible(false);
        btn_anim_toggle.setVisible(false);
        prop_animType_liein.setPromptText("Animtype:");
        
        
        drawnShapes.addListener((ListChangeListener<Shape>)(e->{
            while (e.next()) {                
                if(e.wasAdded())
                    canvas.getChildren().add(e.getAddedSubList().get(0));
                else if (e.wasRemoved())
                    canvas.getChildren().remove(e.getRemoved().get(0));
            }
        }));
        pointers.addListener((ListChangeListener<Pointer>)(e->{
            while (e.next()) {
                if(e.wasAdded()){
                    Pointer p = e.getAddedSubList().get(0);
                    p.distance.addListener((c,o,n)->{
                        if ((double) n <= 10) {
                            if(closestPointers.indexOf(p) == -1)
                                closestPointers.add(p);
                        }
                        else if (closestPointers.indexOf(p) != -1) {
                            closestPointers.remove(p);
                        }
                    });
                    canvas.getChildren().add(p);
                }else if (e.wasRemoved()) {
                    canvas.getChildren().remove(e.getRemoved().get(0));
                }
            }
        }));
        closestPointers.addListener((ListChangeListener<Pointer>)(e->{
            while (e.next()) {
                if(e.wasAdded() && prop_grid.isSelected() && toDraw!=0){
                    if (closestPointers.size()>1)
                        closestPointers.sort((p1,p2)-> p1.distance.get()-p2.distance.get() < 0 ? -1:1);
                    if (closestPointers.get(0)!= focusedPointer) {
                        if(focusedPointer!=null)
                            focusedPointer.setVisible(false);
                        focusedPointer = closestPointers.get(0);
                        focusedPointer.setVisible(true);
                    }
                } else if (e.wasRemoved()) {
                    if (closestPointers.size()>=1) {
                        if (closestPointers.size()>1)
                            closestPointers.sort((p1,p2)-> p1.distance.get()-p2.distance.get() < 0 ? -1:1);
                        if (closestPointers.get(0)!= focusedPointer){
                            if(focusedPointer!=null)
                                focusedPointer.setVisible(false);
                            focusedPointer = closestPointers.get(0);
                            focusedPointer.setVisible(true);
                        }
                    } else { /*size == 0*/
                        if(focusedPointer!=null)
                            focusedPointer.setVisible(false);
                        focusedPointer = null;
                    }
                }
            }
        }));
        closestShapes.addListener((ListChangeListener<Shape>)(e->{
            while (e.next()) {                
                if (e.wasAdded()) {
                    if (closestShapes.size() > 1)
                        closestShapes.sort((sh1,sh2)->{
                            double distance_1 = sh1 instanceof Liein ? ((Liein) sh1).distance_from_mouse.get(): ((Sirkale) sh1).mouseDistance.get();
                            double distance_2 = sh2 instanceof Liein ? ((Liein) sh2).distance_from_mouse.get(): ((Sirkale) sh2).mouseDistance.get();
                            return distance_1-distance_2<0? -1: (distance_1-distance_2==0? 0:1);
                        });
                    if (closestShapes.get(0)!= hoveredShape){
                        if(hoveredShape!=null)
                            hoveredShape.setStrokeWidth(hoveredShape.getStrokeWidth()-2);
                        hoveredShape = closestShapes.get(0);
                        hoveredShape.setStrokeWidth(hoveredShape.getStrokeWidth()+2);
                    }
                } else if (e.wasRemoved()) {
                    if (closestShapes.size() >= 1){
                        if(closestShapes.size()>1)
                            closestShapes.sort((sh1,sh2)->{
                            double distance_1 = sh1 instanceof Liein ? ((Liein) sh1).distance_from_mouse.get(): ((Sirkale) sh1).mouseDistance.get();
                            double distance_2 = sh2 instanceof Liein ? ((Liein) sh2).distance_from_mouse.get(): ((Sirkale) sh2).mouseDistance.get();
                            return distance_1-distance_2<0? -1: (distance_1-distance_2==0? 0:1);
                            });
                        if (closestShapes.get(0)!= hoveredShape){
                            if(hoveredShape!=null)
                                hoveredShape.setStrokeWidth(hoveredShape.getStrokeWidth()-2);
                            hoveredShape = closestShapes.get(0);
                            hoveredShape.setStrokeWidth(hoveredShape.getStrokeWidth()+2);
                        }
                    }else{
                        hoveredShape.setStrokeWidth(hoveredShape.getStrokeWidth()-2);
                        hoveredShape = null;
                    }
                }
            }
        }));
        
        MAINSCREEN.setCenter(canvas);
        MAINSCREEN.setTop(topPane);
        MAINSCREEN.setBottom(bottomPane);
        MAINSCREEN.setBackground(Background.EMPTY);
        
        canvas.setOnMousePressed(e -> {
            dragCondition = 0;
            firstClick = focusedPointer==null? new Point2D(e.getX(), e.getY()): new Point2D(focusedPointer.getCenterX(), focusedPointer.getCenterY());
        });
        canvas.setOnMouseClicked(e->{//System.out.println("mouse clicked");
            if (dragCondition==0)
                toDraw = 0;
            if (hoveredShape!=null) 
                focusOnShape(hoveredShape);
        });
        canvas.setOnMouseMoved(e->{
            MOUSE_X.set(e.getX());
            MOUSE_Y.set(e.getY());
            
        });
        canvas.setOnDragDetected(e->{//System.out.println(canvas.getHeight()+"..."+canvas.getWidth());
            dragCondition = DRAGDETECTED;
            canvas.startFullDrag();
            if(toDraw!=0)
                bottomPane.getChildren().clear();
                
            if (toDraw==DRAWELINE) {
                Liein l = new Liein(firstClick.getX(),firstClick.getY(),e.getX(), e.getY());
                if(!Objects.equals(Liein.curType, 0))
                    l.setType(Liein.curType);
                MOUSE_X.addListener((c,o,n)-> {
                    if(l.isCloseTo((double)n, MOUSE_Y.get()))
                        closestShapes.add(l);
                    else if (closestShapes.indexOf(l)!=-1) 
                        closestShapes.remove(l);
                });
                MOUSE_Y.addListener((c,o,n)-> {
                    if(l.isCloseTo(MOUSE_X.get(), (double)n))
                        closestShapes.add(l);
                    else if (closestShapes.indexOf(l)!=-1) 
                        closestShapes.remove(l);
                });
                drawnShapes.add(l);
                focusOnShape(l);
            }
            else if ( toDraw == DRAWCIRCLE){
                Sirkale c = new Sirkale(firstClick.getX() ,firstClick.getY());
                MOUSE_X.addListener((cu,o,n)-> {
                    if(Xtra.inRange(c.getRadius()-3, c.getRadius()+3, c.mouseDistance.get()))
                        closestShapes.add(c);
                    else if (closestShapes.indexOf(c)!=-1) 
                        closestShapes.remove(c);
                });
                MOUSE_Y.addListener((cu,o,n)-> {
                    if(Xtra.inRange(c.getRadius()-3, c.getRadius()+3, c.mouseDistance.get()))
                        closestShapes.add(c);
                    else if (closestShapes.indexOf(c)!=-1) 
                        closestShapes.remove(c);
                });
                drawnShapes.add(c);
                focusOnShape(c);
            }
        });
        canvas.setOnMouseDragged(e-> {
            MOUSE_X.set(e.getX());
            MOUSE_Y.set(e.getY());
            if (dragCondition != DRAGDETECTED) return;
            if (toDraw==DRAWELINE){
                Liein l = (Liein) focusedShape;
                l.length.set(new Point2D(l.getStartX(), l.getStartY()).distance(e.getX(), e.getY()));
                l.angle.set(Math.toDegrees(PointPolar.angleBetween(l.getStartX(), l.getStartY(), e.getX(), e.getY())));
            }
            else if (toDraw == DRAWCIRCLE){
                Sirkale c = (Sirkale) focusedShape;
                c.setRadius(c.distanceFromCenter(e.getX(), e.getY()));
            }
        });
        canvas.setOnMouseDragReleased(e->{//System.out.println("rel");
            dragCondition = DRAGENDED;
            if(toDraw!=0){
                if(toDraw == DRAWELINE && focusedPointer!= null){/*if the mouse was released on a pointer*/
                    Liein l = (Liein) focusedShape;
                    l.length.set(new Point2D(l.getStartX(), l.getStartY()).distance(focusedPointer.getCenterX(), focusedPointer.getCenterY()));
                    l.angle.set(Math.toDegrees(PointPolar.angleBetween(l.getStartX(), l.getStartY(), focusedPointer.getCenterX(), focusedPointer.getCenterY())));
                }
                else if (toDraw == DRAWCIRCLE && focusedPointer!= null){
                    Sirkale c = (Sirkale) focusedShape;
                    c.setRadius(c.distanceFromCenter(focusedPointer.getCenterX(), focusedPointer.getCenterY()));
                }
                addPointer(focusedShape);/*after dragging ended, add pointers of the corresponding shape; adding pointers during dragging time 
                                          *** may cause unnecessary time lag  */
            }
        });
        
    }
    public void invalidateScreen(){//System.out.println("updated");
        canvas.getChildren().removeAll(screen_related_refreshable_nodes);
        for (int i=1; i*GRIDGAPE< canvas.getWidth(); i++){
            Line l = new Line(i*GRIDGAPE, 0, i*GRIDGAPE, canvas.getHeight());
            l.visibleProperty().bind(prop_grid.selectedProperty());
            screen_related_refreshable_nodes.add(l);
        }
        for (int i=1; i*GRIDGAPE<canvas.getHeight(); i++){
            Line l = new Line( 0,i*GRIDGAPE,canvas.getWidth() , i*GRIDGAPE);
            l.visibleProperty().bind(prop_grid.selectedProperty());
            screen_related_refreshable_nodes.add(l);
        }
        for (double i=0; i*GRIDGAPE< canvas.getWidth(); i++){
            for (double j=0; j*GRIDGAPE<canvas.getHeight(); j++){
                Pointer p = new Pointer(i*GRIDGAPE, j*GRIDGAPE);
                pointers.add(p);
            }
        }
            
        canvas.getChildren().addAll(screen_related_refreshable_nodes);
    }
    private void addPointer(Shape n){
        if (n instanceof Liein) {
            Liein l = (Liein) n ;
            if (!anyPointerIn(l.getStartX(), l.getStartY()))
                pointers.add(new Pointer(l, Pointer.START));
            if (!anyPointerIn(l.getMiddleX(), l.getMiddleY()))
                pointers.add(new Pointer(l, Pointer.MIDDLE));
            if (!anyPointerIn(l.getEndX(), l.getEndY()))
                pointers.add(new Pointer(l, Pointer.END));
        }else if (n instanceof Sirkale) {
            Sirkale c = (Sirkale) n;
            if (anyPointerIn(c.getCenterX(), c.getCenterY()))
                pointers.add(new Pointer((Sirkale) n));
        }
    }
    private boolean anyPointerIn(double x, double y){
        return pointers.stream().anyMatch((p) -> (p.getCenterX()==x && p.getCenterY()==y));
    }
    
    private<T extends Shape> void focusOnShape(T t){
        if(focusedShape!=null){
            focusedShape.strokeWidthProperty().unbindBidirectional(prop_stroke.getObservable());
            focusedShape.strokeProperty().unbind();
        }
            
        focusedShape =  t;
        prop_color.setValue((Color)focusedShape.getStroke());
        bottomPane.getChildren().clear();
        
        prop_stroke.setObservable(t.strokeWidthProperty());
        t.strokeProperty().bind(prop_color.valueProperty());
        bottomPane.getChildren().addAll(prop_stroke, prop_color);
        
        if (t instanceof Liein){
            Liein l = (Liein) focusedShape;
            if (lastFocusedLiein != null){
                lastFocusedLiein.length.unbindBidirectional(prop_length.getObservable());
                lastFocusedLiein.angle.unbindBidirectional(prop_angle.getObservable());
            }
            lastFocusedLiein = l;
            prop_length.setObservable(l.length);
            prop_angle.setObservable(l.angle);
            prop_linetype.setOnAction(e->{
                    switch (prop_linetype.getValue()) {
                    case "<-line":
                        l.setType(Liein.TYPE_START_ARROW);
                        break;
                    case "line->":
                        l.setType(Liein.TYPE_END_ARROW);
                        break;
                    case "<-line->":
                        l.setType(Liein.TYPE_TWO_ARROW);
                        break;
                    case "line":
                        l.setType(Liein.TYPE_NO_ARROW);
                        break;
                }
            });
            prop_bAnimation.setSelected(l.hasAnim);
            prop_bAnimation.setOnAction(e->{
                boolean b = prop_bAnimation.isSelected();
                l.hasAnim = b;
                if (!b){
                    if (btn_anim_toggle.isVisible())
                        btn_anim_toggle.setVisible(false);
                }else
                    prop_animType_liein.setValue(null);
            });
            bottomPane.getChildren().addAll(prop_length,prop_angle,prop_linetype,prop_bAnimation,prop_animType_liein,prop_anim_to_value,prop_anim_duration);
            
        } else if (t instanceof Sirkale){
            Sirkale c = (Sirkale) t;
            if (lastFocusedSirkale != null)
                lastFocusedSirkale.radiusProperty().unbindBidirectional(prop_radius.getObservable());
            lastFocusedSirkale = c;
            prop_radius.setObservable(c.radiusProperty());
            bottomPane.getChildren().add(prop_radius);
        }
    }
    public void deleteFocusedNode(){
        drawnShapes.remove(focusedShape);
        bottomPane.getChildren().clear();
        if (focusedShape instanceof Sirkale){
            Sirkale c = (Sirkale) focusedShape;
            pointers.removeIf(p-> p.getCenterX()==c.getCenterX() && p.getCenterY()==c.getCenterY());
        }
            
        else if (focusedShape instanceof Liein){
            Liein l = (Liein) focusedShape;
            pointers.removeIf(p-> (p.getCenterX()==l.getStartX() && p.getCenterY()==l.getStartY()) ||
                                  (p.getCenterX()==l.getEndX() && p.getCenterY()==l.getEndY()) ||
                                  (p.getCenterX()==l.getMiddleX() && p.getCenterY()==l.getMiddleY()));
        }
    }
}
/*
*** Pointers will not be added while the mouse is being dragged. After finishing the mouse dragged event. 
*** Pointers will be added  at setOnMouseDragReleased event
***
*/