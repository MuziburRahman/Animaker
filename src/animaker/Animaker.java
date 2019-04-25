
package animaker;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author Muzibur Rahman
 * @email  thisismuzib@gmail.com
 */
public class Animaker extends Application {
    
    public static AppScreen totalScreen = new AppScreen();
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Animaker");
        primaryStage.setMinHeight(totalScreen.PREFHEIGHT*0.4);
        primaryStage.setMinWidth(totalScreen.PREFWIDTH*0.4);
        primaryStage.setScene(totalScreen.math_scene);
        primaryStage.getIcons().add(new Image("res/icon.png"));
        primaryStage.show();
        totalScreen.lockScreen();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
