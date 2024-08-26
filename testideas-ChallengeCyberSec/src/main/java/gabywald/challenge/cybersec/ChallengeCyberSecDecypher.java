package gabywald.challenge.cybersec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ChallengeCyberSecDecypher {

	public static void main(String[] args) {
//		String basic = "U1lTSV9TQTpjNzY0NTZkMi05NWU1LTRlYWQtYmZmZS05YTFhZmE1NzlkMDY=";
//		try {
//			System.out.println("Basic " + ChallengeCyberSecDecypher.convert(Base64.decode(basic)) );
//		} catch (IOException e) {
//			System.out.println("\t\t cannot be decoded !");
//		}
		
		String path = "resources/ChallengeCybersec/";
		
		try {
			String content	= ChallengeCyberSecDecypher.readFileViaBufferedReader
								( path + "data2extractv2.txt" );
			
			// System.out.println("content " + ChallengeCyberSecDecypher.convert(Base64.decode(content)) );
			
			ChallengeCyberSecDecypher.write(path + "contentOutput01.txt", new String(Base64.decode(content)));
			
			ChallengeCyberSecDecypher.write(path + "contentOutput02.txt", ChallengeCyberSecDecypher.convert(Base64.decode(content)));
			
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			System.out.println( "FileNotFoundException: {" + e.getMessage() + "}");
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println( "IOException: {" + e.getMessage() + "}");
		}
		
		try {
			String content	= ChallengeCyberSecDecypher.readFileViaBufferedReader
								( path + "Extract2FromPDF.txt" );
			
			// System.out.println("content " + ChallengeCyberSecDecypher.convert(Base64.decode(content)) );
			
			ChallengeCyberSecDecypher.write(path + "contentOutput03.txt", new String(Base64.decode(content)));
			
			ChallengeCyberSecDecypher.write(path + "contentOutput04.txt", ChallengeCyberSecDecypher.convert(Base64.decode(content)));
			
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			System.out.println( "FileNotFoundException: {" + e.getMessage() + "}");
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println( "IOException: {" + e.getMessage() + "}");
		}
		
	}
	
	public static String convert(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	public static String readFileViaBufferedReader(String path2file) 
			throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader( path2file ));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		String ls = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		// delete the last new line separator
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		reader.close();

		return stringBuilder.toString();
	}
	
	public static void write(String filename, String content) {
		FileOutputStream fop = null;
		File file;

		try {

			file	= new File( filename );
			fop		= new FileOutputStream(file);

			// if file doesnt exists, then create it
			if ( ! file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println( "IOException: {" + e.getMessage() + "}");
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
