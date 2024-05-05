package com.needhamsoftware.sandbox.relaunch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class Main {

  public static  int INCR = Integer.getInteger("relauncher.increment",1);

  public static void main(String[] args) throws IOException, InterruptedException {
    String incr = Util.getManifestAttribute("Relaunch-Increment");
    int inc = INCR;
    if (incr != null) {
      inc = Integer.parseInt(incr);
    }
    System.out.println(ProcessHandle.current().info().commandLine().get());
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
      relaunch(count, inc, java);
    }
  }

  private static void relaunch(int count, int inc, String java) throws InterruptedException, IOException {
    String[] actualArgs = ProcessHandle.current().info().arguments().get();
    String argstr = Arrays.toString(actualArgs);
    System.out.println(argstr);
    ArrayList<String> argList = new ArrayList<>(Arrays.asList(actualArgs));
    argList.remove(argList.size()-1);
    argList.add(String.valueOf(count + INCR));
    argList.add(0, "-Drelauncher.increment="+ inc);
    argList.add(0, java + "/bin/java");
    LinkedHashSet<String> tmp = new LinkedHashSet<>(argList);
    ProcessBuilder pb = new ProcessBuilder(new ArrayList<>(tmp));
    pb.inheritIO();
    pb.start().waitFor();
  }

}