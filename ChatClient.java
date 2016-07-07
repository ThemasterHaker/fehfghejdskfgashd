

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*
 * Chat Client code for cs342.
 * the base server and client code
 * is based off of the examples shown at
 * http://www.dreamincode.net/forums/topic/262304-simple-client-and-server-chat-program/
 * 
 */
import java.net.Socket;

public class ChatClient  {

	// for I/O
	private ObjectInputStream messageIn;		// to read from the socket
	private ObjectOutputStream messageOut;		// to write on the socket
	private Socket socket;

	// if I use a GUI or not
	private ChatGUI gui;
	
	// the server, the port and the username
	private String server, username;
	private int port;
	
	public String serverName;

	ChatClient(String server, int port, String username, ChatGUI gui) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.gui = gui;
		
	}
	
	
	public boolean start() {
		//connect to the server
		try {
			socket = new Socket(server, port);
		} 
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		//let gui know it all worked out
		gui.connected = true;
	
		// set up streams
		try
		{
			messageIn  = new ObjectInputStream(socket.getInputStream());
			messageOut = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		
		//first thing to send is the client's username
		try
		{
			messageOut.writeObject(username);
		}
		catch (IOException e) {
			e.printStackTrace();
			disconnect();
			return false;
		}
		// success we inform the caller that it worked
		return true;
	}
	
	/*
	 * To send a message to the server
	 */
	void sendMessage(ChatMessage msg) {
		try {
			messageOut.writeObject(msg);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	//close the connection
	public void disconnect() {
		try { 
			if(messageIn != null) messageIn.close();
		}
		catch(Exception e) {}
		try {
			if(messageOut != null) messageOut.close();
		}
		catch(Exception e) {}
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {}
			
	}
	
	class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
					
					//get the message as a string
					String msg = (String) messageIn.readObject();				
					
					
					//if the message sent has <<> as the first three chars
					//then it is meant to be the server name
					if(msg.toLowerCase().contains("<<>")){
						
						serverName = msg.substring(3);
						gui.append("Connected to: " + serverName);
					}
					
					// if the message sent has >>< as the first three
					// chars, then it is meant to be a username
					// that should get updated to the gui, not displayed as a message
					else if(msg.toLowerCase().contains(">><")){
						
						String userToAdd = msg.substring(3);
						
						boolean repeatName = false;
												
						//check if name already exists
						for(int i=0; i < gui.userNames.size(); i++){
							
							//if user trying to add is within the list, set the flag
							if(userToAdd.equals(gui.userNames.get(i).getText() )){
								repeatName = true;
								break;
							}
							
						}
						
						//if name doesnt repeat 
						//and if its not same as user itself
						if(!repeatName)
							gui.addUser(userToAdd);
							
						
						
					}
					//otherwise, its a regular message
					else{
						gui.append(msg);

					}
		
				}
				catch(IOException e) {
					e.printStackTrace();
					break;
				}
				catch(ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
