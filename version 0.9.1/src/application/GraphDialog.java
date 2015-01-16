package application;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class GraphDialog {
	private final int width = 785;
	private final int height = 250;
	
	private ArrayList<Lines> list_lines = new ArrayList<Lines>();
	private Lines fixedLine;
	
	/**
	 * name
	 * color
	 * start value
	 * end value
	 * data address
	 */
	private ArrayList<ArrayList<Object>> linesInfos=new ArrayList<ArrayList<Object>>();
	
	private Stage stage;
 
	private final Button bt_save=new Button("Save", new ImageView(new Image(getClass().getResourceAsStream("images/save_button.png"))));
	private final Button bt_clear=new Button("Clear",new ImageView(new Image(getClass().getResourceAsStream("images/clear_button.png"))));
	
	private HBox hBox_buttons;
	private VBox vbox;
	
	public void getGraphDialog() {		
		
		fixedLine=new Lines();
		fixedLine.getRemoveNode().setDisable(true);
		
		hBox_buttons=new HBox();
		hBox_buttons.setAlignment(Pos.CENTER_RIGHT);
		hBox_buttons.setPadding(new Insets(10));
		hBox_buttons.setSpacing(10);
		hBox_buttons.getChildren().addAll(bt_clear,bt_save);
		
		vbox = new VBox();
		vbox.getChildren().add(fixedLine);
		vbox.getChildren().add(hBox_buttons);
		
		BorderPane bpane = new BorderPane();
		bpane.setCenter(vbox);	
		
		ScrollPane root = new ScrollPane();
		root.setContent(bpane);
	
		Scene scene = new Scene(root, width, height);

		stage = new Stage();
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(Main.stage.getScene().getWindow());
		stage.setScene(scene);
		stage.setTitle("New Graph");
		stage.setResizable(false);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("images/selectGraphImage.png")));		
		stage.show();
		initializeAddButton();
		initializeClearButton();
		initializeSaveButton();
		
		
	}//end of getGraphDialog method
	
	private void transferValues(Lines from, Lines to){
		ArrayList<Object> fromInfo=from.getNodeInfos();
		ArrayList<Object> toInfo=to.getNodes();
		
		((TextField)(toInfo.get(0))).setText(((String)(fromInfo.get(0))).trim());//name
		((ColorPicker)(toInfo.get(1))).setValue(((Color)(fromInfo.get(1))));//color
		((TextField)(toInfo.get(2))).setText(((String)(fromInfo.get(2))).trim());//start value
		((TextField)(toInfo.get(3))).setText(((String)(fromInfo.get(3))).trim());//end value
		((TextField)(toInfo.get(4))).setText(((String)(fromInfo.get(4))).trim());//data address		
	}
	
	private void resetValues(Lines lines, boolean isClearing){
		ArrayList<Object> nodes=lines.getNodes();	

		((TextField)(nodes.get(0))).setText("default");//name
		((ColorPicker)(nodes.get(1))).setValue(Color.YELLOW);//color
		((TextField)(nodes.get(2))).setText("100");//start value
		((TextField)(nodes.get(3))).setText("-100");//end value
		
		/**
		 * -at the beginning data address node has the
		 * value of 0, on each increment of line
		 * added 1 to the this value
		 * -but if clear button is clicked -so isClearing is true-
		 * this value become 0
		 */
		int dataNo=Integer.parseInt(((TextField)(nodes.get(4))).getText());
		
		if(isClearing)
			dataNo=-1;		
		((TextField)(nodes.get(4))).setText(Integer.toString(++dataNo));//data address
	}
	
	private void initializeRemoveButton(int index){
		list_lines.get(index).getRemoveNode().setOnMouseClicked(e2 -> {
			Platform.runLater(() -> {
				for(int i=0;i<list_lines.size();i++)
				if(e2.getSource()==list_lines.get(i).getRemoveNode()){
					vbox.getChildren().remove(list_lines.get(i));
					list_lines.remove(list_lines.get(i));
				}				
			});
		});
	}
	
	private void initializeAddButton(){
		fixedLine.getAddNode().setOnMouseClicked(e1 -> {
			//Controls if entered values is convenience to add
			if(isConvenienceToAdd())
			Platform.runLater(() -> {				
				Lines tempLine=new Lines();
				list_lines.add(tempLine);		
				ArrayList<Object> addedLine=tempLine.getNodes();
				setLineNodesProp(addedLine);
				tempLine.getAddNode().setDisable(true);				
				transferValues(fixedLine, tempLine);
				resetValues(fixedLine, false);
				vbox.getChildren().add(list_lines.size(), tempLine);
				initializeRemoveButton(list_lines.size()-1);				
			});	//end of run later
		});//end of add click
	}
	
	private void setLineNodesProp(ArrayList<Object> addedLine){
		String nodesColor="#93F47E";
		((TextField)(addedLine.get(0))).setEditable(false);//name node
		((TextField)(addedLine.get(2))).setEditable(false);//start value node
		((TextField)(addedLine.get(3))).setEditable(false);//end value node
		((TextField)(addedLine.get(4))).setEditable(false);//data address node
		((TextField)(addedLine.get(0))).setStyle("-fx-background-color: "+nodesColor);//color of name node
		((TextField)(addedLine.get(2))).setStyle("-fx-background-color: "+nodesColor);//color of start value node
		((TextField)(addedLine.get(3))).setStyle("-fx-background-color: "+nodesColor);//color of end value node
		((TextField)(addedLine.get(4))).setStyle("-fx-background-color: "+nodesColor);//color of data address node
		((ColorPicker)(addedLine.get(1))).setStyle("-fx-background-color: "+nodesColor);
	}
	
	private void initializeClearButton(){
				bt_clear.setOnMouseClicked(e -> {				
					Platform.runLater(() -> {
						Lines temp1=fixedLine;
						HBox temp2=(HBox)(vbox.getChildren().get(vbox.getChildren().size()-1));	
						vbox.getChildren().clear();
						vbox.getChildren().addAll(temp1, temp2);
						resetValues(temp1, true);
						list_lines.clear();
					});
				});
	}

	private void initializeSaveButton(){		
		bt_save.setOnMouseClicked(e -> {		
			linesInfos.clear();
			
			for(Lines i:list_lines)
				linesInfos.add(i.getNodeInfos());
				
			if(linesInfos.size()>0)
			stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
			
		});		
	}
		
	public boolean isNumeric(String str){
		char[] temp=str.toCharArray();
		
		if(temp.length==1 && !Character.isDigit(temp[0]))
			return false;
		
		if(temp.length>1 && !(Character.isDigit(temp[0]) || temp[0]=='-'))
			return false;		

		for (int i=1;i<temp.length;i++)
	        if (!Character.isDigit(temp[i])) 
	        	return false;

	    return true;
	}
	
	public boolean isConvenienceToAdd(){
		ArrayList<Object> temp=fixedLine.getNodeInfos();
		
		if(((String)temp.get(0)).isEmpty())//line name is empty
			return false;
		
		if(((String)temp.get(2)).isEmpty())//start value is emtpy
			return false;
		
		if(((String)temp.get(3)).isEmpty())//end value is emtpy
			return false;
				
		if(((String)temp.get(4)).isEmpty())//data address is empty
			return false;
		
		if(!isNumeric(((String)temp.get(2)).trim()))//start value is not numeric
			return false;
		
		if(!isNumeric(((String)temp.get(3)).trim()))//end value is not numeric
			return false;		
		
		if(!isNumeric(((String)temp.get(4)).trim()))//data address is not numeric
			return false;
		
		return true;
	}
	
	
	/*Getter methods*/	
	public ArrayList<Lines> getListLines(){
		return this.list_lines;
	}
	
	public ArrayList<ArrayList<Object>> getLinesInfos(){
		return linesInfos;
	}
	
	public Stage getStage() {
		return this.stage;
	}
	

	
	
}
