package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface JavaGrep {
  void process() throws IOException;

  /**
   * List all files under the given directory.
   * @param rootDir input directory
   * @return list of all regular riles
   */
  List<File> listFiles(String rootDir);
  /**
   * Read all lines from a given file.
   *
   * @param inputFile the file to read
   * @return all text lines in that file
   * @throws IOException if reading fails
   */
  List<String> readLines(File inputFile) throws IOException;
  /**
   * Check if a line matches the regex pattern.
   *
   * @param line a single line of text
   * @return true if it matches, false otherwise
   */
  boolean containsPattern(String line);
  /**
   * Write all matched lines to an output file.
   *
   * @param lines lines that matched the regex
   * @throws IOException if writing fails
   */
  void writeToFile(List<String> lines) throws IOException;

  String getRootPath();

  void setRootPath(String rootPath);

  String getRegex();

  void setRegex(String regex);

  String getOutFile();

  void setOutFile(String outFile);
}
