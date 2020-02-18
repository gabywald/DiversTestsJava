package com.javacodegeeks.advanced.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CompilerExampleV2 {
	public static void main(String[] args) throws IOException, URISyntaxException {
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();        
		final DiagnosticCollector< JavaFileObject > diagnostics = new DiagnosticCollector<>();
		
		// ***** write class ***** 
		File sourceFile		= new File( "SampleClass.java" );
		FileWriter writer	= new FileWriter(sourceFile);
		StringBuilder sb	= new StringBuilder();
		sb.append("public class SampleClass {").append("\n");
		sb.append("\t").append("public static void main(String[] args) {").append("\n");
		sb.append("\t").append("\t").append("System.out.println( \"SampleClass has been compiled!\" );").append("\n");
		sb.append("\t").append("}").append("\n");
		sb.append("}").append("\n");
		writer.write( sb.toString() );
		writer.close();
		// ***** ***** ***** 

		try ( final StandardJavaFileManager manager = compiler.getStandardFileManager( diagnostics, null, null ) ) {

			final File file = new File( CompilerExampleV2.class.getResource("/SampleClass.java").toURI() );
			final Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles( Arrays.asList( file ) );            

			final CompilationTask task = compiler.getTask( null, manager, diagnostics, null, null, sources );            
			task.call();
		}        

		for ( final Diagnostic< ? extends JavaFileObject > diagnostic : diagnostics.getDiagnostics() ) {
			System.out.format("%s, line %d in %s", 
					diagnostic.getMessage( null ),
					diagnostic.getLineNumber(),
					diagnostic.getSource().getName() );
		}
	}
}
