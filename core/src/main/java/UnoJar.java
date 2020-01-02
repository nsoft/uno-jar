/*
 * Copyright (c) 2004-2010, P. Simon Tuffs (simon@simontuffs.com)
 * All rights reserved.
 *
 * See the full license at http://one-jar.sourceforge.net/one-jar-license.html
 * This license is also included in the distributions of this software
 * under doc/one-jar-license.txt
 */

import com.needhamsoftware.unojar.Boot;

import java.util.Arrays;

/**
 * Uno-JAR Jar files are intended to be executed using the following kind of command:
 * <pre>
 *   java -jar &lt;uno-jar.jar&gt; [args]
 * </pre>
 * This class allows a Uno-JAR jar-file to be executed using the alternative command:
 * <pre>
 *   java -cp &lt;uno-jar.jar&gt; UnoJar [args]
 * </pre>
 * Its main role is in testing the behaviour of OneJar on platforms which mangle the classpath
 * when running with the first kind of command, but it can also be a useful alternative
 * execution mechanism.
 * <p>Note: the same effect can be obtained by using the Boot class, albeit with more
 * typing:
 * <pre>
 *   java -cp &lt;uno-jar.jar&gt; com.needhamsoftware.unojar.Boot [args]
 * </pre>
 * @author simon
 *
 */
public class UnoJar {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.out.println("UnoJar" + Arrays.asList(args).toString().replace('[','(').replace(']',')'));
        new UnoJar().run(args);

    }

    public void run(String[] args) throws Exception {
        Boot.run(args);
    }

}
