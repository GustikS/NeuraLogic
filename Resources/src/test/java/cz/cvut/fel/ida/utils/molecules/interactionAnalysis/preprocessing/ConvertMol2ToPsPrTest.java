package cz.cvut.fel.ida.utils.molecules.interactionAnalysis.preprocessing;

import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

import static cz.cvut.fel.ida.utils.molecules.preprocessing.ConvertMol2ToPsPr.convertMol2InDir;
import static cz.cvut.fel.ida.utils.molecules.preprocessing.Molecule2csv.exportTypes;
import static cz.cvut.fel.ida.utils.molecules.preprocessing.Molecule2csv.mol2tocsvs;

class ConvertMol2ToPsPrTest {

    @TestAnnotations.AdHoc
    public void testCovertFile() throws IOException, ParseException {
        String dirPath = "/home/gusta/data/datasets/NCI/aaaa";
        convertMol2InDir(dirPath, "gi50_screen_MDA_MB_231_ATCC_data");
    }

    @TestAnnotations.AdHoc
    public void testCovert2csv() throws IOException, ParseException {
        File dir = new File("/home/gusta/data/datasets/jair_original_source");
        for (File file : dir.listFiles()) {
            String name = file.getName();
            if (name.endsWith(".mol2")) {
                System.out.println(name);
                String outDir = name.substring(name.indexOf("screen_") + 7, name.indexOf("_data"));
                mol2tocsvs(file.toString(), Paths.get(dir.toString(), outDir).toString());
                Files.copy(Paths.get(dir.getPath(), name.replace("_data.mol2", "_target.txt")), Paths.get(dir.toString(), outDir,"target.txt"));
            }
        }
        exportTypes(dir.toString());
    }
}