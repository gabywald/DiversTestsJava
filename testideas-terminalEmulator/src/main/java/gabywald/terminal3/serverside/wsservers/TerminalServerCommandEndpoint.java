package gabywald.terminal3.serverside.wsservers;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.auth0.jwt.interfaces.DecodedJWT;

import gabywald.terminal3.serverside.TerminalServer;
import gabywald.terminal3.serverside.filesystem.TerminalState;
import gabywald.terminal3.serverside.shell.CommandFactory;
import gabywald.terminal3.serverside.shell.CommandParser;
import gabywald.terminal3.serverside.shell.ICommand;
import gabywald.terminal3.serverside.users.User;
import gabywald.terminal3.serverside.users.UserDB;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

import java.io.IOException;
import java.util.Map;

/**
 * @author Gabriel Chandesris (2026)
 */
@ServerEndpoint("/terminalemulator")
public class TerminalServerCommandEndpoint {
	@OnOpen
	public void onOpen(Session session) {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Client connected to Command Server: [" + session.getId() + "]");
		AbstractServerTerminalServerWSModel.printParameters(session);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Received command from client [" + session.getId() + "]: {" + message + "}");
		try {
			// Treat the Command and return the result
			StringBuilder sbResponse = new StringBuilder();
			String prompt = null;
			
			Map<String, String> parameters = session.getPathParameters();
			if (parameters.containsKey("token") && ! parameters.get("token").equals("")) {
				String token = parameters.get("token");
				DecodedJWT decodedJWT = TerminalServer.verifier.verify(token);
				Logger.printlnLog(LoggerLevel.LL_DEBUG, "DecodedJWT verified !");
				// Claim claim = decodedJWT.getClaim( TokenGenerator.CLAIM_USER );
				
				// Getting the correct CMD
				String[] parts = CommandParser.parse(message);
				if (parts.length == 0) { sbResponse.append("..."); }
				else {
					String commandName = parts[0]; // 
					String[] cmdArgs = new String[parts.length - 1];
					System.arraycopy(parts, 1, cmdArgs, 0, cmdArgs.length);
					ICommand cmd = CommandFactory.getCommand(commandName);
					
					// Getting correct State / PATH (orompt)
					String login = decodedJWT.getClaim(TerminalServer.CLAIM_LOGIN).asString();
					String username = decodedJWT.getClaim(TerminalServer.CLAIM_USER).asString();
					User currentUser = UserDB.getInstance().getUserWithName(login, username);
					if (currentUser == null) 
						{ sbResponse.append("User '" + login + "' not found !"); }
					else {
						TerminalState ts = currentUser.getState();
						// Executing CMD
						sbResponse.append( cmd.execute(ts, cmdArgs) );
						
						prompt = "prompt:" + ts.getPrompt();
					}
				}
			} else { sbResponse.append("Command processed: {").append(message).append("}"); }
			
			session.getBasicRemote().sendText(sbResponse.toString());
			if (prompt != null) { session.getBasicRemote().sendText(prompt); }
		} catch (IOException e) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "Error processing command: {" + e.getMessage() + "}");
		}
	}
	
//	@OnMessage
//	public void onMessage(Message message, Session session) {
//		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Received command from client [" + session.getId() + "]: {" + message + "}");
//		try {
//			// Treat the Command and return the result
//			String response = "Command processed: {" + message + "}";
//			session.getBasicRemote().sendObject(response);
//		} catch (IOException e) {
//			Logger.printlnLog(LoggerLevel.LL_ERROR, "Error processing command: " + e.getMessage());
//		}
//	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Client disconnected from Command Server: [" + session.getId() + "]");
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		Logger.printlnLog(LoggerLevel.LL_ERROR, "Error in Command Server: {" + throwable.getMessage() + "}");
	}
}
