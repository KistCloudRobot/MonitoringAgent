package kr.ac.uos.ai.mcarbi.monitor.utility;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;


public class JarLoader {


	public static String getClassName(String fullName) {
		String[] parts = fullName.split("\\.");
        String className = parts[parts.length - 1];
//        System.out.println("Input String: " + fullName);
//        System.out.println("Last Part: " + className);
		
        return className;
	}
	
	public Object loadJar(String path, String className) {
		Object o = null;
		 try {
	            URL jarFileURL = new File(path).toURI().toURL();
	            URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFileURL});

	            Class<?> loadedClass = classLoader.loadClass(className);

	            o = loadedClass.newInstance();
	            
	            classLoader.close();

	        } catch (Exception e) {
	        	System.out.println("Fail to load jar : " + path + ", " + className);
	            e.printStackTrace();
	        }
		 return o;
	}
	
//	public static void main(String[] args) {
//	System.out.println(getClassName("org.json.simple.JSONObject"));
//}

}
