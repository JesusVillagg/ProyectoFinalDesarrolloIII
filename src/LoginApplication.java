import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginApplication extends Application{

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("logincliente.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Casa de Jade - Login Cliente");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        LoginController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);
    }

    public static void main(String[] args) {
        launch();
    }

}
