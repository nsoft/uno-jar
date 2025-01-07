package com.needhamsoftware.unojar.example;

public class PrintSysprops {
  public static void main(String[] args) {
    System.out.println(System.getProperties().toString().replaceAll(",", "\n"));
  }
}
