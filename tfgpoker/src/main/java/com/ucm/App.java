public class App extends Application{

    @Override

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass(),getResource("templateStyle"))
        Scene scene = new Scene();
        scene.getStylesheets().add(getClass().getResources("/styles/templateStyle.css"))

        
    }

    public static void main(String[] args){
        launch(args);
    }

}
