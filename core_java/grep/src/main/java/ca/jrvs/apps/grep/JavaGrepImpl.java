package ca.jrvs.apps.grep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaGrepImpl implements JavaGrep {

  private static final Logger logger = LoggerFactory.getLogger(JavaGrepImpl.class);
  private String regex; // Regex pattern as a raw string passed from CLI
  private String rootPath; // Root directory to start recursive search
  private String outFile; // Output file to store matched lines
  private Pattern pattern; // Compiled regex pattern (lazy compiled)

  @Override
  public void process() throws IOException {
    validateArgs();
    // Compile the regex once for performance
    if (pattern == null) {
      pattern = Pattern.compile(regex);
    }
    logger.info("Starting grep. rootPath='{}', regex='{}', outFile='{}'", rootPath, regex, outFile);
    List<String> matches = new ArrayList<>();
    List<File> files = listFiles(rootPath);
    logger.info("Found {} files under '{}'", files.size(), rootPath);

    for (File file : files) {
      try {
        List<String> lines = readLines(file);
        for (String line : lines) {
          if (containsPattern(line)) {
            // Prefix each matched line with the file path (grep -r style)
            matches.add(file.getPath() + ":" + line);
          }
        }
      } catch (IOException e) {
        // Log and continue with other files
        logger.warn("Failed reading file '{}'", file.getPath(), e);
      }
    }
    writeToFile(matches);
    logger.info("Completed. Matched {} line(s). Output -> {}", matches.size(), outFile);
  }

  @Override
  public List<File> listFiles(String rootDir) {
    File root = new File(rootDir);
    List<File> files = new ArrayList<>();
    if (!root.exists() || !root.isDirectory()) {
      throw new IllegalArgumentException(
          "Root directory does not exist or is not a directory" + rootDir);
    }
    File[] fileList = root.listFiles();
    if (fileList != null) {
      for (File file : fileList) {
        if (file.isDirectory()) {
          files.addAll(listFiles(file.getAbsolutePath()));
        } else {
          files.add(file);
        }
      }
    }

    return files;
  }

  @Override
  public List<String> readLines(File inputFile) throws IOException {
    if (inputFile == null || !inputFile.isFile()) {
      throw new IllegalArgumentException("input file is not a regular file: " + inputFile);
    }
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8)
    )) {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    }
    return lines;
  }

  @Override
  public boolean containsPattern(String line) {
    if (pattern == null) {
      // Safety: compile regex if it was not compiled yet
      pattern = Pattern.compile(regex);
    }
    return pattern.matcher(line).find();
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {
    File out = new File(outFile);
    File parent = out.getParentFile();
    if (parent != null && !parent.exists()) {
      parent.mkdirs();
    }
    try (BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8))) {
      for (String line : lines) {
        writer.write(line);
        writer.newLine();
      }
    }
  }

  @Override
  public String getRootPath() {
    return rootPath;
  }

  @Override
  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public String getRegex() {
    return regex;
  }

  @Override
  public void setRegex(String regex) {
    this.regex = regex;
  }

  @Override
  public String getOutFile() {
    return outFile;
  }

  @Override
  public void setOutFile(String outFile) {
    this.outFile = outFile;
  }

  public void validateArgs() {
    // Regex must not be null or empty
    if (regex == null || regex.isEmpty()) {
      throw new IllegalArgumentException("regex is required");
    }
    // Root path must not be null or empty
    if (rootPath == null || rootPath.isEmpty()) {
      throw new IllegalArgumentException("rootPath is required");
    }
    // Root path must be an existing directory
    File root = new File(rootPath);
    if (!root.exists() || !root.isDirectory()) {
      throw new IllegalArgumentException("Root path must be an existing directory: " + rootPath);
    }
    // Out file must not be null or empty
    if (outFile == null || outFile.isEmpty()) {
      throw new IllegalArgumentException("outFile is required");
    }
  }

  public static void main(String[] args) {
    if (args.length != 3) {
      throw new IllegalArgumentException("USAGE: JavaGrep <regex> <rootPath> <OutFile>");
    }
    JavaGrepImpl app =new JavaGrepImpl();
    app.setRegex(args[0]); // regex
    app.setRootPath(args[1]); // root path
    app.setOutFile(args[2]);  //out file
    try{
      app.process();
    }catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(2);
    }
  }
}
