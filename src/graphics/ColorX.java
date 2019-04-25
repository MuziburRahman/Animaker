
package graphics;

import javafx.scene.paint.Color;

/**
 *
 * @author's email thisismuzib@gmail.com
 */
public class ColorX {
    private Integer red,green,blue;
    private Double opacity;
    private Color color;

    public ColorX(Color color) {
        this.color = color;
        this.red = (int) (255*color.getRed());
        this.green = (int) (255*color.getGreen());
        this.blue = (int) (255*color.getBlue());
        this.opacity = color.getOpacity();
    }

    public ColorX() {
        this(Color.WHITE);
    }
    
    public static ColorX rgb(Integer r, Integer g, Integer b){
        return new ColorX(Color.rgb(r, g, b));
    }
    
    public ColorX brighterBy(int percentage){
        int newR, newG, newB;
        if (this.red+this.red*percentage/100>255) newR =255;
            else newR = this.red+this.red*percentage/100;
        if(this.green+this.green*percentage/100>255) newG = 255;
            else newG = this.green+this.green*percentage/100;
        if (this.blue+this.blue*percentage/100>255) newB = 255;
            else newB = this.blue+this.blue*percentage/100;
        return ColorX.rgb(newR, newG, newB);
    }
    public ColorX dimmedBy(int percentage){
        return new ColorX(Color.rgb(this.red-this.red*percentage/100,
                            this.green-this.green*percentage/100,
                            this.blue-this.blue*percentage/100));
    }

    public Color toColor() {
        return color;
    }

    public Integer getRed() {
        return red;
    }

    public void setRed(Integer red) {
        if ( red<=255 && red>=0){
            this.color = Color.rgb(red, this.blue, this.green);
        }
    }

    public Integer getGreen() {
        return green;
    }

    public void setGreen(Integer green) {
        if ( green<=255 && green>=0){
            this.color = Color.rgb(this.red, green, this.blue);
        }
    }

    public Integer getBlue() {
        return blue;
    }

    public void setBlue(Integer blue) {
        if ( blue<=255 && blue>=0){
            this.color = Color.rgb(this.red, this.green, blue);
        }
    }

    public Double getOpacity() {
        return opacity;
    }

    public void setOpacity(Double opacity) {
        this.opacity = opacity;
    }

    @Override
    public String toString() {
        return "ColorX{" + "red=" + red + ", green=" + green + ", blue=" + blue + ", opacity=" + opacity + '}';
    }
    
}
