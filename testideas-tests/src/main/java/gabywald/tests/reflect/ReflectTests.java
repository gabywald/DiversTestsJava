package gabywald.tests.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectTests {

	public static void main(String[] args) {
		
		// Sans utiliser la réflexion
		Foo foo = new Foo();
		foo.hello();
		
		try {
			
			// En utilisant la réflexion
			System.out.println(" -- En utilisant la réflexion -- ");
			Class<?> cl = Class.forName("gabywald.tests.reflect.Foo");
			
			// Instanciation de l'objet dont la méthode est à appeler
			System.out.println(" -- Instanciation de l'objet dont la méthode est à appeler -- ");
			Object instance	= cl.newInstance();
//			List<Method> methods = Arrays.asList(instance.getClass().getDeclaredMethods());
//			for (Method method : methods) {
//				System.out.println("\t " + method.getName());
//			}
			// Invocation de la méthode via réflexion
			System.out.println(" -- Invocation de la méthode via réflexion (1) -- ");
			Method method	= instance.getClass().getDeclaredMethod("hello", new Class<?>[0]);
			System.out.println(" -- Invocation de la méthode via réflexion (2) -- ");
			method.invoke(instance);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		// https://openclassrooms.com/fr/courses/26832-apprenez-a-programmer-en-java/22839-java-et-la-reflexivite
		// https://java.developpez.com/faq/java?page=Reflexivite
		// https://docs.oracle.com/javase/tutorial/reflect/class/index.html
		// https://www.journaldev.com/1789/java-reflection-example-tutorial
		// https://www.baeldung.com/java-reflection
		
	}

}

class Foo {
	public void hello() {
		System.out.println("Foo::hello()");
	}
}
