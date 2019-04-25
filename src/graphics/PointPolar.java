
package graphics;

import javafx.geometry.Point2D;


public class PointPolar{
    
    private double radius;
    private double angle;// all angle are in radians
    private double x,y;
    
    public static PointPolar fromRef(PointPolar p, double rds, double angl){
        return p.asOrigin(rds, angl);
    }
    public static PointPolar fromRef(Point2D p, double rds, double angl){
        PointPolar pp = PointPolar.fromPoint2D(p);
        return pp.asOrigin(rds, angl);
    }
    public static PointPolar fromPoint2D(double x, double y){
        return new PointPolar (Math.sqrt(x*x+y*y), Math.atan(y/x));
    }
    public static PointPolar fromPoint2D(Point2D p){
        double x=p.getX(),y=p.getY();
        return new PointPolar (Math.sqrt(x*x+y*y), Math.atan(y/x));
    }
    public PointPolar(double radius, double theta){
        this.radius= radius;
        this.angle= theta;
        x = radius*Math.cos(theta);
        y = radius*Math.sin(theta);
    }
    public PointPolar(){
        this(0,0);
    }
    public PointPolar asOrigin(double rds, double angl){
        double tx=rds*Math.cos(angl) + this.x , ty=rds*Math.sin(angl) + this.y;
        double anglr=Math.abs(ty)/Math.abs(tx);
        if(ty<0 && tx<0)
            anglr= Math.PI + Math.atan(anglr);
        else if(ty>=0  &&  tx<0)
            anglr= Math.PI - Math.atan(anglr);
        else if(ty<0 && tx>=0)
            anglr= 2*Math.PI -  Math.atan(anglr);
        else anglr= Math.atan( ty/tx );
        PointPolar pnt = new PointPolar (Math.sqrt(tx*tx+ty*ty), anglr);
        //System.out.print("\n rad ="+rds+", angl="+Math.toDegrees(angl)+"\t returning = " +pnt);
        return pnt;
    }

    public double getAngle() {
        return angle;
    }
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public double getRadius() {
        return radius;
    }
    public void setRadius(double radius) {
        this.radius = radius;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    
    public Point2D toPoint2D(){
        return new Point2D(x, y);
    }
    public double angleWith (Point2D p){
        return angleBetween(x, y, p.getX(), p.getY());
    }
    
    public static double angleBetween (Double x1, Double y1, Double x2, Double y2){
        if (x1 < x2 && y1 < y2) /*1st quardent, as screen co-ordinate system*/
            return Math.atan((y2-y1) / (x2-x1));
        else if (x1 > x2 && y1 <= y2) 
            return Math.PI-Math.atan((y2-y1) / (x1-x2));
        else if (x1 > x2 && y1 > y2) 
            return Math.PI+Math.atan((y1-y2) / (x1-x2));
        else return 2*Math.PI-Math.atan((y1-y2) / (x2-x1));/*4th quardent*/
    }
    public double angleWith (PointPolar p){
        return this.angleWith(p.toPoint2D());
    }
    public double distanceWith(Point2D p){
        return Math.sqrt((x-p.getX())*(x-p.getX()) + (y-p.getY())*(y-p.getY()));
    }
    public double distanceWith (PointPolar p){
        return this.distanceWith(p.toPoint2D());
    }

    @Override
    public String toString() {
        return "PointPolar{" + "radius=" + radius + ", angle=" + Math.toDegrees(angle) + ", x=" + x + ", y=" + y + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PointPolar other = (PointPolar) obj;
        return Math.abs(this.x-other.x)<0.00000001 && Math.abs(this.x- other.x)<0.00000001;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.radius) ^ (Double.doubleToLongBits(this.radius) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.angle) ^ (Double.doubleToLongBits(this.angle) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }
    
}
