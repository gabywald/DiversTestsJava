package com.javacodegeeks.advanced.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CountElementsV2 {
	public static void main( String[] args ) throws IOException, URISyntaxException {
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();        
		final DiagnosticCollector< JavaFileObject > diagnostics = new DiagnosticCollector<>();
		final CountClassesMethodsFieldsScanner scanner = new CountClassesMethodsFieldsScanner();
		final CountElementsProcessor processor = new CountElementsProcessor( scanner );
		
		// ***** write class ***** 
		File sourceFile		= new File( "bin/SampleClassToParse.java" );
		FileWriter writer	= new FileWriter(sourceFile);
		StringBuilder sb	= new StringBuilder();
		sb.append("public class SampleClassToParse {").append("\n");
		sb.append("\t").append("private String str;").append("\n");
		sb.append("\t").append("\n");
		sb.append("\t").append("private static class InnerClass {").append("\n");
		sb.append("\t\t").append("private int number;").append("\n");
		sb.append("\t\t").append("\n");
		sb.append("\t\t").append("public void method() {").append("\n");
		sb.append("\t\t\t").append("int i = 0;").append("\n");
		sb.append("\t\t\t").append("\n");
		sb.append("\t\t\t").append("try {").append("\n");
		sb.append("\t\t\t\t").append("// Some implementation here").append("\n");
		sb.append("\t\t\t").append("} catch( final Throwable ex ) {").append("\n");
		sb.append("\t\t\t\t").append("// Some implementation here").append("\n");
		sb.append("\t\t\t").append("}").append("\n");
		sb.append("\t\t").append("}").append("\n");
		sb.append("\t").append("}").append("\n");
		sb.append("\t").append("\n");
		sb.append("\t").append("public static void main(String[] args) {").append("\n");
		sb.append("\t").append("\t").append("System.out.println( \"SampleClassToParse has been compiled!\" );").append("\n");
		sb.append("\t").append("}").append("\n");
		sb.append("}").append("\n");
		writer.write( sb.toString() );
		writer.close();
		// ***** ***** ***** 

		try ( final StandardJavaFileManager manager = compiler.getStandardFileManager( diagnostics, null, null ) ) {

			final File file = new File( CompilerExample.class.getResource("/SampleClassToParse.java").toURI() );
			final Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles( Arrays.asList( file ) );            

			final CompilationTask task = compiler.getTask( null, manager, diagnostics, null, null, sources );
			task.setProcessors( Arrays.asList( processor ) );
			task.call();
		}    

		System.out.format( "Classes %d, methods/constructors %d, fields %d", scanner.getNumberOfClasses(),
				scanner.getNumberOfMethods(), scanner.getNumberOfFields() );
	}
}
