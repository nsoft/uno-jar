package com.needhamsoftware.sandbox.relaunch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class Main {
  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println(ProcessHandle.current().info().commandLine().get());
    String[] actualArgs = ProcessHandle.current().info().arguments().get();
    String argstr = Arrays.toString(actualArgs);
    ArrayList<String> argList = new ArrayList<>(Arrays.asList(actualArgs));
    System.out.println(argstr);
    String java = System.getProperty("java.home");
    System.out.println(java);
    System.out.println(Arrays.toString(args));
    ProcessBuilder test = new ProcessBuilder("java", "-version");
    test.inheritIO();
    Process start = test.start();
    start.waitFor();

    if (args.length == 1) {
      System.out.println("parsing2");

      int count = Integer.parseInt(args[0]);
      if (count >= 10) {
        System.out.println("done");
        System.exit(0);
      }
      argList.remove(argList.size()-1);
      argList.add(String.valueOf(count +1));
      argList.add(0, java + "/bin/java");
      ProcessBuilder pb = new ProcessBuilder(argList);
      pb.inheritIO();
      pb.start().waitFor();

    }
  }
}