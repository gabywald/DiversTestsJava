package gabywald.websocket.chatServerSide.other2;

import java.io.StringReader;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message> {

	@Override
	public Message decode(String jsonMessage) throws DecodeException {

		JsonObject jsonObject = Json
				.createReader(new StringReader(jsonMessage)).readObject();
		Message message = new Message();
		message.setUsername(jsonObject.getString("username"));
		message.setMessage(jsonObject.getString("message"));
		return message;

	}

	@Override
	public boolean willDecode(String jsonMessage) {
		try {
			// Check if incoming message is valid JSON
			Json.createReader(new StringReader(jsonMessage)).readObject();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void init(EndpointConfig ec) {
		System.out.println("Initializing message decoder");
	}

	@Override
	public void destroy() {
		System.out.println("Destroyed message decoder");
	}

}
