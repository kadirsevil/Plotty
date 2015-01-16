package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Main extends Application {
	static Stage stage;
	
	Stage splashStage;
	StackPane splashLayout;
	boolean splashControl=true;
	
	static boolean isResized=false;
	final private  String version ="Plotty - 0.9.1";
	
	@Override
	public void start(Stage splashStage) {
		this.splashStage=splashStage;
		initSplashWindow();
		initMainWindow();
	}
	
	public static void main(String[] args) {
		launch(args);

	}
	
	public void initSplashWindow(){		
		splashLayout=new StackPane();
		splashLayout.setAlignment(Pos.CENTER);				
		splashLayout.getChildren().add(new ImageView(new Image(getClass().getResourceAsStream("images/splashImage.png"))));		
		splashStage.initStyle(StageStyle.TRANSPARENT);				
		splashStage.setScene(new Scene(splashLayout,Color.TRANSPARENT));		
		splashStage.show();		
		
		splashLayout.setOnMouseClicked(e -> {				
			if(splashControl)//if mouse event was not fired before 
			if(e.getClickCount()==2)//if double click is fired
			if(e.getButton().equals(MouseButton.PRIMARY)){//if clicked button is rigth
				setupMainwindow();
				splashControl=false;
			}
		});		
	}
	
	public void initMainWindow(){	
		new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			splashLayout.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
	                0, 0, 0, MouseButton.PRIMARY, 2, true, true, true, true,
	                true, true, true, true, true, true, null));
		}).start();
	}
	
	public void setupMainwindow(){
		Platform.runLater(() -> {
			try {
				splashStage.hide();
				stage=new Stage();
				Parent root = FXMLLoader.load(getClass().getResource("Controller.fxml"));
				Scene scene = new Scene(root);				
				stage.setScene(scene);
				stage.setTitle(version);
				stage.setMinHeight(400);
				stage.setMinWidth(400);
				stage.setFullScreen(true);
				stage.show();
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
	}
	
	public static String getTime(){
		long totalSecond=System.currentTimeMillis()/1000;
		long currentSecond=totalSecond%60;
		long totalMinute=totalSecond/60;
		long currentMinute=totalMinute%60;
		long totalHour=totalMinute/60;
		long currentHour=totalHour%24;
		return currentHour+":"+currentMinute+":"+currentSecond+"-";
	}
}
