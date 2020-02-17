package gabywald.tests.reflect;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class DynamicCompilationV3 {

	// ***** https://www.rgagnon.com/javadetails/java-0039.html
	// see also : https://stackoverflow.com/questions/2946338/how-do-i-programmatically-compile-and-instantiate-a-java-class
	// see also : https://stackoverflow.com/questions/12173294/compile-code-fully-in-memory-with-javax-tools-javacompiler
	// see also : http://www.rndblog.com/how-to-dynamically-create-an-object-in-java-from-a-class-name-given-as-string-format/

	// https://www.javacodegeeks.com/2015/09/java-compiler-api.html -- 
	// https://docs.oracle.com/javase/7/docs/api/javax/tools/ToolProvider.html -- 
	// https://docs.oracle.com/javase/7/docs/api/javax/tools/StandardJavaFileManager.html -- 
	
	public static void main(String[] args) throws Exception {
		// create the source
		File sourceFile   = new File("/temp/Hello.java");
		FileWriter writer = new FileWriter(sourceFile);

		StringBuilder sb = new StringBuilder();
		sb.append("public class Hello {").append("\n");
		sb.append("\t").append("public void doit() {").append("\n");
		sb.append("\t").append("\t").append("System.out.println(\"Hello world\");").append("\n");
		sb.append("\t").append("}").append("\n");
		sb.append("}").append("\n");
		writer.write( sb.toString() );
		writer.close();

		JavaCompiler compiler				= ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager	= compiler.getStandardFileManager(null, null, null);

//		List<File> files = Arrays.asList(new File("resources/compilation/*.class"));
//		for (File file : files) {
//			System.out.println(file.getName());
//		}
		
		fileManager.setLocation(	StandardLocation.CLASS_OUTPUT, 
									Arrays.asList(new File("/temp")));
		// Compile the file
		compiler.getTask(	null,
							fileManager,
							null,
							null,
							null,
							fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile)))
					.call();
		fileManager.close();
		
		// compiler.run(null, null, null, sourceFile.getPath());

		// delete the source file
		// sourceFile.deleteOnExit();

		DynamicCompilationV3.runIt();
	}

	public static void runIt() {
		try {
			Class<?> params[]	= {};
			Object paramsObj[]	= {};
			Class<?> thisClass	= Class.forName("Hello");
			Object iClass		= thisClass.newInstance();
			Method thisMethod	= thisClass.getDeclaredMethod("doit", params);
			thisMethod.invoke(iClass, paramsObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
