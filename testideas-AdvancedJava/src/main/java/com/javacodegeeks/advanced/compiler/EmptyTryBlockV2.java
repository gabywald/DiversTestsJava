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

public class EmptyTryBlockV2 {
	public static void main( String[] args ) throws IOException, URISyntaxException {
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();        
		final DiagnosticCollector< JavaFileObject > diagnostics = new DiagnosticCollector<>();
		final EmptyTryBlockScanner scanner = new EmptyTryBlockScanner();
		final EmptyTryBlockProcessor processor = new EmptyTryBlockProcessor( scanner );
		
		// ***** write class ***** 
		File sourceFile01		= new File( "SampleClassToParse.java" );
		FileWriter writer01	= new FileWriter(sourceFile01);
		StringBuilder sb01	= new StringBuilder();
		sb01.append("public class SampleClassToParse {").append("\n");
		sb01.append("\t").append("private String str;").append("\n");
		sb01.append("\t").append("\n");
		sb01.append("\t").append("private static class InnerClass {").append("\n");
		sb01.append("\t\t").append("private int number;").append("\n");
		sb01.append("\t\t").append("\n");
		sb01.append("\t\t").append("public void method() {").append("\n");
		sb01.append("\t\t\t").append("int i = 0;").append("\n");
		sb01.append("\t\t\t").append("\n");
		sb01.append("\t\t\t").append("try {").append("\n");
		sb01.append("\t\t\t\t").append("// Some implementation here").append("\n");
		sb01.append("\t\t\t").append("} catch( final Throwable ex ) {").append("\n");
		sb01.append("\t\t\t\t").append("// Some implementation here").append("\n");
		sb01.append("\t\t\t").append("}").append("\n");
		sb01.append("\t\t").append("}").append("\n");
		sb01.append("\t").append("}").append("\n");
		sb01.append("\t").append("\n");
		sb01.append("\t").append("public static void main(String[] args) {").append("\n");
		sb01.append("\t").append("\t").append("System.out.println( \"SampleClassToParse has been compiled!\" );").append("\n");
		sb01.append("\t").append("}").append("\n");
		sb01.append("}").append("\n");
		writer01.write( sb01.toString() );
		writer01.close();
		// ***** ***** ***** 
		
		// ***** write class ***** 
		File sourceFile02		= new File( "SampleClass.java" );
		FileWriter writer02	= new FileWriter(sourceFile02);
		StringBuilder sb02	= new StringBuilder();
		sb02.append("public class SampleClass {").append("\n");
		sb02.append("\t").append("public static void main(String[] args) {").append("\n");
		sb02.append("\t").append("\t").append("System.out.println( \"SampleClass has been compiled!\" );").append("\n");
		sb02.append("\t").append("}").append("\n");
		sb02.append("}").append("\n");
		writer02.write( sb02.toString() );
		writer02.close();
		// ***** ***** ***** 

		try ( final StandardJavaFileManager manager = compiler.getStandardFileManager( diagnostics, null, null ) ) {

			final Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles( 
					Arrays.asList( 
							new File( CompilerExample.class.getResource( "/SampleClassToParse.java" ).toURI() ),
							new File( CompilerExample.class.getResource( "/SampleClass.java" ).toURI() )
							) 
					);

			final CompilationTask task = compiler.getTask( null, manager, diagnostics, null, null, sources );
			task.setProcessors( Arrays.asList( processor ) );
			task.call();
		}  

		System.out.format( "Empty try/catch blocks: %d", scanner.getNumberOfEmptyTryBlocks() );
	}
}
