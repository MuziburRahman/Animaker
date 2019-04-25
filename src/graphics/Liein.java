
package graphics;

import animaker.AppScreen;
import animaker.Xtra;
import com.sun.javafx.scene.DirtyBits;
import javafx.beans.property.DoubleProperty; 
import javafx.beans.property.DoublePropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public class Liein extends Path{
    public static final String TYPE_NO_ARROW="line", TYPE_TWO_ARROW="<-line->", TYPE_START_ARROW="<-line",TYPE_END_ARROW="line->";
    public static String curType = "line";
    public DoubleProperty startX  = new DoublePropertyBase(0.0) {

        @Override
        public void invalidated() {
            impl_markDirty(DirtyBits.NODE_GEOMETRY);
            impl_geomChanged();
        }

        @Override
        public Object getBean() {
            return Liein.this;
        }

        @Override
        public String getName() {
            return "startX";
        }
    };
    
    public DoubleProperty endX = new DoublePropertyBase(0.0) {

        @Override
        public void invalidated() {
            impl_markDirty(DirtyBits.NODE_GEOMETRY);
            impl_geomChanged();
        }

        @Override
        public Object getBean() {
            return Liein.this;
        }

        @Override
        public String getName() {
            return "endX";
        }
    };
    
    public final DoubleProperty startY = new DoublePropertyBase(0.0) {

        @Override
        public void invalidated() {
            impl_markDirty(DirtyBits.NODE_GEOMETRY);
            impl_geomChanged();
        }

        @Override
        public Object getBean() {
            return Liein.this;
        }

        @Override
        public String getName() {
            return "startY";
        }
    };
    
    public final DoubleProperty endY = new DoublePropertyBase(0.0) {

        @Override
        public void invalidated() {
            impl_markDirty(DirtyBits.NODE_GEOMETRY);
            impl_geomChanged();
        }

        @Override
        public Object getBean() {
            return Liein.this;
        }

        @Override
        public String getName() {
            return "endY";
        }
    };
    
    public DoubleProperty gradient = new DoublePropertyBase(0.0) {

        @Override
        public void invalidated() {
            impl_markDirty(DirtyBits.NODE_GEOMETRY);
            impl_geomChanged();
        }

        @Override
        public Object getBean() {
            return Liein.this;
        }

        @Override
        public String getName() {
            return "angle";
        }
    };/*in degree*/
    
    public final DoubleProperty length = new DoublePropertyBase(1.0) {

        @Override
        public void invalidated() {
            impl_markDirty(DirtyBits.NODE_GEOMETRY);
            impl_geomChanged();
        }

        @Override
        public Object getBean() {
            return Liein.this;
        }

        @Override
        public String getName() {
            return "length";
        }
    };
    
    public final DoubleProperty distance_from_mouse = new DoublePropertyBase(0.0) {

        @Override
        public void invalidated() {
            impl_markDirty(DirtyBits.NODE_GEOMETRY);
            impl_geomChanged();
        }

        @Override
        public Object getBean() {
            return Liein.this;
        }

        @Override
        public String getName() {
            return "distance_from_mouse";
        }
    };
    private final DoubleProperty iCap = new DoublePropertyBase(0.0) {

        @Override
        public void invalidated() {
            impl_markDirty(DirtyBits.NODE_GEOMETRY);
            impl_geomChanged();
        }

        @Override
        public Object getBean() {
            return Liein.this;
        }

        @Override
        public String getName() {
            return "x_component";
        }
    };
    private final DoubleProperty jCap= new DoublePropertyBase(0.0) {

        @Override
        public void invalidated() {
            impl_markDirty(DirtyBits.NODE_GEOMETRY);
            impl_geomChanged();
        }

        @Override
        public Object getBean() {
            return Liein.this;
        }

        @Override
        public String getName() {
            return "y_component";
        }
    };;
    
    private PointPolar fixedPoint ;/*when rotating, this line will rotate about this point*/
    private final MoveTo moveToStart = new MoveTo(),moveToEnd = new MoveTo();
    private final MoveTo moveToStart2=new MoveTo(),moveToStart3=new MoveTo(), moveToEnd3=new MoveTo();
    private final LineTo lineto_main = new LineTo(),lineto1 = new LineTo(), lineto2 = new LineTo(),lineto3 = new LineTo(), lineto4 = new LineTo();
    private final ObservableList<PathElement> sartarrows = FXCollections.observableArrayList(moveToStart2,lineto1,moveToStart3,lineto2);
    private final ObservableList<PathElement> endArrows = FXCollections.observableArrayList(/*moveToEnd2,*/lineto3,moveToEnd3,lineto4);
    private final double alpha = Math.atan(3.0/4.0);
    private double direction_indicator_length = 10;
    public Boolean isolate = false ;
    private String type = "line";
    
    public Liein(double sx, double sy, double ex, double ey) {
        startX.addListener((c,o,n)->{
            double nn =(double) n;
            iCap.set(getEndX()-nn);
            moveToStart.setX(nn);
            moveToStart2.setX(nn);
            moveToStart3.setX(nn);
            lineto1.setX(nn+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())+alpha));
            lineto2.setX(nn+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())-alpha));
            lineto1.setY(getStartY()+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())+alpha));
            lineto2.setY(getStartY()+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())-alpha));
            lineto3.setX(getEndX()+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())+Math.PI-alpha));
            lineto4.setX(getEndX()+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())-Math.PI+alpha));
            lineto3.setY(getEndY()+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())+Math.PI-alpha));
            lineto4.setY(getEndY()+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())-Math.PI+alpha));
        });
        startY.addListener((c,o,n)->{
            double nn =(double) n;
            jCap.set(getEndY()-nn);
            moveToStart.setY(nn);
            moveToStart2.setY(nn);
            moveToStart3.setY(nn);
            lineto1.setX(getStartX()+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())+alpha));
            lineto2.setX(getStartX()+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())-alpha));
            lineto1.setY(nn+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())+alpha));
            lineto2.setY(nn+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())-alpha));
            lineto3.setX(getEndX()+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())+Math.PI-alpha));
            lineto4.setX(getEndX()+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())-Math.PI+alpha));
            lineto3.setY(getEndY()+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())+Math.PI-alpha));
            lineto4.setY(getEndY()+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())-Math.PI+alpha));
            
        });
        endX.addListener((c,o,n)->{
            double nn =(double) n;
            iCap.set(nn-getStartX());
            moveToEnd.setX(nn);
            moveToEnd3.setX(nn);
            lineto_main.setX(nn);
            lineto1.setX(getStartX()+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())+alpha));
            lineto2.setX(getStartX()+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())-alpha));
            lineto1.setY(getStartY()+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())+alpha));
            lineto2.setY(getStartY()+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())-alpha));
            lineto3.setX(nn+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())+Math.PI-alpha));
            lineto4.setX(nn+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())-Math.PI+alpha));
            lineto3.setY(getEndY()+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())+Math.PI-alpha));
            lineto4.setY(getEndY()+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())-Math.PI+alpha));
        });
        endY.addListener((c,o,n)->{
            double nn =(double) n;
            jCap.set(nn-getStartY());
            moveToEnd.setY(nn);
            moveToEnd3.setY(nn);
            lineto_main.setY(nn);
            lineto1.setX(getStartX()+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())+alpha));
            lineto2.setX(getStartX()+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())-alpha));
            lineto1.setY(getStartY()+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())+alpha));
            lineto2.setY(getStartY()+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())-alpha));
            lineto3.setX(getEndX()+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())+Math.PI-alpha));
            lineto4.setX(getEndX()+direction_indicator_length*Math.cos(angle(iCap.get() , jCap.get())-Math.PI+alpha));
            lineto3.setY(nn+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())+Math.PI-alpha));
            lineto4.setY(nn+direction_indicator_length*Math.sin(angle(iCap.get() , jCap.get())-Math.PI+alpha));
        });
        iCap.addListener((c,o,n)->{
            invalidate_direction_indicator_length();
            if(getInstantLength()<1) return;
            if(!isolate){
                length.set(Math.sqrt(Math.pow((double) n, 2)+Math.pow(jCap.get(), 2)));
                gradient.set(Math.toDegrees(angle((double)n, jCap.get())));
            }
        });
        jCap.addListener((c,o,n)->{
            invalidate_direction_indicator_length();
            if(getInstantLength()<1) return;
            if(!isolate){
                length.set(Math.sqrt(Math.pow(iCap.get(), 2)+Math.pow((double) n, 2)));
                gradient.set(Math.toDegrees(angle(iCap.get(), (double)n)));
            }
        });
        length.addListener((c,o,n)->{
            if(isolate){
                double lngth = (double) n;
                double angl =  Math.toRadians(gradient.get());
                setEnd(getStartX()+lngth*Math.cos(angl), getStartY()+lngth*Math.sin(angl));
            }
        });
        gradient.addListener((c,o,n)->{/*only on the eve of animations, isolate will be true;*/
            if(isolate){
                double angl =  Math.toRadians((double)n);
                setEnd(getStartX()+length()*Math.cos(angl), getStartY()+length()*Math.sin(angl));
            }
        });
        setStrokeWidth(2);
        startX.set(sx);
        startY.set(sy);
        endX.set(ex);
        endY.set(ey);
        gradient.set(Math.toDegrees(angle(ey-sy,ex-sx)));
        getElements().addAll(moveToStart,lineto_main/*,moveToStart,lineto1,moveToStart,lineto2,moveToEnd,lineto3,moveToEnd,lineto4*/);
        setStroke(Color.GREEN);
        AppScreen.MOUSE_X.addListener((c,o,n)-> distance_from_mouse.set(perpendicular_distance_from((double) n, AppScreen.MOUSE_Y.get())) );
        AppScreen.MOUSE_Y.addListener((c,o,n)-> distance_from_mouse.set(perpendicular_distance_from(AppScreen.MOUSE_X.get(), (double) n)) );
        
    }    
    private void invalidate_direction_indicator_length(){
       Double dis = new Point2D(getStartX(), getStartY()).distance(getEndX(), getEndY());
        if(dis<15)
            direction_indicator_length = 0;
        else if(dis<30)
            direction_indicator_length = 5;
        else if(direction_indicator_length<10)
            direction_indicator_length = 10;
    }
    public void setType(String type){
        if(this.type == null ? type != null : !this.type.equals(type)){
            switch (type) {
                case TYPE_NO_ARROW:
                    getElements().remove(2, getElements().size());
                    break;
                case TYPE_START_ARROW:
                    {
                        switch (this.type) {
                            case TYPE_NO_ARROW:
                                getElements().addAll(sartarrows);
                                break;
                            case TYPE_END_ARROW:
                                getElements().removeAll(endArrows);
                                getElements().addAll(sartarrows);
                                break;
                            case TYPE_TWO_ARROW:
                                getElements().removeAll(endArrows);
                                break;
                        }break;
                    }
                case TYPE_END_ARROW:
                    {
                        switch (this.type) {
                            case TYPE_NO_ARROW:
                                getElements().addAll(endArrows);
                                break;
                            case TYPE_START_ARROW:
                                getElements().removeAll(sartarrows);
                                getElements().addAll(endArrows);
                                break;
                            case TYPE_TWO_ARROW:
                                getElements().removeAll(sartarrows);
                                break;
                        }break;
                    }
                case TYPE_TWO_ARROW:
                    {
                        switch (this.type) {
                            case TYPE_NO_ARROW:
                                getElements().addAll(endArrows);
                                getElements().addAll(sartarrows);
                                break;
                            case TYPE_START_ARROW:
                                getElements().addAll(endArrows);
                                break;
                            case TYPE_END_ARROW:
                                getElements().addAll(sartarrows);
                                break;
                        }break;
                    }
            }
            this.type= type;
        }
    }
    public String getType(){
        return this.type;
    }
    public void setEnd(Double x, Double y){
        endX.set(x);
        endY.set(y);
    }
    public Double getStartX(){
        return startX.get();
    }
    public Double getStartY(){
        return startY.get();
    }
    public Double getMiddleX(){
        return ((startX.get()+endX.get())/2);
    }
    public Double getMiddleY(){
        return ((startY.get()+endY.get())/2);
    }
    public Double getEndX(){
        return endX.get();
    }
    public Double getEndY(){
        return endY.get();
    }
    public Double getInstantLength(){
        return new Point2D(getStartX(), getStartY()).distance(getEndX() , getEndY());
    }
    public boolean isCloseTo(Double x, Double y){
        return perpendicular_distance_from(x, y)<=5
                && Xtra.inRangeWithPrecision(startX.get()-2, endX.get()+2, x, 3)
                && Xtra.inRangeWithPrecision(startY.get()-2, endY.get()+2, y, 3);
    }
    public double perpendicular_distance_from(Double x, Double y){
        Double x1= startX.get(), y1= startY.get(), x2= endX.get(), y2= endY.get();
        return Math.abs(x*(y1-y2)-y*(x1-x2)+x1*y2-x2*y1) // |ax+by+c |
                    / Math.sqrt(Math.pow(y1-y2, 2)+Math.pow(x1-x2, 2));// root(a^2+b^2)
    }
    public double length(){
        return length.get();
    }
    public final double angle(double x, double y){   /* in radians, of course */
        if (x>=0 && y>=0)
            return Math.atan(y/x);
        else if (x<0 && y>=0)
            return Math.PI - Math.atan(y/Math.abs(x));
        else if (x<0 && y<0)
            return Math.PI + Math.atan(y/x);
        else
            return 2*Math.PI - Math.atan(Math.abs(y)/x);
    }

    @Override
    public String toString() {
        return "Liein{" + "startX=" + getStartX() + ", startY=" + getStartY() + ", length=" + length.get()+ '}';
    }
    
}
