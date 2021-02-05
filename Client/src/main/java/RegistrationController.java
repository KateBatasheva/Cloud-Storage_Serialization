import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {

    public TextField tf_loginField;
    public PasswordField pf_password;
    public TextField tf_nickField;
    public TextArea ta_mainmessage;
    public Button B_reg;
    public Button B_enter;
    public TextArea ta_result;
    public VBox registrationForm;

    private Stage regStage;

    private ClientStorageController controller;

    public void setController(ClientStorageController controller) {
        this.controller = controller;
    }

    public VBox getRegistrationForm() {
        return registrationForm;
    }

    public void clickReg(MouseEvent mouseEvent) throws IOException {
        String password = pf_password.getText().trim();
        String nick = tf_nickField.getText().trim();
        controller.tryRegistr(nick, password);
    }

    public void clickEnter(MouseEvent mouseEvent) throws IOException {
        String password = pf_password.getText().trim();
        String nick = tf_nickField.getText().trim();
        controller.tryAuth(nick, password);

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GoToNet.start();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("clientlayout.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        regStage = new Stage();
        regStage.setTitle("Cloud Storage");
        regStage.setScene(new Scene(root, 600, 500));
        regStage.initModality(Modality.APPLICATION_MODAL);
            regStage.setOnCloseRequest(event -> {
                try {
                    GoToNet.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        controller = fxmlLoader.getController();
        controller.regController = this;
//        controller.setRegController(this);
        controller.start();
    }

    public void showCloudStorage (String nick){
        Platform.runLater(()->
        {
            regStage.show();
            controller.setAuthentif(true);
            controller.refreshLists(nick);

        });
    }

    public void showAll(MouseEvent mouseEvent) {
        controller.askForUsers();
    }
}
