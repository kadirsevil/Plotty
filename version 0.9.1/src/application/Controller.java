package application;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;

public class Controller {
	RXTX rxtx = new RXTX();
	GraphDialog graphDialog;
	AboutDialog aboutDialog;
	Graph graph;

	/**
	 * for control connnect button lighting
	 */
	boolean connectBLightingFlag=false;
	
	final String[] str_baudrates = { "300", "600", "1200", "2400", "4800",
			"9600", "14400", "19200", "28800", "38400", "57600", "115200" };
	final ToggleGroup tgroup_baudrates = new ToggleGroup();

	String selectedComPort = null;

	@FXML
	Menu menu_tools;

	@FXML
	Menu menu2_serialPort;

	@FXML
	Menu menu2_baudrate;

	@FXML
	MenuItem menuitem_exit;

	@FXML
	MenuItem menuitem_about;

	@FXML
	Button bt_connect;

	@FXML
	Button bt_disconnect;

	@FXML
	Button bt_addGraph;

	@FXML
	AnchorPane graphArea;

	@FXML
	SplitPane mainSplitPane;

	@FXML
	TextArea txta_console;

	@FXML
	TextField infoField;

	@FXML
	void initialize() {
		menuitem_exit.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));
		menuitem_about.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));
		menuitem_exit.setOnAction(e -> System.exit(0));
		menuitem_about.setGraphic(new ImageView(new Image(getClass()
				.getResourceAsStream("images/about_icon.png"))));
		menuitem_exit.setGraphic(new ImageView(new Image(getClass()
				.getResourceAsStream("images/exit_icon.png"))));
		txta_console.setText("Plotty - 0.9.1/Console\n");
		mainSplitPane.getDividers().get(0).setPosition(0.9);

		menuitem_about.setOnAction(e -> aboutDialog = new AboutDialog());
		bt_disconnect.setOnAction(e -> txta_console.appendText(rxtx.disconnect() + "\n"));

		refreshPortList();
		setBaudrates();
		initializeAddGraphButton();
		bindMainStage();

		graph = new Graph(graphArea, rxtx, infoField);

		initializeConnectButton();
		ConnectButtonLighting();
	}

	public void refreshPortList() {
		new Thread(() -> {
			while (true) {
				Platform.runLater(() -> {
					if (!menu2_serialPort.isShowing()) {
						menu2_serialPort.getItems().clear();

						if (rxtx.portlist.size() > 0) {
							menu2_serialPort.setDisable(false);
							CheckMenuItem temp = new CheckMenuItem(
									rxtx.portlist.get(0));
							menu2_serialPort.getItems().add(temp);
							if (selectedComPort != null
									&& selectedComPort.equals(rxtx.portlist
											.get(0)))
								((CheckMenuItem) (menu2_serialPort.getItems()
										.get(0))).setSelected(true);
							temp.selectedProperty().addListener(
									(ov, old, new_) -> {
										if (new_)
											selectedComPort = temp.getText();
										else
											selectedComPort = null;
									});
						}

						else {
							selectedComPort = null;
							menu2_serialPort.setDisable(true);
						}
					}
				});// end of run later
				try {
					Thread.sleep(200);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}// end of while loop
		}).start();
	}

	public void setBaudrates() {
		menu2_baudrate.getItems().clear();

		for (int i = 0; i < str_baudrates.length; i++) {
			RadioMenuItem radio = new RadioMenuItem(str_baudrates[i]);
			radio.setToggleGroup(tgroup_baudrates);
			menu2_baudrate.getItems().add(radio);
		}
	}

	public void initializeAddGraphButton() {
		bt_addGraph.setOnMouseClicked(e -> {
			if (graphDialog == null) {
				graphDialog = new GraphDialog();
				graphDialog.getGraphDialog();
				bt_addGraph.setDisable(true);

				graphDialog.getStage().setOnCloseRequest(
						e2 -> {
							bt_addGraph.setDisable(false);

							if (graphDialog.getLinesInfos().size() != 0) {
								graph.addGraph(graphDialog.getLinesInfos());

								txta_console.appendText(Main.getTime()
										+ "New graph added: \n");
								writeLinesInfoToConsole();
							}
							graphDialog = null;
						});// end of close request

			}// end of if

		});// end of add button click
	}

	public void bindMainStage() {
		Main.stage.setOnCloseRequest(e -> {
			if (graphDialog != null)
				graphDialog.getStage().close();
		});
	}

	public void writeLinesInfoToConsole() {
		for (Lines line : graphDialog.getListLines()) {
			ArrayList<Object> nodes = line.getNodeInfos();
			String lineName = (String) (nodes.get(0));// name
			String lineColor = (String) (nodes.get(1)).toString();// color
			String lineStartValue = (String) (nodes.get(2));// start value
			String lineEndValue = (String) (nodes.get(3));// end value
			String lineDataAddress = (String) (nodes.get(4));// data address
			txta_console.appendText(Main.getTime() + "Line: /Name: " + lineName
					+ " /Color: " + lineColor);
			txta_console.appendText(" /Start Value: " + lineStartValue
					+ " /End Value: " + lineEndValue + " /Data address: "
					+ lineDataAddress + "\n");
		}
	}

	public void initializeConnectButton() {
		bt_connect.setOnMouseClicked(e -> {
			boolean flag = true;
			Toggle selectedToggle = tgroup_baudrates.getSelectedToggle();

			if (selectedComPort == null) {
				txta_console.appendText(Main.getTime()+ "Communication port is not selected!\n");
				flag = false;
			}

			if (selectedToggle == null) {
				txta_console.appendText(Main.getTime()+ "Baudrate is not selected!\n");
				flag = false;
			}

			int selectedBaudrate = 0;
			if (flag) {
				int index = tgroup_baudrates.getToggles().indexOf(
						selectedToggle);
				selectedBaudrate = Integer.parseInt(menu2_baudrate.getItems()
						.get(index).getText());
				try {
					String returnedValue=rxtx.connect(selectedComPort,selectedBaudrate);
					if(returnedValue.contains(Integer.toString(selectedBaudrate)))
						connectBLightingFlag=true;
					
					txta_console.appendText(returnedValue+ "\n");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		});
	}

	public void ConnectButtonLighting() {
		
		
		new Thread(() -> {
			while (true) {			
				if(connectBLightingFlag)
				Platform.runLater(() -> {
					bt_connect.setStyle("-fx-background-color: #93F47E");
				});

				try {
					Thread.sleep(500);
				} catch (Exception e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Platform.runLater(() -> {
				bt_connect.setStyle("-fx-background-color: #ECECEC");
			});

			try {
				Thread.sleep(300);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}	).start();
	}

}
