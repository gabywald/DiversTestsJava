package gabywald.test.resources;

import java.net.URL;

public class ClassDemo {

	public static void main(String[] args) throws Exception {

		ClassDemo c = new ClassDemo();
		Class<?> cls = c.getClass();

		// finds resource relative to the class location : WORKS !
		URL url = cls.getResource("file.txt");
		System.out.println("Value = " + url);

		// finds resource relative to the class location : WORKS !
		url = cls.getResource("newfolder/a.txt");
		System.out.println("Value = " + url);
		
		// finds resource absolute to the class location : WORKS !
		url = cls.getResource("/test2.txt");
		System.out.println("Value = " + url);
		
		// finds resource absolute to the class location : WORKS !
		url = cls.getResource("/next/b.txt");
		System.out.println("Value = " + url);
		
		// finds resource absolute to the class location : WORKS !
		url = cls.getClassLoader().getResource("test2.txt");
		System.out.println("Value = " + url);
		
		// finds resource absolute to the class location : DID NOT !
		url = cls.getResource("/test.txt");
		System.out.println("Value = " + url);
		
		// finds resource absolute to the class location : DID NOT !
		url = cls.getClassLoader().getResource("test.txt");
		System.out.println("Value = " + url);
		
		// finds resource absolute to the class location : DID NOT !
		url = cls.getResource("/../test.txt");
		System.out.println("Value = " + url);
		
		// finds resource absolute to the class location : DID NOT !
		url = cls.getClassLoader().getResource("../../../../test.txt");
		System.out.println("Value = " + url);
		
		// finds resource absolute to the class location : DID NOT !
		url = cls.getClassLoader().getResource("/../../../../test.txt");
		System.out.println("Value = " + url);
		
		// finds resource relative to the class location : WORKS !
		url = ClassDemo.class.getResource("file.txt");
		System.out.println("Value = " + url);

		// finds resource relative to the class location : WORKS !
		url = ClassDemo.class.getResource("newfolder/a.txt");
		System.out.println("Value = " + url);
		
		// finds resource absolute to the class location : WORKS !
		url = ClassDemo.class.getResource("/test2.txt");
		System.out.println("Value = " + url);
		
		// finds resource absolute to the class location : WORKS !
		url = ClassDemo.class.getResource("/next/b.txt");
		System.out.println("Value = " + url);
		
		// finds resource absolute to the class location : WORKS !
		url = ClassDemo.class.getClassLoader().getResource("test2.txt");
		System.out.println("Value = " + url);
		

	}
}
