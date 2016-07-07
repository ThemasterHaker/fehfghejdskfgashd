

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JLabel;

public class ServerGUI extends JFrame implements ActionListener{

	private static final long serialVersionUID = 3115405040448162167L;
	
	//size of frame
	private int width = 200;
	private int height = 225;
	
	//size of buttons
	private int btnWidth = 150;
	private int btnHeight = 40;
	
	//size of text field and label
	private int tfWidth = 60;
	private int tfHeight = 30;
	
	//the actual window
	private JPanel Window;
		
	//port stuff
	public JTextField portTextField;
	private JLabel portLabel;
	
	//server name stuff
	public JTextField serverNameField;
	private JLabel serverNameLabel;
		
	//more components
	public JButton startButton;
	public JButton stopButton;
	
	public Server server;
	
	public String defaultName = "CS342 Chat Room";
	public String defaultPort = "1337";
	
	public boolean running;

	/*
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI frame = new ServerGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ServerGUI() {
		setTitle("Chat Server");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(700, 400, width, height);
		
		//set up the JPanel
		Window = new JPanel();
		Window.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(Window);
		Window.setLayout(null);
		
		//server name label
		serverNameLabel = new JLabel("Server Name");
		serverNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		serverNameLabel.setBounds((width/2 - 100/2), 0, 100, tfHeight);
		Window.add(serverNameLabel);
		
		//set up the port text field
		serverNameField = new JTextField();
		serverNameField.setText(defaultName);
		serverNameField.setHorizontalAlignment(SwingConstants.CENTER);
		serverNameField.setBounds( (width/2 - 60), 32, 120, tfHeight);
		Window.add(serverNameField);
		
		//port label
		portLabel = new JLabel("Port");
		portLabel.setHorizontalAlignment(SwingConstants.CENTER);
		portLabel.setBounds((width/2 - tfWidth/2), 60, tfWidth, tfHeight);
		Window.add(portLabel);
		
		//set up the port text field
		portTextField = new JTextField();
		portTextField.setText(defaultPort);
		portTextField.setHorizontalAlignment(SwingConstants.CENTER);
		portTextField.setBounds( (width/2 - tfWidth/2), 85, tfWidth, tfHeight);
		Window.add(portTextField);
		
		startButton = new JButton("Start");
		startButton.setBounds((width/2 - btnWidth/2), 125, btnWidth, btnHeight);
		startButton.addActionListener(this);
		Window.add(startButton);
		
		stopButton = new JButton("Stop");
		stopButton.setBounds((width/2 - btnWidth/2), 175, btnWidth, btnHeight);
		stopButton.setEnabled(false);
		stopButton.addActionListener(this);
		Window.add(stopButton);
		

	}
	
	public void actionPerformed(ActionEvent e){
		
		Object component = e.getSource();
		
		//start button
		if(component.equals(startButton)){
		
			int port = Integer.parseInt(portTextField.getText());
			
			server = new Server(port, serverNameField.getText(), this);
			
			server.start();
			
		}
		else if(component.equals(stopButton)){
			
			server.stop();
		}
		
	}//end actionPerformed

	
}
