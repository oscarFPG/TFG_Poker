public class App extends Application{

    @Override
    public void start(Stage templateStyle) throws Exception {
        Parent root = FXMLLoader.load(getClass(),getResource("templateStyle.fxml"))
        Scene scene = new Scene();
        scene.getStylesheets().add(getClass().getResources("/styles/templateStyle.css").toExternalForm());

        templateStyle.setTitle("Venta Plantilla Estilo");
        templateStyle.setScene(scene);
        templateStyle.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
