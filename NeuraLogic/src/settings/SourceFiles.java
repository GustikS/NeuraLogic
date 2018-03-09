package settings;

import java.io.FileReader;
import java.nio.file.Path;

/**
 * Created by gusta on 26.3.17.
 */
public class SourceFiles {

    public Path templatePath;
    public Path examplesPath;
    public Path queriesPath;

    public FileReader templateFileReader;
    public FileReader examplesFileReader;
    public FileReader queriesFileReader;
}