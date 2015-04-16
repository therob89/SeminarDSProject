package projects.matchingSample.nodes.messages;

import sinalgo.nodes.messages.Message;

public class MSMessage extends Message {

	/**
	 * The payload of the S1Message.
	 */
	public String data;
	
	/**
	 * Constructs a new Message of type S1Message.
	 *
	 * @param data
	 */
	public MSMessage(String data) {
		this.data = data;
	}
	
	public String getPayload(){
		return data;
	}

	@Override
	public Message clone() {
		return new MSMessage(data);
	}

}
