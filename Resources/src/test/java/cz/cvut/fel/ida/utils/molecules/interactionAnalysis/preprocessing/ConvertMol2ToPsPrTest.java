package cz.cvut.fel.ida.utils.molecules.interactionAnalysis.preprocessing;

import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.io.IOException;
import java.text.ParseException;

import static cz.cvut.fel.ida.utils.molecules.preprocessing.ConvertMol2ToPsPr.convertMol2InDir;

class ConvertMol2ToPsPrTest {

    @TestAnnotations.AdHoc
    public void testCovertFile() throws IOException, ParseException {
        String dirPath = "/home/gusta/data/datasets/NCI/aaaa";
        convertMol2InDir(dirPath, "gi50_screen_MDA_MB_231_ATCC_data");
    }

}
