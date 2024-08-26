package com.javacodegeeks.advanced.compiler;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CountElements {
	public static void main( String[] args ) throws IOException, URISyntaxException {
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();        
		final DiagnosticCollector< JavaFileObject > diagnostics = new DiagnosticCollector<>();
		final CountClassesMethodsFieldsScanner scanner = new CountClassesMethodsFieldsScanner();
		final CountElementsProcessor processor = new CountElementsProcessor( scanner );

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
