package gabywald.terminal3.serverside.wsservers;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.codec.binary.Base64;

import gabywald.terminal3.serverside.TerminalServer;
import gabywald.terminal3.serverside.users.User;
import gabywald.terminal3.serverside.users.UserDB;
import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

import java.io.IOException;
import java.util.Map;

/**
 * @author Gabriel Chandesris (2026)
 */
@ServerEndpoint("/authenticate")
public class TerminalServerAuthEndpoint {
	@OnOpen
	public void onOpen(Session session) {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Client connected to Auth Server: [" + session.getId() + "]");
		AbstractServerTerminalServerWSModel.printParameters(session);
		
		try {
			// Generate a Token and send it to client !
			StringBuilder sbToken = new StringBuilder(); 
			sbToken.append("");// TerminalServer.generateUUIDToken();
			// Logger.printlnLog(LoggerLevel.LL_DEBUG,  "\tGENERATE UUID TOKEN !" );
			
			Map<String, String> parameters = session.getPathParameters();
			if ( (parameters.containsKey("login")) && (parameters.containsKey("psswd")) ) {
				String login = parameters.get("login");
				String psswd = parameters.get("psswd");
				Logger.printlnLog(LoggerLevel.LL_DEBUG, "\t{" + login + "}\t{" + psswd + "}");
	            // DONE authenticate !! Check login + password
	            User user = UserDB.getInstance().getUser(login, new String(Base64.decodeBase64(psswd)));
	            // NOTE if user not filled here : stay in UUID !
	            if (user != null)  { 
	            	sbToken.append("token:").append(TerminalServer.generateToken(login, user.getUsername(), user.getRoleSTR()));
	            	Logger.printlnLog(LoggerLevel.LL_DEBUG,  "\tGENERATE JWT TOKEN !" );
	            	sbToken.append(";")	.append("message:")
	            						.append(AbstractServerTerminalServerWSModel.WELCOME_MESSAGE)
	            						.append("Hello, '").append(user.getUsername()).append("'\n");
	            }
			}
			
			session.getBasicRemote().sendText(sbToken.toString());
			if (Logger.isLogLevelAccurate(LoggerLevel.LL_DEBUG)) {
				String tokenPart = sbToken.toString().split(";")[0];
				String token    = tokenPart.split(":")[1];
				Logger.printlnLog(LoggerLevel.LL_DEBUG, "Token sent to client: {" + token + "}");
			}
		} catch (IOException e) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "Error sending token: {" + e.getMessage() + "}");
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "Client disconnected from Auth Server: [" + session.getId() + "]");
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		Logger.printlnLog(LoggerLevel.LL_ERROR, "Error in Auth Server: {" + throwable.getMessage() + "}");
	}
}
