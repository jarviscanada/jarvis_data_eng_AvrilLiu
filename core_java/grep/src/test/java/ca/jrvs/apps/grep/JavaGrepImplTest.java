package ca.jrvs.apps.grep;

import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class JavaGrepImplTest {

  @Test
  public void testContainsPattern() {
    JavaGrepImpl grep = new JavaGrepImpl();
    grep.setRegex(".*Romeo.*Juliet.*");

    assertTrue(grep.containsPattern("Romeo meets Juliet today"));
    assertFalse(grep.containsPattern("Romeo alone"));
    assertFalse(grep.containsPattern("Juliet alone"));
  }

  @Test
  public void testListFiles() {
    JavaGrepImpl grep = new JavaGrepImpl();
    grep.setRegex("dummy");
    grep.setRootPath("./data");

    List<File> files = grep.listFiles("./data");

    assertTrue(files.size() > 0);
  }

  @Test
  public void testReadLines() throws IOException {
    JavaGrepImpl grep = new JavaGrepImpl();

    File f = new File("./data/txt/shakespeare.txt");
    List<String> lines = grep.readLines(f);

    assertTrue(lines.size() > 0);

    boolean found = lines.stream().anyMatch(
        l -> l.contains("Romeo") || l.contains("Juliet")
    );

    assertTrue(found);
  }
}
