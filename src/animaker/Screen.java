
package animaker;

import graphics.Liein;
import graphics.Pointer;
import graphics.Sirkale;
import graphics.WindowsButton;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public class Screen {
    public final AnchorPane MAINSCREEN = new AnchorPane(),canvas = new AnchorPane();
    private final HBox topPane = new HBox(1), propPane = new HBox(1), infoPane = new HBox();
    private final WindowsButton btn_circle = new WindowsButton(new Sirkale(8));
    private final WindowsButton btn_line = new WindowsButton(new Liein(0, 0, 12, 12));
    private final HBox itemPane = new HBox(btn_line, btn_circle);
    private final WindowsButton btn_anim_toggle = new WindowsButton("Play");
    private final WindowsButton btn_anim_sequential = new WindowsButton("Sequential");
    private final WindowsButton btn_anim_parallel = new WindowsButton("Parallel");
    private final HBox multiple_anim_trgger = new HBox(btn_anim_sequential, btn_anim_parallel);
    private final CheckBox prop_grid = new CheckBox("Show gridlines");
    private final Property prop_stroke = new Property("Stroke: ", Property.SIZE_SMALL);
    private final ColorPicker prop_color = new ColorPicker(Color.GREEN);
    private final ComboBox<String> prop_animType_liein = new ComboBox(FXCollections.observableArrayList("startX","startY","endX","endY","length","angle"));
    private final ComboBox<String> prop_animType_sirkale = new ComboBox(FXCollections.observableArrayList("Radius", "centerX", "centerY"));
    private final ComboBox<String> prop_animType_multiple = new ComboBox(FXCollections.observableArrayList("Sequential", "Simultaneous"));
    private final ComboBox<String> prop_linetype = new ComboBox(FXCollections.observableArrayList("line", "<-line","line->","<-line->"));
    private final ListView<Anymation> anim_list = new ListView();
    private final AnchorPane anim_list_holder_primary = new AnchorPane(anim_list);
    private final TitledPane anim_list_grand_holder = new TitledPane("List of all animations", anim_list_holder_primary);
    private Liein lastFocusedLiein;
    private Sirkale lastFocusedSirkale;
    private Shape focusedShape, hoveredShape;
    
    private final Property prop_length = new Property("Length: ", Property.SIZE_BIG);
    private final Property prop_angle = new Property("Angle: ", Property.SIZE_BIG);
    private final Property prop_startX = new Property("startX: ", Property.SIZE_MEDIUM);
    private final Property prop_startY = new Property("startY: ", Property.SIZE_MEDIUM);
    private final Property prop_radius = new Property("Radius: ", Property.SIZE_BIG);
    private final Property  prop_anim_to_value = new Property("To: ", Property.SIZE_MEDIUM);
    private final Property prop_anim_duration = new Property("Duration: ", Property.SIZE_SMALL);
    
    private final ObservableList<Pointer> pointers = FXCollections.observableArrayList();
    private final ObservableList<Pointer> closestPointers = FXCollections.observableArrayList();
    private final ObservableList<Shape> closestShapes = FXCollections.observableArrayList();
    private final ObservableList<Shape> screen_related_refreshable_nodes = FXCollections.observableArrayList();
    private final ObservableList<Shape> drawnShapes = FXCollections.observableArrayList();
    private Anymation master_animation ;
    
    private final int  DRAWCIRCLE=1, DRAWELINE=2, DRAGDETECTED=3, DRAGENDED=4, GRIDGAPE=44;
    public static final DoubleProperty MOUSE_X = new SimpleDoubleProperty(), MOUSE_Y = new SimpleDoubleProperty();
    private int toDraw=0,  dragCondition=0 ;
    private Boolean anim_list_is_ctrl_pressed = false;
    private Pointer focusedPointer;
    private Point2D firstClick ;
    private Color focusedShapeStroke = Color.GREEN;
    
    
    private ChangeListener<Boolean> anim_active_listener = (c,o,n)->{
        if(n){
            if(propPane.getChildren().indexOf(btn_anim_toggle) == -1)
                propPane.getChildren().add(btn_anim_toggle);
        } else{
            if(propPane.getChildren().indexOf(btn_anim_toggle) != -1)
                propPane.getChildren().remove(btn_anim_toggle);
        }
    };
    
    public Screen() {
        btn_line.setOnMouseClicked(e->{
            unfocus();
            toDraw=DRAWELINE;
            itemPane.getChildren().remove(btn_line);
            if(itemPane.getChildren().indexOf(btn_circle)==-1)
                    itemPane.getChildren().add(btn_circle);
            propPane.getChildren().clear();
            propPane.getChildren().add(prop_linetype);
        });
        btn_circle.setOnMouseClicked(e->{
            unfocus();
            toDraw = DRAWCIRCLE;
            itemPane.getChildren().remove(btn_circle);
            if(itemPane.getChildren().indexOf(btn_line)==-1)
                    itemPane.getChildren().add(0, btn_line);
            propPane.getChildren().clear();
        });
        btn_anim_sequential.setOnMouseClicked(e->{
            ObservableList<Anymation> ol = FXCollections.observableArrayList(anim_list.getSelectionModel().getSelectedItems());
            int i = anim_list.getItems().indexOf(ol.get(0));
            anim_list.getItems().removeAll(ol);
            master_animation = new SequentialAnymation(ol);
            anim_list.getItems().add(i, master_animation);
        });
        btn_anim_parallel.setOnMouseClicked(e->{
            ObservableList<Anymation> ol = FXCollections.observableArrayList(anim_list.getSelectionModel().getSelectedItems());
            int i = anim_list.getItems().indexOf(ol.get(0));
            anim_list.getItems().removeAll(ol);
            master_animation = new ParallelAnymation(ol);
            anim_list.getItems().add(i, master_animation);
        });
        prop_anim_to_value.getTextProperty().addListener((c,o,n)->{
            ((SingleAnimation)master_animation).setEndValue(n);
            updateItemInListView(master_animation);
        });
        prop_anim_duration.getTextProperty().addListener((c,o,n)->{
            ((SingleAnimation)master_animation).setDurationInSecond(n);
            updateItemInListView(master_animation);
        });
        prop_animType_liein.setOnAction(e->{
            String s = prop_animType_liein.getValue();
            if (s!=null){
                switch (s){
                    case "startX" : 
                        master_animation = new SingleAnimation(lastFocusedLiein.startX);
                        break;
                    case "startY" : 
                        master_animation = new SingleAnimation(lastFocusedLiein.startY);
                        break;
                    case "endX" : 
                        master_animation = new SingleAnimation(lastFocusedLiein.endX);
                        break;
                    case "endY" : 
                        master_animation = new SingleAnimation(lastFocusedLiein.endY);
                        break;
                    case "angle":
                        lastFocusedLiein.isolate = true;
                        master_animation = new SingleAnimation(lastFocusedLiein.gradient);
                        master_animation.activeProperty().addListener((c,o,n)->{
                            lastFocusedLiein.isolate = n;
                        });
                        break;
                    case "length":
                        lastFocusedLiein.isolate = true;
                        master_animation = new SingleAnimation(lastFocusedLiein.length);
                        master_animation.activeProperty().addListener((c,o,n)-> {
                            lastFocusedLiein.isolate = n;
                        });
                        break;
                }
                ((SingleAnimation)master_animation).setDescription(s);
                anim_list.getItems().add(master_animation);
                master_animation.setPlayButton(btn_anim_toggle);
                master_animation.activeProperty().addListener(anim_active_listener);
                if (propPane.getChildren().indexOf(prop_anim_to_value) == -1){
                    propPane.getChildren().addAll(prop_anim_to_value,prop_anim_duration);
                    prop_anim_to_value.setText("1");
                    prop_anim_duration.setText("3");
                }
                if (propPane.getChildren().indexOf(btn_anim_toggle) == -1)
                    propPane.getChildren().add(btn_anim_toggle);
            }
        });
        prop_animType_sirkale.setOnAction(e->{
            String s = prop_animType_sirkale.getValue();
            if (s!=null){
                switch (s){
                    case "Radius":
                        master_animation = new SingleAnimation(lastFocusedSirkale.radiusProperty());
                        break;
                    case "centerX":
                        master_animation = new SingleAnimation(lastFocusedSirkale.centerXProperty());
                        break;
                    case "centerY":
                        master_animation = new SingleAnimation(lastFocusedSirkale.centerYProperty());
                        break;
                }
                ((SingleAnimation)master_animation).setDescription(s);
                anim_list.getItems().add(master_animation);
                master_animation.setPlayButton(btn_anim_toggle);
                master_animation.activeProperty().addListener(anim_active_listener);
                if (propPane.getChildren().indexOf(prop_anim_to_value) == -1){
                    propPane.getChildren().addAll(prop_anim_to_value,prop_anim_duration);
                    prop_anim_to_value.setText("1");
                    prop_anim_duration.setText("3");
                }
                if (propPane.getChildren().indexOf(btn_anim_toggle) == -1)
                    propPane.getChildren().add(btn_anim_toggle);
            }
        });
        prop_linetype.setOnAction(e->{
            Boolean b = focusedShape!=null;
            switch (prop_linetype.getValue()) {
                case "<-line":
                    if(b)
                        lastFocusedLiein.setType(Liein.TYPE_START_ARROW);
                    Liein.curType = Liein.TYPE_START_ARROW;
                    break;
                case "line->":
                    if(b)
                        lastFocusedLiein.setType(Liein.TYPE_END_ARROW);
                    Liein.curType = Liein.TYPE_END_ARROW;
                    break;
                case "<-line->":
                    if(b)
                        lastFocusedLiein.setType(Liein.TYPE_TWO_ARROW);
                    Liein.curType = Liein.TYPE_TWO_ARROW;
                    break;
                case "line":
                    if(b)
                        lastFocusedLiein.setType(Liein.TYPE_NO_ARROW);
                    Liein.curType = Liein.TYPE_NO_ARROW;
                    break;
            }
        });
        prop_color.setOnAction(e->{
            focusedShape.setStroke(prop_color.getValue());
            focusedShapeStroke = prop_color.getValue();
        });
        prop_angle.getFocusProperty().addListener((c,o,n)->lastFocusedLiein.isolate = n);
        prop_length.getFocusProperty().addListener((c,o,n)-> lastFocusedLiein.isolate = n);
        anim_list.getFocusModel().focusedIndexProperty().addListener((c,o,n)->{
            MultipleSelectionModel sm = anim_list.getSelectionModel();
            if(sm.getSelectedItems().size()==1){
                master_animation = (Anymation)sm.getSelectedItem();
            }
            if (anim_list_is_ctrl_pressed){
                
                if (!sm.isSelected((int)n)) {
                    sm.select((int)n);
                }else if(sm.isSelected((int)n) && sm.isSelected((int)o))
                        sm.clearSelection((int)o);
                if ( sm.getSelectedItems().size() >1){
                    if(anim_list_holder_primary.getChildren().indexOf(multiple_anim_trgger)==-1)
                        anim_list_holder_primary.getChildren().add(multiple_anim_trgger);
                }else if(anim_list_holder_primary.getChildren().indexOf(multiple_anim_trgger)!=-1)
                        anim_list_holder_primary.getChildren().remove(multiple_anim_trgger);
            }
        });
        anim_list.setOnKeyPressed(e->{
                switch (e.getCode()) {
                case CONTROL:
                    anim_list_is_ctrl_pressed = true;
                    break;
                case DELETE:
                    anim_list.getItems().removeAll(anim_list.getSelectionModel().getSelectedItems());
                    e.consume();
                    break;
                case ENTER:
                    master_animation.play();
                    break;
            }
        });
        anim_list.setOnKeyReleased(e->{
            if (e.getCode() == KeyCode.CONTROL)
                anim_list_is_ctrl_pressed = false;
        });
        MAINSCREEN.getChildren().addAll(canvas, topPane);
        AnchorPane.setTopAnchor(topPane, 0.0);
        AnchorPane.setLeftAnchor(topPane, 0.0);
        AnchorPane.setRightAnchor(topPane, 0.0);
        AnchorPane.setTopAnchor(canvas, 25.0);
        AnchorPane.setTopAnchor(anim_list_grand_holder, 0.0);
        AnchorPane.setRightAnchor(anim_list_grand_holder, 0.0);
        AnchorPane.setLeftAnchor(anim_list, 0.0);
        AnchorPane.setRightAnchor(anim_list, 0.0);
        AnchorPane.setBottomAnchor(anim_list, 25.0);
        AnchorPane.setBottomAnchor(multiple_anim_trgger, 0.0);
        AnchorPane.setRightAnchor(multiple_anim_trgger, 0.0);
        AnchorPane.setLeftAnchor(multiple_anim_trgger, 0.0);
        anim_list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        anim_list.setPrefWidth(200);
        infoPane.setPrefWidth(50);
        multiple_anim_trgger.setAlignment(Pos.CENTER);
        topPane.setId("hbox");
        propPane.setId("hbox");
        canvas.setId("anchor-pane");
        propPane.setMinHeight(25);
        prop_grid.setSelected(true);
        prop_linetype.setMaxWidth(80);
        prop_linetype.setPromptText("Type:");
        topPane.getChildren().addAll(itemPane,prop_grid,infoPane,propPane);
        prop_animType_liein.setPromptText("Animtype:");
        prop_animType_sirkale.setPromptText("Animtype:");
        prop_animType_multiple.setPromptText("Type:");
        
        anim_list.getItems().addListener((ListChangeListener<Anymation>)(e->{
            while (e.next()) {                
                if(e.wasAdded()){
                    if (anim_list.getItems().size()==1){
                        MAINSCREEN.getChildren().add(anim_list_grand_holder);
                    }
                }else if (e.wasRemoved()){
                    if (anim_list.getItems().isEmpty()){
                        MAINSCREEN.getChildren().remove(anim_list_grand_holder);
                    }
                }
                if(anim_list.getItems().size()<=1)
                    if (anim_list_holder_primary.getChildren().indexOf(multiple_anim_trgger) != -1)
                        anim_list_holder_primary.getChildren().remove(multiple_anim_trgger);
            }
        }));
        drawnShapes.addListener((ListChangeListener<Shape>)(e->{
            while (e.next()) {                
                if(e.wasAdded()){
                    canvas.getChildren().add(e.getAddedSubList().get(0));
                }else if (e.wasRemoved()){
                    canvas.getChildren().remove(e.getRemoved().get(0));
                }
            }
        }));
        pointers.addListener((ListChangeListener<Pointer>)(e->{
            while (e.next()) {
                if(e.wasAdded()){
                    e.getAddedSubList().forEach(p->{
                        if(allPointerIn(p.getCenterX(), p.getCenterY()).size()>1)
                            p.setActive(false);
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
                    });
                }else if (e.wasRemoved()) {
                    e.getRemoved().forEach(p->{
                        p.setActive(false); /*this line is needed because listeners will still do their job*/
                        canvas.getChildren().remove(p);

                        List<Pointer> pointersInSamePosition = allPointerIn(p.getCenterX(), p.getCenterY());
                        if(pointersInSamePosition.size()>0)
                            pointersInSamePosition.get(0).setActive(true);
                    });
                    
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
        for (int i=1; i*GRIDGAPE< 1370; i++){
            Line l = new Line(i*GRIDGAPE, 0, i*GRIDGAPE, 695);
            l.visibleProperty().bind(prop_grid.selectedProperty());
            screen_related_refreshable_nodes.add(l);
        }
        for (int i=1; i*GRIDGAPE<695; i++){
            Line l = new Line( 0,i*GRIDGAPE,1370 , i*GRIDGAPE);
            l.visibleProperty().bind(prop_grid.selectedProperty());
            screen_related_refreshable_nodes.add(l);
        }
        for (double i=0; i*GRIDGAPE< 1370; i++){
            for (double j=0; j*GRIDGAPE<695; j++){
                Pointer p = new Pointer(i*GRIDGAPE, j*GRIDGAPE);
                pointers.add(p);
            }
        }
        canvas.getChildren().addAll(screen_related_refreshable_nodes);
        canvas.setOnMousePressed(e -> {
            dragCondition = 0;
            firstClick = focusedPointer==null? new Point2D(e.getX(), e.getY()): new Point2D(focusedPointer.getCenterX(), focusedPointer.getCenterY());
        });
        canvas.setOnMouseClicked(e->{//System.out.println("mouse clicked");
            if(anim_list_grand_holder.isExpanded())
                anim_list_grand_holder.setExpanded(false);
            if (hoveredShape!=null) {
                if (dragCondition == 0)
                    focusOnShape(hoveredShape);
            } else{
                toDraw = 0;
                if(itemPane.getChildren().indexOf(btn_line)==-1)
                    itemPane.getChildren().add(0, btn_line);
                else if(itemPane.getChildren().indexOf(btn_circle)==-1)
                    itemPane.getChildren().add( btn_circle);
                unfocus();
            }
        });
        canvas.setOnMouseMoved(e->{
            MOUSE_X.set(e.getX());
            MOUSE_Y.set(e.getY());
            
        });
        canvas.setOnDragDetected(e->{//System.out.println(canvas.getHeight()+"..."+canvas.getWidth());
            dragCondition = DRAGDETECTED;
            canvas.startFullDrag();
            if(toDraw!=0)
                propPane.getChildren().clear();
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
            }else if ( toDraw == DRAWCIRCLE){
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
                lastFocusedLiein.setEnd(e.getX(), e.getY());
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
                    lastFocusedLiein.setEnd(focusedPointer.getCenterX(), focusedPointer.getCenterY());
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
    private void updateItemInListView(Anymation anmtn){
        int index = anim_list.getItems().indexOf(anmtn);
        if(index !=-1){
            anim_list.getItems().remove(anmtn);
            anim_list.getItems().add(index, anmtn);
        }
    }
    public void playNext(){
        if(MAINSCREEN.getChildren().indexOf(anim_list_grand_holder) == -1)
            return;
        MultipleSelectionModel msm = anim_list.getSelectionModel();
        if(msm.getSelectedIndex()==anim_list.getItems().size()-1)
            return;
        if(msm.getSelectedIndex()==-1){
            msm.select(0);
            
        }else{
            msm.clearAndSelect(msm.getSelectedIndex()+1);
        }
        ((Anymation)msm.getSelectedItem()).play();
    }
    private void addPointer(Shape n){
        if (n instanceof Liein) {
            Liein l = (Liein) n ;
            pointers.addAll(new Pointer(l, Pointer.START), new Pointer(l, Pointer.MIDDLE), new Pointer(l, Pointer.END));
        }else if (n instanceof Sirkale) {
            Sirkale c = (Sirkale) n;
            pointers.add(new Pointer((Sirkale) n));
        }
    }
    private List<Pointer> allPointerIn(double x, double y){
        return pointers.stream().filter((p) -> (p.getCenterX()==x && p.getCenterY()==y)).collect(Collectors.toList());
    }
    
    private<T extends Shape> void focusOnShape(T t){
        if(focusedShape == t)
            return;
        unfocus();
        focusedShape =  t;
        focusedShapeStroke =(Color) focusedShape.getStroke();
        focusedShape.setStroke(Color.WHITE);
        prop_color.setValue((Color) focusedShape.getStroke());
        prop_stroke.setObservable(t.strokeWidthProperty());
        propPane.getChildren().addAll(prop_stroke, prop_color);
        
        if (t instanceof Liein){
            Liein l = (Liein) focusedShape;
            lastFocusedLiein = l;
            prop_length.setObservable(l.length);
            prop_angle.setObservable(l.gradient);
            prop_startX.setObservable(l.startX);
            prop_startY.setObservable(l.startY);
            prop_linetype.setValue(l.type.get());
            prop_animType_liein.setValue(null);
            propPane.getChildren()
                .addAll(FXCollections.observableArrayList(prop_length,prop_angle,prop_startX,prop_startY,prop_linetype,prop_animType_liein));
            
        } else if (t instanceof Sirkale){
            Sirkale c = (Sirkale) t;
            lastFocusedSirkale = c;
            prop_radius.setObservable(c.radiusProperty());
            prop_animType_sirkale.setValue(null);
            propPane.getChildren().addAll(prop_radius, prop_animType_sirkale);
        }
    }
    public void unfocus(){
        if(focusedShape!=null){
            focusedShape.strokeWidthProperty().unbindBidirectional(prop_stroke.getObservable());
            focusedShape.strokeProperty().unbind();
            focusedShape.setStroke(focusedShapeStroke);
            if (focusedShape instanceof Liein) {
                lastFocusedLiein.length.unbindBidirectional(prop_length.getObservable());
                lastFocusedLiein.gradient.unbindBidirectional(prop_angle.getObservable());
                lastFocusedLiein.startX.unbindBidirectional(prop_startX.getObservable());
                lastFocusedLiein.startY.unbindBidirectional(prop_startY.getObservable());
            } else if (focusedShape instanceof Sirkale){
                lastFocusedSirkale.radiusProperty().unbindBidirectional(prop_radius.getObservable());
            }
            propPane.getChildren().clear();
            focusedShape = null;
        }
    }
    public void deleteFocusedNode(){
        if (focusedShape instanceof Sirkale){
            pointers.remove(allPointerIn(lastFocusedSirkale.getCenterX(), lastFocusedSirkale.getCenterY()).get(0));
        }else if (focusedShape instanceof Liein){
            pointers.removeAll(allPointerIn(lastFocusedLiein.getStartX(), lastFocusedLiein.getStartY()).get(0),
                                allPointerIn(lastFocusedLiein.getMiddleX(), lastFocusedLiein.getMiddleY()).get(0),
                                allPointerIn(lastFocusedLiein.getEndX(), lastFocusedLiein.getEndY()).get(0));
        }
        drawnShapes.remove(focusedShape);
        propPane.getChildren().clear();
    }
}
/*
*** Pointers will not be added while the mouse is being dragged. After finishing the mouse dragged event. 
*** Pointers will be added  at setOnMouseDragReleased event
***
*/