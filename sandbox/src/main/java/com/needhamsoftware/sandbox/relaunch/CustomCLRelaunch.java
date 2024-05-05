package com.needhamsoftware.sandbox.relaunch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CustomCLRelaunch {
  public static void main(String[] args) throws IOException, InterruptedException {
    String scl = Util.getManifestAttribute("UnoJar-SystemClassLoaderClass");
    String sclSet = System.getProperty("java.system.class.loader");
    System.out.println(sclSet);
    if (scl != null && sclSet == null) {
      System.out.println("relaunch");
      relaunch(scl);
    }
    System.out.println("Here We are...");
    File file = new File(System.getProperty("user.dir"));
    System.out.println(file.exists());
  }

  private static void relaunch( String classloader) throws InterruptedException, IOException {
    String javahome = System.getProperty("java.home");
    String[] actualArgs = ProcessHandle.current().info().arguments().get();
    ArrayList<String> argList = new ArrayList<>(Arrays.asList(actualArgs));
    argList.add(0, "-Djava.system.class.loader="+ classloader);
    argList.add(0, String.join(File.separator, javahome, "bin","java"));
    ProcessBuilder pb = new ProcessBuilder(argList);
    pb.inheritIO();
    pb.start().waitFor();
  }

}
