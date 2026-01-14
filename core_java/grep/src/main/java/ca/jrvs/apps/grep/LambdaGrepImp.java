package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LambdaGrepImp extends JavaGrepImpl {

  private static final Logger logger = LoggerFactory.getLogger(LambdaGrepImp.class);

  @Override
  public List<File> listFiles(String rootDir) {
   try{
     return java.nio.file.Files.walk(java.nio.file.Paths.get(rootDir))
         .filter(java.nio.file.Files::isRegularFile)
         .map(java.nio.file.Path::toFile)
         .collect(java.util.stream.Collectors.toList())
     ;
   }catch (IOException e){
     throw new RuntimeException("Unable to list files", e);
   }
  }

  @Override
  public List<String> readLines(File inputFile) throws IOException {
    try (Stream<String> lines = Files.lines(inputFile.toPath())) {
      return lines.collect(Collectors.toList());
    }
  }

  public List<String> grepLines() {
    return listFiles(getRootPath()).stream()
        .flatMap(file -> {
          try {
            // read each line
            return readLines(file).stream()
                .filter(this::containsPattern)
                .map(line -> file.getPath() + ":" + line);
          } catch (IOException e) {
            // logging if failed
            logger.warn("Failed reading file '{}'", file.getPath(), e);
            return Stream.<String>empty();
          }
        })
        .collect(Collectors.toList());
  }

  @Override
  public void process() throws IOException {
    validateArgs();
    logger.info("Starting Lambda grep. rootPath='{}', regex='{}', outFile='{}'",
        getRootPath(), getRegex(), getOutFile());

    List<String> matches = grepLines();

    writeToFile(matches);

    logger.info("Completed. Matched {} line(s). Output -> {}",
        matches.size(), getOutFile());
  }

  public static void main(String[] args) {
    if (args.length != 3) {
      throw new IllegalArgumentException("USAGE: LambdaGrepImp <regex> <rootPath> <outFile>");
    }

    LambdaGrepImp app = new LambdaGrepImp();
    app.setRegex(args[0]);
    app.setRootPath(args[1]);
    app.setOutFile(args[2]);
    try {
      app.process();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(2);
    }
  }
}
