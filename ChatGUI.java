
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class ChatGUI extends JFrame implements ActionListener{

	private static final long serialVersionUID = -1408372546989605762L;
	
	//size of frame
	private int width = 600;
	private int height = 500;
	
	//size of buttons
	private int btnWidth = 120;
	private int btnHeight = 30;
	
	//size of message field
	private int mfWidth = 450;
	private int mfHeight = 30;
	
	//the actual window
	private JPanel Window;
	
	//where user enters the port number
	public static JTextField messageField;
	
	//chat log
	private JTextArea chatLog;
	
	//login stuff
	private JButton loginButton;
	public JTextField nameField;
	public JTextField addressField;
	public JTextField portField;
	private JLabel nameLabel;
	private JLabel portLabel;
	private JLabel addressLabel;
	
	//user list
	public ArrayList<JLabel> userNames = new ArrayList<JLabel>();
	public ArrayList<JCheckBox> userSelect = new ArrayList<JCheckBox>();
	
	public JLabel everyoneLabel;
	public JCheckBox everyoneCheck;
	
	//send message button
	public JButton messageButton;
	
	//the client that is asociated with this gui
	public ChatClient client;
	
	public boolean connected = false;
	
	//the wonderful idea of a default
	//port and address because im tired 
	//of re-entering localhost a million times
	
	private String defaultAddress = "localhost";
	private String defaultPort = "1337";
	
	
	/*
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatGUI frame = new ChatGUI();
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
	public ChatGUI() {
		setTitle("Chat Client");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(700, 400, width, height);
		
		//set up the JPanel
		Window = new JPanel();
		Window.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(Window);
		Window.setLayout(null);
		
		//set up the message field
		messageField = new JTextField();
		messageField.setHorizontalAlignment(SwingConstants.LEFT);
		messageField.setBounds( 10, (height-10-mfHeight), mfWidth, mfHeight);
		Window.add(messageField);
		messageField.setColumns(6);
		
		//the send button
		messageButton = new JButton("Send");
		messageButton.setBounds( (20+mfWidth), (height-10-mfHeight), btnWidth, btnHeight);
		messageButton.addActionListener(this);
		Window.add(messageButton);
		
		//the chat log
		chatLog = new JTextArea();
		chatLog.setBounds(10, 40, mfWidth, (height - 60 - mfHeight) );
		chatLog.setEditable(false);
		Window.add(chatLog);
		
		//login stuff
		addressLabel = new JLabel("Address:");
		addressLabel.setBounds(10, 13, 65, 15);
		Window.add(addressLabel);
		
		addressField = new JTextField();
		addressField.setText(defaultAddress);
		addressField.setBounds(75, 8, 100, 25);
		Window.add(addressField);
		
		portLabel = new JLabel("Port:");
		portLabel.setBounds(185, 13, 40, 15);
		Window.add(portLabel);
		
		portField = new JTextField();
		portField.setText(defaultPort);
		portField.setBounds(225, 8, 50, 25);
		Window.add(portField);
		
		nameLabel = new JLabel("Username:");
		nameLabel.setBounds(285, 13, 80, 15);
		Window.add(nameLabel);
		
		nameField = new JTextField();
		nameField.setBounds(370, 8, 100, 25);
		Window.add(nameField);
		
		loginButton = new JButton("Login");
		loginButton.setBounds(485, 8, 100, 25);
		loginButton.addActionListener(this);
		Window.add(loginButton);
		

	}
	
	// display messages on text area
	void append(String str) {
		chatLog.append(str + "\n");
	}
	
	public void addUser(String name){
		
		if(userNames.size() < 19){
						
			userNames.add(new JLabel(name, SwingConstants.LEFT));
			
			int top = userNames.size() -1;
			
			userNames.get(top).setBounds( (45+mfWidth), (70 + (top*20) ), 100, 20);
			Window.add(userNames.get(top));
			
			userSelect.add(new JCheckBox());
			userSelect.get(top).setBounds( (15+mfWidth), (70 + (top*20) ), 20, 20);
			Window.add(userSelect.get(top));
		
			Window.repaint();
		}
	}
	
	//handler for the different components
	public void actionPerformed(ActionEvent e){
		
		Object component = e.getSource();
		
		//if user tried logging in
		if(component == loginButton && !connected){
			
			String name = nameField.getText();
			String host = addressField.getText();
			int port = Integer.parseInt(portField.getText());
			
			//start client
			if( nameCheck(name) && !host.equals("") && (port > 50) ){
				client = new ChatClient(host, port, name, this );
				client.start();
				
				//if it worked fine, disable all login related components
				if(connected){
					nameField.setEnabled(false);
					portField.setEnabled(false);
					addressField.setEnabled(false);
					
					//send to everyone
					everyoneCheck = new JCheckBox();
					everyoneCheck.setBounds(15 + mfWidth, 40, 20, 20);
					Window.add(everyoneCheck);
					
					everyoneLabel = new JLabel("Everyone");
					everyoneLabel.setBounds( (45+mfWidth), 40, 100, 20);
					Window.add(everyoneLabel);
					
					//change button to logout
					loginButton.setText("Logout");
					
				}
					
			}
	
		}//end login
		
		//logout 
		//if user tried logging in
		else if(component == loginButton && connected){
			
			nameField.setText("");
			portField.setText(defaultPort);
			addressField.setText(defaultAddress);
			
			nameField.setEnabled(true);
			portField.setEnabled(true);
			addressField.setEnabled(true);
			
			//change button to login
			loginButton.setText("Login");
			client.disconnect();
			connected = false;
			
			//delete the array lists
			for(int j=0; j < userSelect.size(); j++){
				
				Window.remove(userSelect.get(j));
				Window.remove(userNames.get(j));
				
				userSelect.remove(j);
				userNames.remove(j);
				
			}
			
			//have to do this because for some reason
			// the loop never gets to the last index
			Window.remove(userSelect.get(0));
			Window.remove(userNames.get(0));
			userSelect.remove(0);
			userNames.remove(0);
			
			Window.repaint();
			
			append("Logged out of " + client.serverName);
			

	
		}//end login
		//if user is connected and send button pressed
		else if(connected && component == messageButton){
			//create new message object and send it through client
			
			boolean sendToEveryone = everyoneCheck.isSelected();
			
			ArrayList<Boolean> whoToSendTo = new ArrayList<Boolean>();
			
			for(int i=0; i < userSelect.size(); i++){
				
				whoToSendTo.add( (Boolean) userSelect.get(i).isSelected() );
			}
			
			
			client.sendMessage(new ChatMessage(messageField.getText(), whoToSendTo, sendToEveryone ));
		
			//reset the text in the message box
			messageField.setText("");
			return;
		}
		
	}
	
	public boolean nameCheck(String name){
		if( !name.equals("") ){
			
			if(name.length() < 9){
				return true;
			}
			else{
				JOptionPane.showMessageDialog(
						getParent(),
					    "Max name length is 9 characters.",
					    "Username Error",
					    JOptionPane.ERROR_MESSAGE);
				return false;
			}
				
		}else{
			JOptionPane.showMessageDialog(
					getParent(),
				    "Please enter a username.",
				    "Login Error",
				    JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
}
