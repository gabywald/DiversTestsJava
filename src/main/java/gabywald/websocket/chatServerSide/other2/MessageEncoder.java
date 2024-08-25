package gabywald.websocket.chatServerSide.other2;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message> {

	@Override
	public String encode(Message message) throws EncodeException {

		JsonObject jsonObject = Json.createObjectBuilder()
				.add("username", message.getUsername())
				.add("message", message.getMessage()).build();
		return jsonObject.toString();

	}

	@Override
	public void init(EndpointConfig ec) {
		System.out.println("Initializing message encoder");
	}

	@Override
	public void destroy() {
		System.out.println("Destroying encoder...");
	}

}
