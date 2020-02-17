package gabywald.tests.reflect;

import javax.lang.model.SourceVersion;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class JavaCompilerAPI {

	public static void main(String[] args) {

		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();       
		for ( final SourceVersion version: compiler.getSourceVersions() ) {
			System.out.println( version );
		}

		// ***** 

		final DiagnosticCollector< JavaFileObject > diagnostics = new DiagnosticCollector<>();
		final StandardJavaFileManager manager = compiler.getStandardFileManager( diagnostics, null, null );

//		final File file = new File( CompilerExample.class.getResource("/SampleClass.java").toURI() );
//
//		final Iterable< ? extends JavaFileObject > sources = 
//				manager.getJavaFileObjectsFromFiles( Arrays.asList( file ) );  

	}
}
