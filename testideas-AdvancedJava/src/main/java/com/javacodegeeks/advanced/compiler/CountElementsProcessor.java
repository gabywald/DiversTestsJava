package com.javacodegeeks.advanced.compiler;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@SupportedSourceVersion( SourceVersion.RELEASE_7 )
@SupportedAnnotationTypes( "*" )
public class CountElementsProcessor extends AbstractProcessor {
	private final CountClassesMethodsFieldsScanner scanner;

	public CountElementsProcessor( final CountClassesMethodsFieldsScanner scanner ) {
		this.scanner = scanner;
	}

	public boolean process( final Set< ? extends TypeElement > types, final RoundEnvironment environment ) {
		if ( ! environment.processingOver() ) {
			for ( final Element element: environment.getRootElements() ) {
				this.scanner.scan( element );
			}
		}

		return true;
	}
}
