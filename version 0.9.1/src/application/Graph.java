package application;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Graph {
	private AnchorPane apane;
	private TextField infoField;
	private Thread thread;

	private RXTX rxtx;

	private int grillGapX = 50;
	private int grillGapY = 50;

	private long time;
	private long counter;

	private int graphGap = 1;
	private int dataSize;

	private int gWidth, gHeight;
	private int prevGWidth, prevGHeight;

	private int[] buffer;

	int graphSizeX, graphSizeY;
	int[] graphOffsets = { 40, 20, 70, 10 };// [ top bottom left right]

	ArrayList<OneGraph> graphs = new ArrayList<Graph.OneGraph>();
	Grill grill=new Grill();

	private int numberOfLineSegment = 0;

	private boolean newGraphAddedControl = false;

	public Graph(AnchorPane apane, RXTX rxtx, TextField infoField) {
		this.apane = apane;
		this.rxtx = rxtx;
		this.infoField = infoField;
		prepareThread();
	}

	public void prepareThread() {
		thread = new Thread(() -> {

			time = System.currentTimeMillis();

			while (true) {

				Platform.runLater(() -> {

					updatePaneSize();
					updateGraphSize();
					updateDataSize();
					updateBuffer();
					grill.printGrill();

					for (int i = 0; i < graphs.size(); i++)
						graphs.get(i).updateGraph();

					/**
					 * this block calculates refresh rate of thread
					 */
					counter++;
					if (System.currentTimeMillis() - time > 300) {
						infoField.setText("Refresh Rate: " + (int) (counter / ((double) (System.currentTimeMillis() - time) / 1000)));
						time = System.currentTimeMillis();
						counter = 0;
					}

				});

				// for(long i=0;i<100000;i++);//delay
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void addGraph(ArrayList<ArrayList<Object>> linesInfos) {

		graphs.add(new OneGraph(linesInfos));
		newGraphAddedControl = true;

		if (graphs.size() == 1)
			thread.start();
	}

	class OneGraph {
		private ArrayList<OneLine> lines = new ArrayList<Graph.OneLine>();

		private ArrayList<ArrayList<Object>> linesInfos=new ArrayList<ArrayList<Object>>();
		
		int graphIndex;

		public OneGraph(ArrayList<ArrayList<Object>> linesInfos) {	
			
			this.linesInfos=linesInfos;
			
			this.graphIndex = graphs.size();
			// create new lines with its infos
			for (ArrayList<Object> temp : linesInfos)
				lines.add(new OneLine(graphIndex, lines.size(), temp));

		}

		public void updateGraph() {
			for (int i = 0; i < lines.size(); i++)
				lines.get(i).printDataAdding();
		}
		
		public ArrayList<ArrayList<Object>> getLinesInfos(){
			return this.linesInfos;
		}

	}

	class OneLine {
		private int prevData;

		private Line line;

		private int graphIndex;

		private int lineIndex;

		private String name;
		private Color color;
		private int sValue;
		private int eValue;
		private int dataAddress;

		public OneLine(int graphIndex, int lineIndex, ArrayList<Object> lineInfo) {
			this.graphIndex = graphIndex;
			this.lineIndex = lineIndex;
			this.name = (String) (lineInfo.get(0));
			this.color = (Color) (lineInfo.get(1));
			this.sValue = Integer.parseInt((String) (lineInfo.get(2)));
			this.eValue = Integer.parseInt((String) (lineInfo.get(3)));
			this.dataAddress = Integer.parseInt((String) (lineInfo.get(4)));
		}

		private void printDataAdding() {

			/**
			 * only first line of first graph can set to zero
			 * #numberoflinesegment
			 */
			if (numberOfLineSegment >= dataSize && graphIndex == 0 && lineIndex == 0)
				numberOfLineSegment = 0;

			int currentData = buffer[dataAddress];

			/**
			 * fits data to the graph
			 */
			currentData = (int) (graphSizeY* ((double) Math.abs(currentData - eValue)) / Math.abs(sValue - eValue));

			/**
			 * only first line of first graph can increment #numberoflinesegment
			 */
			if (graphIndex == 0 && lineIndex == 0)
				numberOfLineSegment++;

			int j = numberOfLineSegment - 1;

			int x1 = (j - 1) * graphGap + graphOffsets[2];
			int y1 = (graphIndex + 1)* (graphSizeY + graphOffsets[0] + graphOffsets[1])- graphOffsets[1] - prevData;
			int x2 = j * graphGap + graphOffsets[2];
			int y2 = (graphIndex + 1)* (graphSizeY + graphOffsets[0] + graphOffsets[1])- graphOffsets[1] - currentData;

			line = new Line(x1, y1, x2, y2);

			prevData = currentData;
			line.setStroke(color);
			line.setStrokeWidth(2);
			apane.getChildren().add(line);
			line = null;

		}

	}

	class Grill{
		
		/**
		 *for smart scaling field
		 */
		boolean flag=false, flag2=true;
		int incrementSize=0, residue=0;
		
		/**
		 *for line drawing 
		 */
		int j;
		int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
		
		
		/**
		 * for printing border values
		 */
		int sValue, eValue;
		
		
		ArrayList<ArrayList<ArrayList<Object>>> wholeLinesInfos=new ArrayList<ArrayList<ArrayList<Object>>>();
				
		private void printGrill() {
			
			/**
			 * grill is reprinted under this conditions:
			 * if
			 * -number of line segment exceeds data size
			 * -new graph is added
			 * -window size is changed
			 */
			if (numberOfLineSegment >= dataSize || newGraphAddedControl || prevGWidth != gWidth || prevGHeight != gHeight) {

				wholeLinesInfos.clear();
				for(OneGraph oneGraph:graphs)
					wholeLinesInfos.add(oneGraph.getLinesInfos());
				
				Line line;
				
				apane.getChildren().clear();
				
				/**
				 * remove unused objects
				 * from the heap space
				 */
				System.gc();

				/**
				 * prints whole grill
				 */
				for (int i = 0; i < graphs.size(); i++) {
					sValue=Integer.parseInt((String)(wholeLinesInfos.get(0).get(0).get(2)));
					eValue=Integer.parseInt((String)(wholeLinesInfos.get(0).get(0).get(3)));
					
					/**
					 * prints vertical grills
					 */
					initSmartScaling("y");//y axis
					for (j = 0; j <= graphSizeX; j += grillGapX) {

						if(j!=0 && flag){
							if(flag2 && (residue-=incrementSize)>=0)
							j+=incrementSize;
							
							else if(!flag2 && (residue+=incrementSize)<=grillGapX)						 
								j-=incrementSize;						
						}
						
						x1 = j + graphOffsets[2];
						y1 = i * (graphSizeY + graphOffsets[0] + graphOffsets[1]) + graphOffsets[0];
						x2 = j + graphOffsets[2];
						y2 = (i + 1) * (graphSizeY + graphOffsets[0] + graphOffsets[1]) - graphOffsets[1];

						line = new Line(x1, y1, x2, y2);
						if(j==0 || j==graphSizeX){
						line.setStrokeWidth(5);
						line.setStroke(Color.GREY);
						}
						else
							line.setStrokeWidth(0.3);
						apane.getChildren().add(line);

						/**
						 * print last line if
						 * current line is not on border
						 * and next line will not be printed
						 * due to j>border
						 */
						if (j + grillGapX > graphSizeX) {
							j = graphSizeX;
							x1 = j + graphOffsets[2];
							y1 = i * (graphSizeY + graphOffsets[0] + graphOffsets[1]) + graphOffsets[0];
							x2 = j + graphOffsets[2];
							y2 = (i + 1) * (graphSizeY + graphOffsets[0] + graphOffsets[1]) - graphOffsets[1];

							line = new Line(x1, y1, x2, y2);
							line.setStrokeWidth(5);
							line.setStroke(Color.GREY);
							apane.getChildren().add(line);
							break;
						}//end of last grill

					}//end of vertical for loop

					
					/**
					 * prints horizontal grills
					 */
					initSmartScaling("x");//x axis
					for (j = 0; j <= graphSizeY; j += grillGapY) {
						
						if(j!=0 && flag){
								if(flag2 && (residue-=incrementSize)>=0)
								j+=incrementSize;
								
								else if(!flag2 && (residue+=incrementSize)<=grillGapY)						 
									j-=incrementSize;						
						}
						
						x1 = graphOffsets[2];
						y1 = j + i * (graphSizeY + graphOffsets[0] + graphOffsets[1]) + graphOffsets[0];
						x2 = graphSizeX + graphOffsets[2];				
						y2 = j + i * (graphSizeY + graphOffsets[0] + graphOffsets[1]) + graphOffsets[0];
						
						line = new Line(x1, y1, x2, y2);
						if(j==0 || j==graphSizeY){
							line.setStrokeWidth(5);
							line.setStroke(Color.GREY);
						}
							else
								line.setStrokeWidth(0.3);
						apane.getChildren().add(line);
						
						/**
						 * prints border values						
						 */
						apane.getChildren().add(printNumbers(x1, y1, j));
						

						/**
						 * print last line if
						 * current line is not on border
						 * and next line will not be printed
						 * due to j>border
						 */
						if (j + grillGapY > graphSizeY) {
							j = graphSizeY;
							x1 = graphOffsets[2];
							y1 = j + i * (graphSizeY + graphOffsets[0] + graphOffsets[1]) + graphOffsets[0];
							x2 = graphSizeX + graphOffsets[2];
							y2 = j + i * (graphSizeY + graphOffsets[0] + graphOffsets[1]) + graphOffsets[0];

							line = new Line(x1, y1, x2, y2);
							line.setStrokeWidth(5);
							line.setStroke(Color.GREY);
							apane.getChildren().add(line);
							
							/**
							 * prints border values						
							 */
							apane.getChildren().add(printNumbers(x1, y1, j));
							
							break;
						}//end of last grill
					}//end of horizontal for loop
				}//end of main for
				
				numberOfLineSegment = 0;
				newGraphAddedControl = false;
			}//end of if statement

		}
		
		private Text printNumbers(int x1, int y1, int j){
			double value=sValue-((double)j/graphSizeY)*Math.abs(sValue-eValue);//determine scalled value
			value=(int)(value*100)/100.0;//round up two decimal 1.25512 -> 1.25
			String str=Double.toString(value);
			
			Text text=new Text();
			text.setText(str);
			float width = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(str, text.getFont());
			float height = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().getFontMetrics(text.getFont()).getLineHeight();
			
			text.setX(x1-width-10);
			text.setY(y1+height/2-5);						
			text.setFont(new Font(grillGapY*0.3));
			text.setStroke(Color.GREY);
			
			return text;
		}
	
		/**
		 * @param axis
		 * This function determines incrementsize that
		 * adjust grill rectangle to fit it in graph area
		 */
		private void initSmartScaling(String axis){
			
			if(axis.equals("y")){
				residue=graphSizeX%grillGapX;
				if(residue!=0){
					flag=true;
					incrementSize=residue/(graphSizeX/grillGapX)+1;
					
					if(grillGapX/2>residue)
						flag2=true;
					else
						flag2=false;
				}
			}//end of y axis			
			else if(axis.equals("x")){
				residue=graphSizeY%grillGapY;
				if(residue!=0){
					flag=true;
					incrementSize=residue/(graphSizeY/grillGapY)+1;
					
					if(grillGapY/2>residue)
						flag2=true;
					else
						flag2=false;
				}
			}//end of x axis			
		}//end of initSmartScaling function
				
	}//end of Grill class
	

	private void updatePaneSize() {
		prevGWidth = gWidth;
		prevGHeight = gHeight;
		gWidth = (int) apane.getWidth();
		gHeight = (int) apane.getHeight();
	}

	private void updateGraphSize() {
		graphSizeX = gWidth - graphOffsets[2] - graphOffsets[3];
		graphSizeY = gHeight / graphs.size() - graphOffsets[0]- graphOffsets[1];
	}

	private void updateDataSize() {
		dataSize = graphSizeX / graphGap;
	}

	private void updateBuffer() {
		String[] e = rxtx.getBufferInstance().split("\t");
		buffer = new int[e.length];
		for (int i = 0; i < e.length; i++)
			buffer[i] = Integer.parseInt(e[i]);
	}

}
