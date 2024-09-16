package com.javacodegeeks.advanced.compiler;

import java.io.File;
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

public class CompilerErrorExample {
	public static void main(String[] args) throws IOException, URISyntaxException {
		final JavaCompiler compiler								= ToolProvider.getSystemJavaCompiler();        
		final DiagnosticCollector< JavaFileObject > diagnostics	= new DiagnosticCollector<>();

		try ( final StandardJavaFileManager manager = 
				compiler.getStandardFileManager( diagnostics, null, null ) ) {
			
			System.out.println( "\t -----: {" + CompilerErrorExample.class.getResource("/SampleClassWithErrors.java") + "}" );
			System.out.println( "\t -----: {" + CompilerErrorExample.class.getResource("/SampleClassToParse.java") + "}" );
			System.out.println( "\t -----: {" + CompilerErrorExample.class.getResource("/SampleClass.java") + "}" );

			final File file = new File( CompilerErrorExample.class.getResource("/SampleClassWithErrors.java").toURI() );

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
