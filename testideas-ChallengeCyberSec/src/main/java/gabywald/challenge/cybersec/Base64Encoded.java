package gabywald.challenge.cybersec;

import java.util.Base64;

public class Base64Encoded {

	public static void main(String... strings) {
		String clientId		= "SuperOAuthArticle";
		String clientSecret	= "YourFeedbackIsAppreciated";
		System.out.println(Base64Encoded.encodeCredentials(clientId, clientSecret));
		
		System.out.println();
		// 20190314
		// https://twitter.com/EtatMajorFR/status/1106128879612252161
		System.out.println(Base64Encoded.decode("TGFuY2VtZW50IGRlIGxhIHPDqXF1ZW5jZSBkJ2luaXRpYWxpc2F0aW9uLiAKUHJvZ3JhbW1lIERlZm5ldC4KUHJvY2Vzc2luZy4="));
		System.out.println();
		// https://twitter.com/gouvernementFR/status/1106186691776315392
		System.out.println(Base64Encoded.decode("Tm91cyB2b3VzIHNvdWhhaXRvbnMgdW4gYm9uIGV4ZXJjaWNlLiBRdWUgbGEgZm9yY2Ugc29pdCBhdmVjIHZvdXMuIEZpbiBkZSB0cmFuc21pc3Npb24u"));
		System.out.println();
		// https://twitter.com/Generationopex/status/1106262003130290176
		System.out.println(Base64Encoded.decode("QXR0ZW50aW9uLCB2b3VzIMOqdGVzIGVzcGlvbm7DqXM="));
		System.out.println();
		System.out.println();
		
		System.out.println(Base64Encoded.decode("FQ3lbFit1JtSOlc4rV32j6x08"));
		
		System.out.println(Base64Encoded.decode("NO6kcptJ0tU16cH7GSqma9x46YZChJblLxCnri0yAtebPUeznl"));
	}
	
	public static String encodeCredentials(String username, String password) {
		String cred = username + ":" + password;
		String encodedValue = null;
		byte[] encodedBytes = Base64.getEncoder().encode(cred.getBytes());
		encodedValue = new String(encodedBytes);
		System.out.println("encodedBytes " + new String(encodedBytes));

		byte[] decodedBytes = Base64.getDecoder().decode(encodedBytes);
		System.out.println("decodedBytes " + new String(decodedBytes));

		return encodedValue;
	}
	
	public static String decode(String toDecode) {
		return new String(Base64.getDecoder().decode(toDecode));
	}

}
