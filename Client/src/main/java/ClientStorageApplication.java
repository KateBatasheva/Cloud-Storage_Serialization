import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientStorageApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("registration.fxml"));
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("Cloud Storage");
        primaryStage.setOnCloseRequest(event -> {
            try {
                GoToNet.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        primaryStage.show();
    }
}
