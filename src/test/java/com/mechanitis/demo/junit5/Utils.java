package com.mechanitis.demo.junit5;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;



public class Utils {
  public static String showcaseProductGet() throws IOException {
    File file = new File("showcaseProductGet.groovy");
    return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
  }

  public static String UtilsGet() throws IOException {
    File file = new File("util.groovy");
    return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
  }
}
