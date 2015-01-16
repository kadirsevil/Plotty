package application;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class Lines extends HBox {
	private final TextField name = new TextField();
	private final ColorPicker color = new ColorPicker();
	private final TextField startValue = new TextField();
	private final TextField endValue = new TextField();
	private final TextField dataAddress=new TextField();
	
	private final Button bt_add = new Button("Add",new ImageView(new Image(getClass().getResourceAsStream("images/add_button.png"))));
	private final Button bt_remove = new Button("Remove",new ImageView(new Image(getClass().getResourceAsStream("images/remove_button.png"))));

	private ArrayList<Object> nodes=new ArrayList<Object>();
	
	public Lines() {
		name.setPromptText("Line name");
		name.setPrefWidth(100);
		name.setText("default");

		color.setPrefWidth(125);
		color.setValue(Color.YELLOW);

		startValue.setPromptText("Start Value");
		startValue.setPrefWidth(100);
		startValue.setText("100");
		
		endValue.setPromptText("End Value");
		endValue.setPrefWidth(100);
		endValue.setText("-100");

		dataAddress.setPromptText("Data Address");
		dataAddress.setText("0");
		dataAddress.setPrefWidth(100);
		
		this.getChildren().addAll(name, color, startValue, endValue, dataAddress, bt_add, bt_remove);
		this.setSpacing(10);
		this.setPadding(new Insets(10));

		nodes.add(name);
		nodes.add(color);
		nodes.add(startValue);
		nodes.add(endValue);
		nodes.add(dataAddress);
	}

	//Getter methods
	public ArrayList<Object> getNodes(){
		return this.nodes;
	}
	
	public ArrayList<Object> getNodeInfos(){
		ArrayList<Object> temp=new ArrayList<Object>();
		temp.add(name.getText());
		temp.add(color.getValue());
		temp.add(startValue.getText());
		temp.add(endValue.getText());
		temp.add(dataAddress.getText());		
		return temp;
	}
	
	public Button getAddNode(){
		return bt_add;
	}
	
	public Button getRemoveNode(){
		return bt_remove;
	}

	
	// Setter methods
	public void setColor(Color color) {
		this.color.setValue(color);
	}
}
