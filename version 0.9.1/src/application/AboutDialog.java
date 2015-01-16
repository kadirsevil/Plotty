package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AboutDialog {
	
	private Stage stage;
	
	public AboutDialog(){
		stage=new Stage();
		
		StackPane root=new StackPane();
		root.setAlignment(Pos.CENTER);		
		root.getChildren().add(new ImageView(new Image(getClass().getResourceAsStream("images/aboutDialog.png"))));
		root.setStyle("-fx-background: #CFE3E9");
			
		Scene scene=new Scene(root);
		
		stage.initStyle(StageStyle.UNDECORATED);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(Main.stage.getScene().getWindow());		
		stage.setScene(scene);		
		stage.show();
		
		scene.setOnMouseClicked(e -> stage.close());
	}

}
