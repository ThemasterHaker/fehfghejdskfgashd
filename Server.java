

/*
 * Chat Server code for cs342.
 * the base server and client code
 * is based off of the examples shown at
 * http://www.dreamincode.net/forums/topic/262304-simple-client-and-server-chat-program/
 * 
 */


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JCheckBox;

public class Server {

	
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> clients;
	
	// port to listen to
	private int port;
	
	//name of server (DIFFERENT THAN HOSTNAME)
	public String name;
	
	// is the server running or not
	private boolean running;
	
	//array list to hold all the users online, along with
	//which user to send message to
	public ArrayList<Boolean> sendTo = new ArrayList<Boolean>();
	public ArrayList<String> users = new ArrayList<String>();
	
	//used to change start and stop buttons
	private ServerGUI gui;
	
	private ServerSocket serverSocket;
	
	//how many clients(index)
	private int numClients = -1;
	
	public Server(int port, String name, ServerGUI gui) {

		this.port = port;
		this.name = name;
		this.gui = gui;
		
		// ArrayList for the Client list
		clients = new ArrayList<ClientThread>();
	}
	
	public void start() {
		running = true;
		
		gui.startButton.setEnabled(false);
		gui.stopButton.setEnabled(true);
		gui.serverNameField.setEnabled(false);
		gui.portTextField.setEnabled(false);
		
		gui.running = true;

		try 
		{
			// server socket
			serverSocket = new ServerSocket(port);

			// waiting for connections until 
			//server should stop running
			while(running) 
			{
				//new client connects
				Socket socket = serverSocket.accept();

				//create a thread for the client
				ClientThread t = new ClientThread(socket);
				
				numClients++;
				t.clientNum = numClients;
				
				//add it to the list
				clients.add(t);							
				
				//update each client's list of other clients
				addUserToClients();
				
				//send the new client the server's name
				t.writeToClient(name, 2);
				
				//start the client's thread
				t.start();
			}
			
			stop();
		}
		// something went bad
		catch (IOException e) {
			e.printStackTrace();
		}
	}		

	//self explanatory
	protected void stop() {
		running = false;
		
		//if it gets to here, running flag is off
		//so server must stop
		try {
			
			//close the server socket
			serverSocket.close();
			
			for(int i = 0; i < clients.size(); ++i) {
				
				//get each client and close its streams and socket
				ClientThread t = clients.get(i);
				
				try {
					t.messageIn.close();
					t.messageOut.close();
					t.socket.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		gui.startButton.setEnabled(true);
		gui.stopButton.setEnabled(false);
		gui.running = false;
		gui.serverNameField.setEditable(true);
		gui.serverNameField.setText(gui.defaultName);
		gui.portTextField.setEditable(true);
		gui.portTextField.setText(gui.defaultPort);

	}


	// adds the username to the client's gui so it can show
	// all currently logged in users
	private synchronized void addUserToClients(){
		
		int size = clients.size();
		
		//each client gets each other client's name
		for(int i = 0; i < size; i++) {
			ClientThread clientToSendTo = clients.get(i);
						
			for(int j=0; j < size; j++){
				ClientThread clientToGetNameFrom = clients.get(j);
				clientToSendTo.writeToClient(clientToGetNameFrom.username, 1);
			}
			
		}//outer for
		
	}
	
	// the reverse logic was taken from the online example
	// because otherwise you'd have to decrement i for each
	// disconnect and this is a cooler way to do it
	//
	//send message to clients specified by the sender
	private synchronized void broadcast(String message,
										   ArrayList<Boolean> whoToSendTo,
										   boolean sendToEveryone)
	{
		
		//should send to every client
		if(sendToEveryone){
			for(int i =0; i < clients.size(); i++) {
				ClientThread t = clients.get(i);
				
				t.writeToClient(message, 0);
			
			}//end of for
		}
		
		//should send to only some clients
		else{
			for(int i =0; i < clients.size(); i++) {
				ClientThread t = clients.get(i);
				
				if(whoToSendTo.get(i))
					t.writeToClient(message, 0);
				
				if(t.clientNum == i){
					//t.writeToClient(message, 0);
				}
			
			}//end of for
			
		}

			

	}//end of broadcast

	// remove a client
	synchronized void remove(ClientThread thread) {
		
		//find the thread and remove it
		for(int i = 0; i < clients.size(); ++i) {
			
			if(thread.equals(clients.get(i))){
				clients.remove(i);
				break;
			}
		}
	}
	
	//inner class that will server as a thread for each client
	class ClientThread extends Thread {
		
		Socket socket;
		
		//object streams
		ObjectInputStream messageIn;
		ObjectOutputStream messageOut;

		//the client's username
		String username;
		
		//message object
		ChatMessage chatMessage;
		
		//which client is it
		int clientNum;
		

		ClientThread(Socket socket) {

			this.socket = socket;
			
			//create streams
			try
			{
				// streams
				messageOut = new ObjectOutputStream(socket.getOutputStream());
				messageIn  = new ObjectInputStream(socket.getInputStream());
				
				// first object is always the username
				username = (String) messageIn.readObject();
				
				//add the user to the user lists
				users.add(username);
				sendTo.add(false);
			}
			catch (IOException e) {
				e.printStackTrace();
				return;
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		// thread's run method
		public void run() {
			
			//go until user clicks logout
			boolean running = true;
			while(running) {
				
				try {
					chatMessage = (ChatMessage) messageIn.readObject();
				}
				catch (IOException e) {
					e.printStackTrace();
					break;				
				}
				catch(ClassNotFoundException e) {
					e.printStackTrace();
					break;
				}
				// get actual string
				String message = chatMessage.getMessage();
				
				//users to send to
				ArrayList<Boolean> usersToSendTo = chatMessage.usersToSendTo;
				
				//everyone checkbox
				boolean sendToEveryone = chatMessage.sendToEveryone;
				
				broadcast(username + ": " + message, usersToSendTo, sendToEveryone);

			}
			
			//remove thread from server's list and close
			remove(this);
			close();
		}
		
		// close everything, pretty much killing thread
		private void close() {
			
			//close output stream
			try {
				if(messageOut != null) messageOut.close();
			}
			catch(Exception e) { e.printStackTrace(); }
			
			//close input stream
			try {
				if(messageOut != null) messageOut.close();
			}
			catch(Exception e) { e.printStackTrace(); }
			
			//close socket lastly
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) { e.printStackTrace(); }
		}
		

		//write strings to the client, they can be either
		// 0 - regular message
		// 1 - user name
		// 2 - server name 
		private boolean writeToClient(String message, int type) {
			
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				//close the streams and socket
				close();
				//failed send
				return false;
			}
			
			// send the string to the client
			try {
				
				//type 0 = regular message
				if(type == 0)
					messageOut.writeObject(message);
				
				//type 1 = user name
				else if(type == 1)
					messageOut.writeObject(">><" + message);
				
				//type 2 = server name
				else if(type == 2)
					messageOut.writeObject("<<>" + message);

			}
			catch(IOException e) {
				e.printStackTrace();
			}
			
			//Successful sending
			return true;
		}
	}
}

