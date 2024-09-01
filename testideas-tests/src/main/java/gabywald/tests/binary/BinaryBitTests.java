package gabywald.tests.binary;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BinaryBitTests {

	public static void main(String[] args) {

		String input = "Hello";
		String result = convertStringToBinary(input);
		System.out.println(result);
		System.out.println(prettyBinary(result, 8, " "));

		String input02 = "a";
		String result02 = convertByteArraysToBinary(input02.getBytes(StandardCharsets.UTF_8));
		System.out.println(prettyBinary(result02, 8, " "));

		// 0100100001100101011011000110110001101111
		// 01001000 01100101 01101100 01101100 01101111
		String binary01 = "0100100001100101011011000110110001101111";
		String binary02 = "01001000 01100101 01101100 01101100 01101111";
		// System.out.println(convertBinaryToString(binary01));
		System.out.println(convertBinaryToString(binary02));
		System.out.println(convertBinaryToAscii(binary01));

		// ACGT <=> 00 01 10 11
		System.out.println((int)'A'); // 65
		System.out.println((int)'C'); // 67
		System.out.println((int)'G'); // 71
		System.out.println((int)'T'); // 84
		System.out.println((int)'U'); // 85

		System.out.println(sequence2binary("ACGT"));
		System.out.println(binary2sequence("00011011"));

		System.out.println(convertBinaryToString(sequence2binary("ACGT")));
		System.out.println(binary2sequence(convertStringToBinary("")));

		System.out.println(sequence2binary("ACGTACGTACGTACGT"));
		System.out.println(convertBinaryToString(sequence2binary("ACGTACGTACGTACGT")));
	}

	public static String sequence2binary(String input) {
		// input.chars().map( c -> ((c == 65)?"00":((c == 67)?"01":((c == 71)?"10":((c == 84)?"11":""))))).forEach(System.out::println); // .collect(Collectors.joining());
		StringBuilder result = new StringBuilder();
		for (char c : input.toCharArray()) 
			{ result.append(((c == 65)?"00":((c == 67)?"01":((c == 71)?"10":((c == 84)?"11":""))))); }
		return result.toString();
	}

	public static String binary2sequence(String input) {
		StringBuilder output = new StringBuilder();
		for (int i = 0 ; i <= input.length() - 2 ; i += 2) {
			String b = input.substring(i, i + 2);
			int k = ((b.equals("00"))?65:((b.equals("01"))?67:((b.equals("10"))?71:((b.equals("11"))?84:85))));
			output.append((char) k);
		}
		return output.toString();
	}

	public static String convertStringToBinary(String input) {
		StringBuilder result = new StringBuilder();
		char[] chars = input.toCharArray();
		for (char aChar : chars) {
			// char -> int, auto-cast ; zero pads
			result.append( String.format("%8s", Integer.toBinaryString(aChar)).replaceAll(" ", "0") );
		}

		return result.toString();
	}

	public static String convertByteArraysToBinary(byte[] input) {
		StringBuilder result = new StringBuilder();
		for (byte b : input) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				result.append((val & 128) == 0 ? 0 : 1);	  // 128 = 1000 0000
				val <<= 1;
			}
		}
		return result.toString();
	}

	public static String convertBinaryToString(String input) 
		{ return Arrays.stream(input.split(" "))
				.map(b -> Integer.parseInt(b, 2))
				.map(b -> ((char)b.intValue()) + "")
				.collect(Collectors.joining()); }

	public static String convertBinaryToAscii(String binary) {
		// Convert binary string into ASCII.
		StringBuilder output = new StringBuilder();
		for (int i = 0 ; i <= binary.length() - 8 ; i += 8) {
			int k = Integer.parseInt(binary.substring(i, i + 8), 2);
			output.append((char) k);
		}
		return output.toString();
	}

	public static String prettyBinary(String binary, int blockSize, String separator) {
		List<String> result = new ArrayList<>();
		int index = 0;
		while (index < binary.length()) {
			result.add(binary.substring(index, Math.min(index + blockSize, binary.length())));
			index += blockSize;
		}

		return result.stream().collect(Collectors.joining(separator));
	}
}
