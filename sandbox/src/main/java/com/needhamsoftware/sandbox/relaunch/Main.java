package com.needhamsoftware.sandbox.relaunch;

import com.needhamsoftware.unojar.Boot;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class Main {

  public static  int INCR = Integer.getInteger("relauncher.increment",1);

  public static void main(String[] args) throws IOException, InterruptedException {
    String jar = Boot.getMyJarPath();
    JarInputStream jis = new JarInputStream(new URL(jar).openConnection().getInputStream());
    Manifest manifest = jis.getManifest();
    Object incr = manifest.getMainAttributes().getValue("Relaunch-Increment");
    int inc = INCR;
    if (incr != null) {
      inc = Integer.parseInt(String.valueOf(incr));
    }
    System.out.println(ProcessHandle.current().info().commandLine().get());
    String[] actualArgs = ProcessHandle.current().info().arguments().get();
    String argstr = Arrays.toString(actualArgs);
    ArrayList<String> argList = new ArrayList<>(Arrays.asList(actualArgs));
    System.out.println(argstr);
    String java = System.getProperty("java.home");
    System.out.println(java);
    System.out.println(Arrays.toString(args));

    if (args.length == 1) {
      System.out.println("parsing2");

      int count = Integer.parseInt(args[0]);
      if (count >= 10) {
        System.out.println("done");
        System.exit(0);
      }
      argList.remove(argList.size()-1);
      argList.add(String.valueOf(count + INCR));
      argList.add(0, "-Drelauncher.increment="+inc);
      argList.add(0, java + "/bin/java");
      LinkedHashSet<String> tmp = new LinkedHashSet<>(argList);
      ProcessBuilder pb = new ProcessBuilder(new ArrayList<>(tmp));
      pb.inheritIO();
      pb.start().waitFor();
    }
  }
}