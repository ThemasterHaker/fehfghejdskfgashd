

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JCheckBox;

// better to have messages as an object incase we need to send some other information
// other than just the actual text
public class ChatMessage implements Serializable {

	private static final long serialVersionUID = -6851165824415585071L;
	private String message;
	
	public ArrayList<Boolean> usersToSendTo;
	public boolean sendToEveryone;
	
	ChatMessage(String message, ArrayList<Boolean> usersToSendTo, boolean sendToEveryone) {
		this.message = message;
		this.usersToSendTo = usersToSendTo;
		this.sendToEveryone = sendToEveryone;
	}
	
	String getMessage() {
		return message;
	}
}
