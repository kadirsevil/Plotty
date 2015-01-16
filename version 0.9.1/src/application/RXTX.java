package application;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;

public class RXTX {

	private String buffer="0";

	public ArrayList<String> portlist = new ArrayList<String>();
	public String connnectedPortName = "";

	private SerialPort serialPort;
	private Thread th_in;

	private BufferedWriter out;

	public RXTX() {

		// refresh portlist array
		new Thread(() -> {
			while (true) {
				refresh();
				try {
					Thread.sleep(170);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

	public String connect(String portName, int baudrate) throws Exception {

		if (portName == null)
			return Main.getTime()+"Port name is null!";
		else if(connnectedPortName.equals(portName))
			return Main.getTime()+"Already connected this port!";
		else if(portName.isEmpty())
			return Main.getTime()+"Port name is empty!";

		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

		if (portIdentifier.isCurrentlyOwned())
			return Main.getTime()+"Error: Port is currently in use!";
			
		else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

			if (commPort instanceof SerialPort) {
				serialPort = (SerialPort) commPort;

				serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				BufferedReader in = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream()));

				th_in = new Thread(new SerialReader(in));
				th_in.start();

				connnectedPortName = portName;
			}

			else
				return Main.getTime()+"Error: Only serial ports are handled by this example!";
		}
		
		return Main.getTime()+"Connected: Comm Port: " + portIdentifier.getName()+" Baudrate: "+baudrate;
	}

	public class SerialReader implements Runnable {
		BufferedReader in;

		public SerialReader(BufferedReader in) {
			this.in = in;
		}

		public void run() {

			while (buffer != null) {
				try {
					while ((buffer = in.readLine()) != null);
				} catch (IOException e) {
				}
			}
		}
	}

	public String disconnect() {
		if (!connnectedPortName.isEmpty()) {
			th_in=null;
			serialPort.close();
			String temp=connnectedPortName;
			connnectedPortName="";
			return Main.getTime()+"Disconnected: " + temp;
		}
		
		return Main.getTime()+"There is not any connection!";
	}

	public void SerialWriter(String buffer) {
		if (out == null)
			return;

		try {
			out.write(buffer);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// Refresh port names
	public void refresh() {
		Enumeration enum_portlist = CommPortIdentifier.getPortIdentifiers();

		portlist.clear();
		while (enum_portlist.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) (enum_portlist
					.nextElement());
			portlist.add(portId.getName());
		}
	}

	public String getBufferInstance() {
		return this.buffer;
	}

}