package com.javacodegeeks.advanced.compiler;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.source.util.Trees;

@SupportedSourceVersion( SourceVersion.RELEASE_7 )
@SupportedAnnotationTypes( "*" )
public class EmptyTryBlockProcessor extends AbstractProcessor {
	private final EmptyTryBlockScanner scanner;
	private Trees trees;

	public EmptyTryBlockProcessor( final EmptyTryBlockScanner scanner ) {
		this.scanner = scanner;
	}

	@Override
	public synchronized void init( final ProcessingEnvironment processingEnvironment ) {
		super.init( processingEnvironment );
		this.trees = Trees.instance( processingEnvironment );
	}

	public boolean process( final Set< ? extends TypeElement > types, final RoundEnvironment environment ) {
		if( !environment.processingOver() ) {
			for( final Element element: environment.getRootElements() ) {
				this.scanner.scan( this.trees.getPath( element ), this.trees );
			}
		}

		return true;
	}
}
