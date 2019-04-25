
package animaker;

import graphics.shape.Pointer;
import graphics.shape.Point;
import graphics.shape.Liein;
import graphics.shape.Sirkale;
import graphics.control.WindowsButton;
import graphics.Anymation.SingleAnimation;
import graphics.Anymation.Anymation;
import graphics.Anymation.SequentialAnymation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.shape.VLineTo;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javax.imageio.ImageIO;
/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public class AppScreen {
    
    private final HBox animPane = new HBox(),propPane = new HBox();
    private final WindowsButton btn_anim_sequential = new WindowsButton("Sequential");
    private final WindowsButton btn_anim_parallel = new WindowsButton("Parallel");
    private final WindowsButton btn_anim_edit = new WindowsButton("Edit");
    private final WindowsButton btn_anim_selectnone = new WindowsButton("Clear Selection");
    private final HBox multiple_anim_trgger = new HBox(btn_anim_selectnone);
    private final CheckBox prop_grid = new CheckBox("Show gridlines");
    private final WindowsButton btn_circle = new WindowsButton(new Sirkale(8));
    private final WindowsButton btn_line = new WindowsButton(new Liein(0, 0, 12, 12));
    private final WindowsButton btn_vector_field = new WindowsButton("vect");
    private final WindowsButton btn_snap = new WindowsButton(new Path(new MoveTo(4,0), new HLineTo(11), new VLineTo(4),new HLineTo(15), 
                                            new VLineTo(15), new HLineTo(0), new VLineTo(4), new HLineTo(4), new ClosePath(), new MoveTo(5,10),
                                            new ArcTo(2.5,2.5, 0,10, 10, true, false),new ArcTo(2.5,2.5, 0, 5, 10, true, false)));
    private final WindowsButton btn_array = new WindowsButton(new Path(new MoveTo(2, 2), new LineTo(8, 8), new MoveTo(10, 2), new LineTo(16, 8),
                                                                new MoveTo(2, 10), new LineTo(8, 16), new MoveTo(10, 10), new LineTo(16, 16)));
    private final WindowsButton btn_bind_property = new WindowsButton("Bind");
    private final HBox itemPane = new HBox(prop_grid,btn_line, btn_circle,btn_vector_field,btn_array, btn_snap);
    private final ColorPicker prop_color = new ColorPicker(Color.GREEN);
    private final ColorPicker prop_fill = new ColorPicker(Color.TRANSPARENT);
    private final Label MOUSE_X_SHOWCASE = new Label("null"),separator = new Label(" , "), MOUSE_Y_SHOWCASE = new Label("null");
    private final ListView<Anymation> anim_list = new ListView();
    private final AnchorPane anim_list_holder_primary = new AnchorPane(anim_list, multiple_anim_trgger);
    private final TitledPane anim_list_grand_holder = new TitledPane("List of all animations", anim_list_holder_primary);
    private final HBox infoPane = new HBox(MOUSE_X_SHOWCASE,separator,  MOUSE_Y_SHOWCASE, anim_list_grand_holder);
    private final HBox topPane = new HBox(10,itemPane ,propPane,  animPane);
    public final AnchorPane canvas = new AnchorPane(),MAINSCREEN = new AnchorPane(canvas, topPane, infoPane);
    public final Double PREFWIDTH=Screen.getPrimary().getBounds().getWidth(), PREFHEIGHT=Screen.getPrimary().getBounds().getHeight();
    public final Scene math_scene = new Scene(MAINSCREEN, PREFWIDTH, PREFHEIGHT);
    
    private final ComboBox<String> prop_animType_liein = new ComboBox(FXCollections.observableArrayList("start","startX","startY","end","endX","endY","length","angle"));
    private final ComboBox<String> prop_animType_sirkale = new ComboBox(FXCollections.observableArrayList("Radius","center", "centerX", "centerY"));
    private final ComboBox<String> prop_linetype = new ComboBox(FXCollections.observableArrayList("line", "<-line","line->","<-line->"));
    
    private final Property prop_length = new Property("Length: ", Property.SIZE_BIG);
    private final Property prop_angle = new Property("Angle: ", Property.SIZE_BIG);
    private final Property prop_startX = new Property("startX: ", Property.SIZE_MEDIUM);
    private final Property prop_startY = new Property("startY: ", Property.SIZE_MEDIUM);
    private final Property prop_radius = new Property("Radius: ", Property.SIZE_BIG);
    private final Property prop_centerX = new Property("centerX: ", Property.SIZE_BIG);
    private final Property prop_centerY = new Property("centerY: ", Property.SIZE_BIG);
    private final Property  prop_anim_to_value = new Property("To: ", Property.SIZE_MEDIUM);
    private final Property prop_anim_duration = new Property("Duration: ", Property.SIZE_SMALL);
    
    private final ObservableList<Node> lieinRelatedProps = FXCollections.observableArrayList(prop_color, prop_length,prop_angle, prop_startX,prop_startY);
    private final ObservableList<Node> sirkaleRelatedProps = FXCollections.observableArrayList(prop_color,prop_fill,prop_radius, prop_centerX, prop_centerY);
    private final ObservableList<Pointer> pointers = FXCollections.observableArrayList();
    private final ObservableList<Pointer> closestPointers = FXCollections.observableArrayList();
    private final ObservableList<Shape> closestShapes = FXCollections.observableArrayList();
    public final ObservableList<Point> points = FXCollections.observableArrayList();
    private final List<Shape> grides =new ArrayList() ;
    private final List<Line> vertical_grides = new ArrayList(), horizontal_grides = new ArrayList();
    private final ObservableList<Shape> drawnShapes = FXCollections.observableArrayList();
    private final ObservableList<DoubleProperty> selectedProperties = FXCollections.observableArrayList();
    private final int  DRAWCIRCLE=1, DRAWELINE=2, DRAWPOINT=3, DRAGDETECTED=3, DRAGENDED=4, GRIDGAPE=44, MODE_SELECT_PROPERTY=5, MODE_SELECT_POINT=6;
    
    public static final DoubleProperty MOUSE_X = new SimpleDoubleProperty(), MOUSE_Y = new SimpleDoubleProperty();
    private final MultipleSelectionModel sm = anim_list.getSelectionModel();
    private final Liein X_AXIS = new Liein( 0,(PREFHEIGHT-25)/2, PREFWIDTH-10, (PREFHEIGHT-25)/2);
    private final Liein Y_AXIS = new Liein(PREFWIDTH/2, 0, PREFWIDTH/2, PREFHEIGHT-75);
    private Liein lastFocusedLiein;
    private Sirkale lastFocusedSirkale;
    private Shape focusedShape, hoveredShape;
    private Anymation master_animation ;
    private int toDraw=0,  dragCondition=0 , mode =0;
    public Boolean is_ctrl_pressed = false;
    private Pointer focusedPointer;
    private Point2D firstClick ;
    private Color focusedShapeStroke = Color.GREEN;
    
    public AppScreen() {
        btn_line.setOnMouseClicked(e->{
            if(focusedShape instanceof Sirkale){
                propPane.getChildren().removeAll(sirkaleRelatedProps);
                animPane.getChildren().clear();
            }
            toDraw=DRAWELINE;
            itemPane.getChildren().remove(btn_line);
            Xtra.addIfDoesnotExist(itemPane.getChildren(), btn_circle);
            Xtra.addIfDoesnotExist(propPane.getChildren(), prop_linetype);
        });
        btn_circle.setOnMouseClicked(e->{
            if(focusedShape instanceof Liein){
                propPane.getChildren().removeAll(lieinRelatedProps);
                propPane.getChildren().remove(prop_linetype);
                animPane.getChildren().clear();
            }
            toDraw = DRAWCIRCLE;
            itemPane.getChildren().remove(btn_circle);
            if(itemPane.getChildren().indexOf(btn_line)==-1)
                    itemPane.getChildren().add(1, btn_line);
        });
        btn_snap.setOnMouseClicked(f->{
            try {
                FileChooser fc = new FileChooser();
                fc.setSelectedExtensionFilter(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
                ImageIO.write(SwingFXUtils.fromFXImage(canvas.snapshot(new SnapshotParameters(), null), null), "png", fc.showSaveDialog(null));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        btn_bind_property.setOnMouseClicked(e->{
            selectedProperties.clear();
            if(mode == 0){
                mode= MODE_SELECT_PROPERTY;
                btn_bind_property.setText("Abort binding");
            }else{
                mode= 0;
                btn_bind_property.setText("bind");
            }
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
        btn_anim_selectnone.setOnMouseClicked(e->{
            if(sm.getSelectedItems().size()>0){
                sm.clearAndSelect(-1);
                animPane.getChildren().removeAll(prop_anim_duration,prop_anim_to_value);
            }
        });
        prop_anim_to_value.getTextProperty().addListener((c,o,n)->{
            ((SingleAnimation)master_animation).setEndValue(n);
            updateItemInListView(master_animation);
        });
        prop_anim_duration.getTextProperty().addListener((c,o,n)->{
            master_animation.setDurationInSecond(n);
            updateItemInListView(master_animation);
        });
        prop_animType_liein.setOnAction(e->{
            String s = prop_animType_liein.getValue();
            if (s!=null){
                switch (s){
                    case "startX" : 
                        if(mode==0)
                            master_animation = new SingleAnimation(lastFocusedLiein.startX);
                        else if(selectedProperties.size()<2)
                            selectedProperties.add(lastFocusedLiein.startX);
                        break;
                    case "startY" : 
                        if(mode==0)
                            master_animation = new SingleAnimation(lastFocusedLiein.startY);
                        else if(selectedProperties.size()<2)
                            selectedProperties.add(lastFocusedLiein.startY);
                        break;
                    case "endX" : 
                        if(mode==0)
                            master_animation = new SingleAnimation(lastFocusedLiein.endX);
                        else if(selectedProperties.size()<2)
                            selectedProperties.add(lastFocusedLiein.endX);
                        break;
                    case "endY" : 
                        if(mode==0)
                            master_animation = new SingleAnimation(lastFocusedLiein.endY);
                        else if(selectedProperties.size()<2)
                            selectedProperties.add(lastFocusedLiein.endY);
                        break;
                    case "angle":
                        if(mode==0){
                            master_animation = new SingleAnimation(lastFocusedLiein.gradient);
                        }else if(selectedProperties.size()<2)
                            selectedProperties.add(lastFocusedLiein.gradient);
                        break;
                    case "length":
                        if(mode==0){
                            master_animation = new SingleAnimation(lastFocusedLiein.length);
                        }else if(selectedProperties.size()<2)
                            selectedProperties.add(lastFocusedLiein.length);
                        break;
                    case "start":
                        mode = MODE_SELECT_POINT;
                        master_animation = new ParallelAnymation(new SingleAnimation(lastFocusedLiein.startX),
                                                                 new SingleAnimation(lastFocusedLiein.startY));
                        break;
                    case "end" : 
                        mode = MODE_SELECT_POINT;
                        master_animation = new ParallelAnymation(new SingleAnimation(lastFocusedLiein.endX),
                                                                 new SingleAnimation(lastFocusedLiein.endY));
                        break;
                }
                if(mode!= MODE_SELECT_PROPERTY){
                    master_animation.setDescription(s);
                    anim_list.getItems().add(master_animation);
                    if (animPane.getChildren().indexOf(prop_anim_to_value) == -1){
                        if (mode == 0) {
                            animPane.getChildren().add(prop_anim_to_value);
                            prop_anim_to_value.setText("1");
                        }
                        if(Xtra.addIfDoesnotExist(animPane.getChildren(), prop_anim_duration))
                            prop_anim_duration.setText("3");
                    }
                }
                
            }
            if (mode == MODE_SELECT_PROPERTY && selectedProperties.size()==2)
                mode=0;
        });
        prop_animType_sirkale.setOnAction(e->{
            String s = prop_animType_sirkale.getValue();
            if (s!=null){
                switch (s){
                    case "Radius":
                        if(mode==0)
                            master_animation = new SingleAnimation(lastFocusedSirkale.radiusProperty());
                        else if(selectedProperties.size()<2)
                            selectedProperties.add(lastFocusedSirkale.radiusProperty());
                        break;
                    case "centerX":
                        if(mode==0)
                            master_animation = new SingleAnimation(lastFocusedSirkale.centerXProperty());
                        else if(selectedProperties.size()<2)
                            selectedProperties.add(lastFocusedSirkale.centerXProperty());
                        break;
                    case "centerY":
                        if(mode==0)
                            master_animation = new SingleAnimation(lastFocusedSirkale.centerYProperty());
                        else if(selectedProperties.size()<2)
                            selectedProperties.add(lastFocusedSirkale.centerYProperty());
                        break;
                    case "center": 
                        mode = MODE_SELECT_POINT;
                        master_animation = new ParallelAnymation(new SingleAnimation(lastFocusedSirkale.centerXProperty()),
                                                                 new SingleAnimation(lastFocusedSirkale.centerYProperty()));
                        break;
                }
                if(mode != MODE_SELECT_PROPERTY){
                    master_animation.setDescription(s);
                    anim_list.getItems().add(master_animation);
                    if (animPane.getChildren().indexOf(prop_anim_to_value) == -1){
                        if (mode == 0) {
                            animPane.getChildren().add(prop_anim_to_value);
                            prop_anim_to_value.setText("1");
                        }
                        if(Xtra.addIfDoesnotExist(animPane.getChildren(), prop_anim_duration))
                            prop_anim_duration.setText("3");
                    }
                }
            }
        });
        prop_linetype.setOnAction(e->{
            Boolean b = lastFocusedLiein!=null;
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
            master_animation = (Anymation)sm.getSelectedItem();
            
            if (is_ctrl_pressed)
                sm.select((int)n);
            if(sm.getSelectedItems().size()==1){
                Xtra.addIfDoesnotExist(multiple_anim_trgger.getChildren(), btn_anim_edit);
                Xtra.addIfDoesnotExist(animPane.getChildren(), prop_anim_duration);
                if(sm.getSelectedItem() instanceof SingleAnimation)
                    Xtra.addIfDoesnotExist(animPane.getChildren(), prop_anim_to_value);
            }
            if(sm.getSelectedItems().size()<=1)
                multiple_anim_trgger.getChildren().removeAll(btn_anim_parallel, btn_anim_sequential);
            else{
                multiple_anim_trgger.getChildren().remove(btn_anim_edit);
                Xtra.addIfDoesnotExist(multiple_anim_trgger.getChildren(), btn_anim_parallel , btn_anim_sequential);
            }
        });
        
        anim_list.setOnKeyPressed(e->{
            switch (e.getCode()) {
                case DELETE:
                    anim_list.getItems().removeAll(sm.getSelectedItems());
                    e.consume();
                    break;
                case ENTER:
                    isolateLieins(master_animation, true);
                    master_animation.play();
                    master_animation.setOnFinished(f->isolateLieins(master_animation, false));
                    break;
                case UP:
                    if(e.isAltDown() && sm.getSelectedIndex()>0){
                        Collections.swap(anim_list.getItems(), sm.getSelectedIndex(), sm.getSelectedIndex()-1);
                        sm.clearAndSelect(sm.getSelectedIndex()-1);
                    }
                    break;
                case DOWN:
                    if(e.isAltDown() && sm.getSelectedIndex()<anim_list.getItems().size()-1){
                        Collections.swap(anim_list.getItems(), sm.getSelectedIndex(), sm.getSelectedIndex()+1);
                        sm.clearAndSelect(sm.getSelectedIndex()+1);
                    }
                    break;
            }
        });
        math_scene.getStylesheets().add("res/animaker.css");
        math_scene.setOnKeyPressed(k->{//System.out.println(k.getCode());
            if (null!=k.getCode())
                switch (k.getCode()) {
                case DELETE:
                    deleteFocusedNode();
                    break;
                case ENTER:
                    playNext();
                    break;
                case CONTROL:
                    is_ctrl_pressed = true;
                    break;
                case C:
                    if(k.isAltDown())
                        points.clear();
                    if(k.isControlDown())
                        btn_circle.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                        0, 0, 0, 0, MouseButton.PRIMARY, 1,
                        true, true, true, true, true, true, true, true, true, true, null));
                    break;
                case L:
                    if(k.isControlDown())
                        btn_line.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                        0, 0, 0, 0, MouseButton.PRIMARY, 1,
                        true, true, true, true, true, true, true, true, true, true, null));
                    break;
                    
            }
        });
        math_scene.setOnKeyReleased(k->{
            if(k.getCode() == KeyCode.CONTROL)
                is_ctrl_pressed = false;
        });
        math_scene.widthProperty().addListener((c,o,n)->{
            //System.out.println(n);
            if((double)n>1150 ){
                Xtra.addIfDoesnotExist(MAINSCREEN.getChildren(), infoPane);
                if( !animPane.isVisible())
                    animPane.setVisible(true);
                if( !topPane.isVisible())
                    topPane.setVisible(true);
            }else if(Xtra.inRange(881,1150,(double)n)){
                MAINSCREEN.getChildren().remove(infoPane);
                if( !animPane.isVisible())
                    animPane.setVisible(true);
                if( !topPane.isVisible())
                    topPane.setVisible(true);
            } else if(Xtra.inRange(601,880,(double)n) ){
                MAINSCREEN.getChildren().remove(infoPane);
                if( animPane.isVisible())
                    animPane.setVisible(false);
                if( !topPane.isVisible()){
                    topPane.setVisible(true);
                    AnchorPane.setTopAnchor(canvas, 25.0);
                }
            } else if(Xtra.inRange(600,PREFWIDTH*0.4,(double)n) && topPane.isVisible()){
                topPane.setVisible(false);
                MAINSCREEN.getChildren().remove(infoPane);
                AnchorPane.setTopAnchor(canvas, 0.0);
            }
                
        });
        X_AXIS.setType(Liein.TYPE_TWO_ARROW);
        Y_AXIS.setType(Liein.TYPE_TWO_ARROW);
        X_AXIS.setStroke(Color.rgb(0, 89, 179));
        Y_AXIS.setStroke(Color.rgb(0, 89, 179));
        MOUSE_X.addListener((c,o,n)->{
            MOUSE_X_SHOWCASE.setText(String.format("%.2g", at_x_reverse(focusedPointer == null? (double)n : focusedPointer.getCenterX())));
        });

        MOUSE_Y.addListener((c,o,n)->{
            MOUSE_Y_SHOWCASE.setText(String.format("%.2g", at_y_reverse(focusedPointer == null? (double)n : focusedPointer.getCenterY())));
        });
        AnchorPane.setTopAnchor(topPane, 0.0);
        AnchorPane.setLeftAnchor(topPane, 0.0);
        AnchorPane.setRightAnchor(topPane, 0.0);
        AnchorPane.setTopAnchor(canvas, 25.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        AnchorPane.setLeftAnchor(canvas, 0.0);
        AnchorPane.setTopAnchor(infoPane, 0.0);
        AnchorPane.setRightAnchor(infoPane, 0.0);
        AnchorPane.setLeftAnchor(anim_list, 0.0);
        AnchorPane.setRightAnchor(anim_list, 0.0);
        AnchorPane.setBottomAnchor(anim_list, 25.0);
        AnchorPane.setBottomAnchor(multiple_anim_trgger, 0.0);
        AnchorPane.setRightAnchor(multiple_anim_trgger, 0.0);
        AnchorPane.setLeftAnchor(multiple_anim_trgger, 0.0);
        anim_list_grand_holder.setVisible(false);
        anim_list_grand_holder.setExpanded(false);
        anim_list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        anim_list.setPrefWidth(200);
        multiple_anim_trgger.setAlignment(Pos.CENTER);
        topPane.setId("hbox");
        topPane.setMinHeight(25);
        propPane.setId("prop-pane");
        itemPane.setId("item-pane");
        canvas.setId("anchor-pane");
        propPane.setMinHeight(25);
        prop_grid.setSelected(true);
        prop_linetype.setPromptText("Type:");
        prop_animType_liein.setPromptText("Animtype:");
        prop_animType_sirkale.setPromptText("Animtype:");
        
        selectedProperties.addListener((ListChangeListener<DoubleProperty>)(e->{
            while (e.next()) {                
                if(e.wasAdded() && selectedProperties.size()==2){
                    Bindings.bindBidirectional(selectedProperties.get(0), selectedProperties.get(1));
                    btn_bind_property.setText("bind");
                }
            }
        }));
        anim_list.getItems().addListener((ListChangeListener<Anymation>)(e->{
            while (e.next()) {                
                if(e.wasAdded()){
                    if (anim_list.getItems().size()==1){
                        anim_list_grand_holder.setVisible(true);
                    }
                }else if (e.wasRemoved()){
                    if (anim_list.getItems().isEmpty()){
                        anim_list_grand_holder.setVisible(false);
                    }
                }
                
            }
        }));
        drawnShapes.addListener((ListChangeListener<Shape>)(e->{
            while (e.next()) {                
                if(e.wasAdded()){
                    if(drawnShapes.size()>1 )
                        Xtra.addIfDoesnotExist(itemPane.getChildren(), btn_bind_property);
                    canvas.getChildren().addAll(e.getAddedSubList());
                    e.getAddedSubList().forEach(addedShape->{
                        addPointer(addedShape);
                    if(addedShape instanceof Liein){
                        Liein l =(Liein)addedShape;
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
                    }else if (addedShape instanceof Sirkale) {
                        Sirkale c = (Sirkale) addedShape;
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
                    }
                    });
                    
                }else if (e.wasRemoved()){
                    if(drawnShapes.size()<=1 )
                        itemPane.getChildren().remove(btn_bind_property);
                    canvas.getChildren().removeAll(e.getRemoved());
                }
            }
        }));
        points.addListener((ListChangeListener<Shape>)(e->{
            while (e.next()) {                
                if(e.wasAdded()){
                    drawnShapes.addAll(e.getAddedSubList());
                }else if (e.wasRemoved()){
                    drawnShapes.removeAll(e.getRemoved());
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
                            else if (closestPointers.indexOf(p) != -1) 
                                closestPointers.remove(p);
                        });
                        canvas.getChildren().add(p);
                    });
                }else if (e.wasRemoved()) {
                    e.getRemoved().forEach(p->{
                        closestPointers.remove(p);
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
                if(e.wasAdded() && prop_grid.isSelected() && (toDraw!=0 || is_ctrl_pressed)){
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
                        if(focusedPointer!=null){
                            focusedPointer.setVisible(false);
                            //co_ordinate_showcase.setVisible(false);
                        }
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
        
        
        for (double i=1,j=-1; at_x(i)<= PREFWIDTH; i++,j--){/*vertical lines*/
            Line lpos = new Line(at_x(i), 0, at_x(i), PREFHEIGHT-25), lneg = new Line(at_x(j), 0, at_x(j), PREFHEIGHT-25);
            vertical_grides.add(lpos);
            vertical_grides.add(lneg);
        }
        vertical_grides.forEach(vl->{
            vl.startXProperty().bind(Y_AXIS.startX.add(vl.getStartX()-Y_AXIS.getStartX()));
            vl.endXProperty().bind(Y_AXIS.endX.add(vl.getEndX()-Y_AXIS.getEndX()));
        });
        for (double i=1,j=-1; at_y(i)<= PREFHEIGHT; i++,j--){/*horizontal lines*/
            Line lpos = new Line(0,at_y(i),PREFWIDTH,at_y(i)), lneg = new Line(0,at_y(j),PREFWIDTH,at_y(j));
            horizontal_grides.add(lpos);
            horizontal_grides.add(lneg);
        }
        horizontal_grides.forEach(hl->{
            hl.startYProperty().bind(X_AXIS.startY.add(hl.getStartY()-X_AXIS.getStartY()));
            hl.endYProperty().bind(X_AXIS.endY.add(hl.getEndY()-X_AXIS.getEndY()));
        });
        grides.addAll(vertical_grides);
        grides.addAll(horizontal_grides);
        grides.forEach(grd->grd.setStroke(Color.rgb(0, 89, 179, 0.5)));
        grides.add(X_AXIS);
        grides.add(Y_AXIS);
        grides.forEach(grd->grd.visibleProperty().bind(prop_grid.selectedProperty()));
        canvas.getChildren().addAll(grides);
        /*pointers*/
        for (double i=1, h=-1; at_x(i)< PREFWIDTH; i++,h--)
            for (double j=1,k=-1; at_y(j)<PREFHEIGHT; j++,k--)/*1st and 4th quadrent*/
                pointers.addAll(new Pointer(at_x(i), at_y(j)),new Pointer(at_x(i), at_y(k)), new Pointer(at_x(h), at_y(j)),new Pointer(at_x(h), at_y(k)));
            
        for (double j=1,k=-1; at_y(j)<PREFHEIGHT; j++,k--)
            pointers.addAll(new Pointer(at_x(0), at_y(j)),new Pointer(at_x(0), at_y(k)));
        for (double l=1,k=-1; at_x(l)< PREFWIDTH; l++,k--)
            pointers.addAll(new Pointer(at_x(l), at_y(0)),new Pointer(at_x(k), at_y(0)));
        
        System.out.println(X_AXIS.getIntersectionPoint(Y_AXIS));
        pointers.add(new Pointer(PREFWIDTH/2 , (PREFHEIGHT-25)/2));/*origin*/
        canvas.setOnMousePressed(e -> {
            dragCondition = 0;
            firstClick = focusedPointer==null? new Point2D(e.getX(), e.getY()): new Point2D(focusedPointer.getCenterX(), focusedPointer.getCenterY());
        });
        canvas.setOnMouseClicked(e->{//System.out.println("mouse clicked");
            if(anim_list_grand_holder.isExpanded())
                anim_list_grand_holder.setExpanded(false);
            if(dragCondition == DRAGENDED)
                return;
            if(e.isControlDown()){
                points.add(focusedPointer == null? new Point(e.getX(), e.getY()): new Point(focusedPointer.getCenterX(), focusedPointer.getCenterY()));
            }
            
            if(mode== MODE_SELECT_POINT){
                int i = anim_list.getItems().indexOf(master_animation);
                anim_list.getItems().remove(master_animation);
                if(focusedShape instanceof Liein){
                    if("start".equals(prop_animType_liein.getValue())){
                        master_animation = new ParallelAnymation(new SingleAnimation(lastFocusedLiein.startX, e.getX(),Double.valueOf(prop_anim_duration.getText())),
                                        new SingleAnimation(lastFocusedLiein.startY, e.getY(),Double.valueOf(prop_anim_duration.getText())));
                    }else{
                        master_animation = new ParallelAnymation(new SingleAnimation(lastFocusedLiein.endX, e.getX(),Double.valueOf(prop_anim_duration.getText())),
                                        new SingleAnimation(lastFocusedLiein.endY, e.getY(),Double.valueOf(prop_anim_duration.getText())));
                        
                    }
                }else if(focusedShape instanceof Sirkale){
                    master_animation = new ParallelAnymation(new SingleAnimation(lastFocusedSirkale.centerXProperty(), e.getX(),Double.valueOf(prop_anim_duration.getText())),
                                    new SingleAnimation(lastFocusedSirkale.centerYProperty(), e.getY(),Double.valueOf(prop_anim_duration.getText())));
                }
                anim_list.getItems().add(i, master_animation);
                mode =0;
            }else if (hoveredShape!=null) {
                if (dragCondition == 0)/*for drag ended event, that also fire mouseClicked event*/
                    focusOnShape(hoveredShape);
            } else{
                toDraw = 0;
                if(!Xtra.addIfDoesnotExist(itemPane.getChildren(), btn_line))
                    Xtra.addIfDoesnotExist(itemPane.getChildren(), btn_circle);
                unfocus(null);
                animPane.getChildren().removeAll(prop_anim_duration, prop_anim_to_value);
            }
        });
        canvas.setOnMouseMoved(e->{
            MOUSE_X.set(e.getX());
            MOUSE_Y.set(e.getY());
            
        });
        canvas.setOnDragDetected(e->{//System.out.println(canvas.getHeight()+"..."+canvas.getWidth());
            dragCondition = DRAGDETECTED;
            canvas.startFullDrag();
            if (toDraw==DRAWELINE) {
                Liein l = new Liein(firstClick.getX(),firstClick.getY(),e.getX(), e.getY());
                if(!Objects.equals(Liein.curType, 0))
                    l.setType(Liein.curType);
                canvas.getChildren().add(l);
                focusOnShape(l);
            }else if ( toDraw == DRAWCIRCLE){
                Sirkale c = new Sirkale(firstClick.getX() ,firstClick.getY());
                canvas.getChildren().add(c);
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
        canvas.setOnMouseDragReleased(e->{
            dragCondition = DRAGENDED;
            if(toDraw!=0){
                if(toDraw == DRAWELINE ){/*if the mouse was released on a pointer*/
                    canvas.getChildren().remove(focusedShape);
                    drawnShapes.add(focusedShape);
                    if(focusedPointer!= null)
                        lastFocusedLiein.setEnd(focusedPointer.getCenterX(), focusedPointer.getCenterY());
                }
                else if ( toDraw == DRAWCIRCLE ){
                    canvas.getChildren().remove(focusedShape);
                    drawnShapes.add(focusedShape);
                    if(focusedPointer!= null)
                        lastFocusedSirkale.setRadius(lastFocusedSirkale.distanceFromCenter(focusedPointer.getCenterX(), focusedPointer.getCenterY()));
                }
                /*addPointer(focusedShape);after dragging ended, add pointers of the corresponding shape; adding pointers during dragging time 
                                          *** may cause unnecessary time lag  */
            }
        });
        
    }
    public void lockScreen(){
        math_scene.widthProperty().addListener((c,o,n)->{
            double nn = (double)n;
            Y_AXIS.startX.set(nn/2);
            Y_AXIS.endX.set(nn/2);
            X_AXIS.endX.set(nn);
        });
        math_scene.heightProperty().addListener((c,o,n)->{
            double nn = (double)n;
            X_AXIS.startY.set((nn-25)/2);
            X_AXIS.endY.set((nn-25)/2);
            Y_AXIS.endY.set(nn-25);
        });
    }
    public void isolateLieins(Anymation an, Boolean boo){
        List<SingleAnimation> singles = new ArrayList();
        master_animation.getSingleAnimations().forEach(e->{
            if ("length".equals(e.getObservable().getName()) || "angle".equals(e.getObservable().getName())) {
                singles.add(e);
                ((Liein) e.getObservable().getBean()).isolate = true;
            }
        });
        master_animation.setOnFinished(e->singles.forEach(f-> ((Liein) f.getObservable().getBean()).isolate = false));
    }
    
    /*user to developer*/
    private double at_x(double x){ 
        return x*GRIDGAPE+PREFWIDTH/2;
    }
    private double at_y(double y){ 
        return y*GRIDGAPE+(PREFHEIGHT-25)/2;
    }
    
    /*developer to user*/
    private double at_x_reverse(double x){
        return (x-PREFWIDTH/2)/GRIDGAPE;
    }
    private double at_y_reverse(double y){
        return (y-(PREFHEIGHT-25)/2)/GRIDGAPE;
    }
    
    private void updateItemInListView(Anymation anmtn){
        int index = anim_list.getItems().indexOf(anmtn);
        if(index !=-1){
            anim_list.getItems().remove(anmtn);
            anim_list.getItems().add(index, anmtn);
        }
    }
    public void playNext(){
        if(!anim_list_grand_holder.isVisible())
            return;
        MultipleSelectionModel msm = anim_list.getSelectionModel();
        if(msm.getSelectedIndex()==anim_list.getItems().size()-1|| msm.getSelectedIndex()==-1){
            msm.select(0);
        }else{
            msm.clearAndSelect(msm.getSelectedIndex()+1);
        }
        isolateLieins(((Anymation)msm.getSelectedItem()), true);
        ((Anymation)msm.getSelectedItem()).play();
        ((Anymation)msm.getSelectedItem()).setOnFinished(e-> isolateLieins(((Anymation)msm.getSelectedItem()), false));
    }
    private void addPointer(Shape n){
        if (n instanceof Liein) {
            Liein l = (Liein) n ;
            pointers.addAll(new Pointer(l, Pointer.START), new Pointer(l, Pointer.MIDDLE), new Pointer(l, Pointer.END));
        }else if (n instanceof Sirkale || n instanceof Point) {
            pointers.add(new Pointer((Circle)n));
        }
    }
    private List<Pointer> allPointerIn(double x, double y){
        return pointers.stream().filter((p) -> (p.getCenterX()==x && p.getCenterY()==y)).collect(Collectors.toList());
    }
    
    private<T extends Shape> void focusOnShape(T t){
        if(focusedShape == t)
            return;
        if(focusedShape != null)
            unfocus(t);
        focusedShapeStroke =(Color) t.getStroke();
        t.setStroke(Color.WHITE);
        prop_color.setValue((Color) t.getStroke());
        animPane.getChildren().removeAll(prop_anim_to_value, prop_anim_duration);
        /*prop_stroke.setObservable(t.strokeWidthProperty());*/
        if (t instanceof Liein){
            Liein l = (Liein) t;
            lastFocusedLiein = l;
            prop_length.setObservable(l.length);
            prop_angle.setObservable(l.gradient);
            prop_startX.setObservable(l.startX);
            prop_startY.setObservable(l.startY);
            prop_linetype.setValue(l.getType());
            prop_animType_liein.setValue(null);
            propPane.getChildren().addAll(lieinRelatedProps);
            Xtra.addIfDoesnotExist(propPane.getChildren(), prop_linetype);
            Xtra.addIfDoesnotExist(animPane.getChildren(), prop_animType_liein);
            
        } else if (t instanceof Sirkale){
            Sirkale c = (Sirkale) t;
            lastFocusedSirkale = c;
            c.fillProperty().bind(prop_fill.valueProperty());
            prop_radius.setObservable(c.radiusProperty());
            prop_centerX.setObservable(c.centerXProperty());
            prop_centerY.setObservable(c.centerYProperty());
            prop_animType_sirkale.setValue(null);
            propPane.getChildren().addAll(sirkaleRelatedProps);
            Xtra.addIfDoesnotExist(animPane.getChildren(), prop_animType_sirkale);
        }
        focusedShape =  t;
    }
    public<T extends Shape> void unfocus(T newFocus){/*new focus will be null if user click on empty space*/
        if(focusedShape==null)
            return;
        //focusedShape.strokeWidthProperty().unbindBidirectional(prop_stroke.getObservable());
        focusedShape.strokeProperty().unbind();
        focusedShape.setStroke(focusedShapeStroke);
        if (focusedShape instanceof Liein) {
            propPane.getChildren().removeAll(lieinRelatedProps);
            if (!(newFocus instanceof Liein)) {
                animPane.getChildren().clear();
                propPane.getChildren().remove(prop_linetype);
                if(newFocus == null){
                    propPane.getChildren().clear();
                    focusedShape = null;
                }else if(newFocus instanceof Sirkale){
                    propPane.getChildren().removeAll(lieinRelatedProps);
                    animPane.getChildren().clear();
                }
            }
            lastFocusedLiein.length.unbindBidirectional(prop_length.getObservable());
            lastFocusedLiein.gradient.unbindBidirectional(prop_angle.getObservable());
            lastFocusedLiein.startX.unbindBidirectional(prop_startX.getObservable());
            lastFocusedLiein.startY.unbindBidirectional(prop_startY.getObservable());
        } else if (focusedShape instanceof Sirkale){
            propPane.getChildren().removeAll(sirkaleRelatedProps);
             if (!(newFocus instanceof Sirkale)){
                animPane.getChildren().clear();
                if(newFocus == null){
                    propPane.getChildren().clear();
                    focusedShape = null;
                }else if(newFocus instanceof Liein){
                    propPane.getChildren().removeAll(lieinRelatedProps);
                    animPane.getChildren().clear();
                }
             } 
            lastFocusedSirkale.radiusProperty().unbindBidirectional(prop_radius.getObservable());
            lastFocusedSirkale.centerXProperty().unbindBidirectional(prop_centerX.getObservable());
            lastFocusedSirkale.centerYProperty().unbindBidirectional(prop_centerY.getObservable());
            lastFocusedSirkale.fillProperty().unbind();
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
        unfocus(null);
    }
}

/*
*** Pointers will not be added while the mouse is being dragged. After finishing the mouse dragged event. 
*** Pointers will be added  at setOnMouseDragReleased event
***
*/