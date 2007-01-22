/*
 * Copyright (c) 2004-2007, P. Simon Tuffs (simon@simontuffs.com)
 * All rights reserved.
 *
 * See the full license at http://www.simontuffs.com/one-jar/one-jar-license.html
 * This license is also included in the distributions of this software
 * under doc/one-jar-license.txt
 */
import java.util.Arrays;

import com.simontuffs.onejar.Boot;




/**
 * One-JAR Jar files are intended to be executed using the following kind of command:
 * <pre>
 *   java -jar <one-jar.jar> [args]
 * </pre>
 * This class allows a One-JAR jar-file to be executed using the alternative command:
 * <pre>
 *   java -cp <one-jar.jar> OneJar [args]
 * </pre>
 * Its main role is in testing the behaviour of OneJar on platforms which mangle the classpath
 * when running with the first kind of command, but it can also be a useful alternative
 * execution mechanism.
 * @author simon
 *
 */
public class OneJar {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.out.println("OneJar" + Arrays.asList(args).toString().replace('[','(').replace(']',')'));
        new OneJar().run(args);
        
    }
    
    public void run(String[] args) throws Exception {
        // Scan the list of jar files for the jar file containing Boot.class.
        System.out.println("classpath=" + System.getProperty("java.class.path"));
        String jarname = Boot.getMyJarName();
        System.out.println("jarname=" + jarname);
    }
    
    

}
