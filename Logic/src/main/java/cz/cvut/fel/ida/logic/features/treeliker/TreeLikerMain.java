/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.PrologList;
import cz.cvut.fel.ida.logic.io.Prolog2PseudoPrologReader;
import cz.cvut.fel.ida.utils.math.CommandLine;
import cz.cvut.fel.ida.utils.math.StringUtils;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.VectorUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
/**
 * Class providing the command-line interface to algorithms RelF, HiFi and Poly.
 * 
 * @author admin
 */
public class TreeLikerMain {

    /**
     * 
     * @param a
     * @throws IOException
     */
    public static void main(String a[]) throws IOException {
        Map<String,String> params = CommandLine.parseParams(a);
        if (params.containsKey("-batch")){
            BufferedReader br = new BufferedReader(new FileReader(params.get("-batch")));
            Map<String,String> batchParams = new HashMap<String,String>();
            Propositionalization prop = new Propositionalization();
            int maxSize = Integer.MAX_VALUE;
            int mode = Propositionalization.RELF;
            final int PSEUDOPROLOG = 1, PROLOG = 2;
            int inputType = PSEUDOPROLOG;
            for (String line : Sugar.readLines(br)){
                line = line.trim();
                if (line.indexOf('.') > -1 && line.lastIndexOf('.') == line.length()-1){
                    line = line.substring(0, line.length()-1);
                }
                if (line.indexOf("%") > -1){
                    line = line.substring(0, line.indexOf("%"));
                }
                if (line.length() > 0){
                    Clause c  = Clause.parse(line);
                    for (Literal l : c.literals()){
                        if (l.predicate().name.equalsIgnoreCase("print")){
                            System.out.println(">> "+l.get(0).toString());
                        } else if (l.predicate().name.equalsIgnoreCase("get")){
                            String usrInput = CommandLine.read();
                            batchParams.put(l.get(0).name(), usrInput);
                        } else if (l.predicate().name.equalsIgnoreCase("set")){
                            if (l.get(0).name().equals("template")){
                                PrologList pl = (PrologList)l.get(1);
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < pl.countItems(); i++){
                                    sb.append(pl.get(i));
                                    if (i < pl.countItems()-1){
                                        sb.append(", ");
                                    }
                                }
                                if (sb.charAt(0) == '\'' && sb.charAt(sb.length()-1) == '\''){
                                    batchParams.put(l.get(0).name(), sb.substring(1, sb.length()-1));
                                } else {
                                    batchParams.put(l.get(0).name(), sb.toString());
                                }
                            } else {
                                if (l.get(1).name().charAt(0) == '\'' && l.get(1).name().charAt(l.get(1).name().length()-1) == '\''){
                                    batchParams.put(l.get(0).name(), l.get(1).name().substring(1, l.get(1).name().length()-1));
                                } else {
                                    batchParams.put(l.get(0).name(), l.get(1).name().toString());
                                }
                            }
                        } else if (l.predicate().name.equalsIgnoreCase("work") || l.predicate().name.equalsIgnoreCase("whatever")){
                            String examples = batchParams.get("examples");
                            //if (!new File(examples).exists()){
                                examples = new File(params.get("-batch")).getAbsoluteFile().getParent()+File.separator+examples;
                            //}
                            String template = batchParams.get("template");
                            if (template == null){
                                System.out.println("Template was not found in "+params.get("-batch"));
                            }
                            String cv = batchParams.get("output_type");
                            if (cv == null){
                                cv = "single";
                            }
                            String hreduction = batchParams.get("use_hreduction");
                            if (hreduction == null){
                                hreduction = batchParams.get("use_h_reduction");
                            }
                            if (hreduction != null && hreduction.equalsIgnoreCase("true")){
                                TreeLikerSettings.USE_H_REDUCTION = true;
                            } else if (hreduction != null && hreduction.equalsIgnoreCase("false")){
                                TreeLikerSettings.USE_H_REDUCTION = false;
                            }
                            String redundancy = batchParams.get("use_redundancy");
                            if (redundancy != null && redundancy.equalsIgnoreCase("true")){
                                TreeLikerSettings.USE_REDUNDANCY_FILTERING = true;
                            } else if (redundancy != null && redundancy.equalsIgnoreCase("false")){
                                TreeLikerSettings.USE_REDUNDANCY_FILTERING = false;
                            }
                            String coveredClass = batchParams.get("covered_class");
                            String output = batchParams.get("output");
                            if (output == null){
                                System.out.println("Parameter output, which is where the propositionalized table(s) should be stored, is missing in "+params.get("-batch"));
                                System.exit(0);
                            }
                            if (!new File(output).exists()){
                                output = new File(params.get("-batch")).getAbsoluteFile().getParent()+File.separator+output;
                            }
                            String minFrequency = batchParams.get("min_frequency");
                            if (minFrequency == null){
                                minFrequency = batchParams.get("minimum_frequency");
                            }
                            if (minFrequency != null && StringUtils.isNumeric(minFrequency)){
                                prop.setMinFrequency(Double.parseDouble(minFrequency));
                            }
                            String regressionRedundancyTolerance = batchParams.get("regression_redundancy_tolerance");
                            if (regressionRedundancyTolerance != null){
                                if (StringUtils.isInteger(regressionRedundancyTolerance)){
                                    TreeLikerSettings.REGRESSION_REDUNDANCY_TOLERANCE = Integer.parseInt(regressionRedundancyTolerance);
                                } else {
                                    System.out.println("Parameter regression_redundancy_tolerance must be an integer.");
                                }
                            }
                            String algorithm = batchParams.get("algorithm");
                            if (algorithm != null && algorithm.equalsIgnoreCase("relf")){
                                mode = Propositionalization.RELF;
                            } else if (algorithm != null && (algorithm.equalsIgnoreCase("relf_grounding_counting") || algorithm.equalsIgnoreCase("relf_groundings_counting"))){
                                mode = Propositionalization.RELF_GROUNDING_COUNTING;
                            } else if (algorithm != null && algorithm.equalsIgnoreCase("hifi")){
                                mode = Propositionalization.HIFI;
                            } else if (algorithm != null && (algorithm.equalsIgnoreCase("hifi_grounding_counting") || algorithm.equalsIgnoreCase("hifi_groundings_counting"))){
                                mode = Propositionalization.HIFI_GROUNDING_COUNTING;
                            } else if (algorithm != null && algorithm.equalsIgnoreCase("poly")){
                                mode = Propositionalization.POLY;
                            } else if (algorithm != null && (algorithm.equalsIgnoreCase("poly_grounding_counting") || algorithm.equalsIgnoreCase("poly_groundings_counting"))){
                                mode = Propositionalization.POLY_GROUNDING_COUNTING;
                            }
                            if (batchParams.containsKey("use_sampling")){
                                if (batchParams.get("use_sampling").equalsIgnoreCase("true")){
                                    prop.setUseSampling(true);
                                } else {
                                    prop.setUseSampling(false);
                                }
                            }
                            if (batchParams.containsKey("num_samples") && StringUtils.isInteger(batchParams.get("num_samples"))){
                                prop.setNumSamples(Integer.parseInt(batchParams.get("num_samples")));
                            }
                            if (batchParams.containsKey("sample_size") && StringUtils.isInteger(batchParams.get("sample_size"))){
                                prop.setSampleSize(Integer.parseInt(batchParams.get("sample_size")));
                            }
                            boolean useJustFirstExample = false;
                            if (batchParams.containsKey("use_just_first_example") && batchParams.get("use_just_first_example").equalsIgnoreCase("true")){
                                useJustFirstExample = true;
                            }
                            String size = batchParams.get("max_size");
                            if (size == null){
                                size = batchParams.get("maximum_size");
                            }
                            String normalizationFactor = batchParams.get("normalization_factor");
                            if (normalizationFactor != null){
                                String normF = normalizationFactor;
                                if (normF.contains("[")){
                                    normF = normF.substring(normF.indexOf("[")+1);
                                }
                                if (normF.contains("]")){
                                    normF = normF.substring(0, normF.lastIndexOf("]"));
                                }
                                prop.setNormalizationFactor(Block.parse(normF));
                            }
                            
                            if (batchParams.containsKey("verbosity")){
                                TreeLikerSettings.VERBOSITY = Integer.parseInt(batchParams.get("verbosity").trim());
                            }
                            if (batchParams.containsKey("max_degree")){
                                prop.setMaxDegree(Integer.parseInt(batchParams.get("max_degree")));
                            }
                            if (size != null){
                                if (StringUtils.isNumeric(size)){
                                    maxSize = (int)Double.parseDouble(size);
                                } else {
                                    System.out.println("Warning: max_size must be an integer!");
                                }
                            }
                            String inputFormat = batchParams.get("input_format");
                            if (inputFormat != null){
                                if (inputFormat.equalsIgnoreCase("pseudoprolog") || inputFormat.equalsIgnoreCase("pseudo_prolog")){
                                    inputType = PSEUDOPROLOG;
                                } else if (inputFormat.equalsIgnoreCase("prolog")){
                                    inputType = PROLOG;
                                }
                            }
                            String foldsCount = batchParams.get("folds_count");
                            if (foldsCount == null){
                                foldsCount = batchParams.get("num_folds");
                            }
                            prop.setConstructFeaturesOnlyFromFirstExample(useJustFirstExample);
                            long m1 = System.currentTimeMillis();
                            if (cv.equalsIgnoreCase("single")){
                                if (examples == null){
                                    System.out.println("Parameter examples, which points to the file with examples, is missing in "+params.get("-batch"));
                                    System.exit(0);
                                }
                                File outputFile = new File(output);
                                File parent = outputFile.getAbsoluteFile().getParentFile();
                                if (parent != null && !parent.exists()){
                                    parent.mkdirs();
                                }
                                Reader examplesReader = new FileReader(examples);
                                if (inputType == PROLOG){
                                    examplesReader = new Prolog2PseudoPrologReader(examplesReader);
                                }
                                if (mode == Propositionalization.RELF){
                                    if (coveredClass != null){
                                        prop.setRelfCoveredClasses(Sugar.<String>set(coveredClass));
                                    }
                                    Table<String,String> table = prop.relf(template, examplesReader);
                                    if (TreeLikerSettings.USE_REDUNDANCY_FILTERING){
                                        table.save(new FileWriter(output));
                                    } else {
                                        table.saveWithoutFiltering(new FileWriter(output));
                                    }
                                } else if (mode == Propositionalization.POLY || mode == Propositionalization.POLY_GROUNDING_COUNTING){
                                    Table<String,String> table = prop.poly(template, maxSize, examplesReader);
                                    
                                    if (TreeLikerSettings.USE_REDUNDANCY_FILTERING){
                                        table.save(new FileWriter(output));
                                    } else {
                                        table.saveWithoutFiltering(new FileWriter(output));
                                    }
                                } else if (mode == Propositionalization.RELF_GROUNDING_COUNTING){
                                    if (coveredClass != null){
                                        prop.setRelfCoveredClasses(Sugar.<String>set(coveredClass));
                                    }
                                    Table<String,String> table = prop.relf(template, examplesReader, Propositionalization.RELF_GROUNDING_COUNTING);
                                    if (TreeLikerSettings.USE_REDUNDANCY_FILTERING){
                                        table.save(new FileWriter(output));
                                    } else {
                                        table.saveWithoutFiltering(new FileWriter(output));
                                    }
                                } else if (mode == Propositionalization.HIFI || mode == Propositionalization.HIFI_GROUNDING_COUNTING){
                                    Table<String,String> table = prop.hifi(template, maxSize, examplesReader, mode);
                                    if (TreeLikerSettings.USE_REDUNDANCY_FILTERING){
                                        table.save(new FileWriter(output));
                                    } else {
                                        table.saveWithoutFiltering(new FileWriter(output));
                                    }
                                }
                            } else if (cv.equalsIgnoreCase("train_test") || cv.equalsIgnoreCase("traintest")){
                                if (mode == Propositionalization.RELF || mode == Propositionalization.RELF_GROUNDING_COUNTING){
                                    if (coveredClass != null){
                                        prop.setRelfCoveredClasses(Sugar.<String>set(coveredClass));
                                    }
                                }
                                String trainSet = batchParams.get("train_set");
                                String testSet = batchParams.get("test_set");
                                if (trainSet != null && testSet != null){
                                    if (trainSet.startsWith("[") && testSet.startsWith("[")){
                                        Reader examplesReader = new FileReader(examples);
                                        if (inputType == PROLOG){
                                            examplesReader = new Prolog2PseudoPrologReader(examplesReader);
                                        }
                                        prop.trainTest(template, maxSize, VectorUtils.parseIntegerArray(trainSet), VectorUtils.parseIntegerArray(testSet), examplesReader, output, mode);
                                    } else {
                                        String trainSetPath = new File(params.get("-batch")).getAbsoluteFile().getParent()+File.separator+trainSet;
                                        String testSetPath = new File(params.get("-batch")).getAbsoluteFile().getParent()+File.separator+testSet;
                                        Reader trainSetReader = new FileReader(trainSetPath);
                                        Reader testSetReader = new FileReader(testSetPath);
                                        if (inputType == PROLOG){
                                            trainSetReader = new Prolog2PseudoPrologReader(trainSetReader);
                                            testSetReader = new Prolog2PseudoPrologReader(testSetReader);
                                        }
                                        prop.trainTest(template, maxSize, trainSetReader, testSetReader, output, mode);
                                    }
                                }
                            } else if (cv.equalsIgnoreCase("cv") || cv.equalsIgnoreCase("cross-validation")){
                                if (examples == null){
                                    System.out.println("Parameter examples, which points to the file with examples, is missing in "+params.get("-batch"));
                                    System.exit(0);
                                }
                                if (mode == Propositionalization.RELF || mode == Propositionalization.RELF_GROUNDING_COUNTING){
                                    if (coveredClass != null){
                                        prop.setRelfCoveredClasses(Sugar.<String>set(coveredClass));
                                    }
                                }
                                if (foldsCount != null){
                                    if (StringUtils.isInteger(foldsCount)){
                                        prop.setFoldsCount(Integer.parseInt(foldsCount));
                                    } else {
                                        System.out.println("Warning: Number of folds must be an integer! Ignoring this setting.");
                                    }
                                }
                                Reader examplesReader = new FileReader(examples);
                                if (inputType == PROLOG){
                                    examplesReader = new Prolog2PseudoPrologReader(examplesReader);
                                }
                                prop.cv(template, maxSize, examplesReader, output, mode);
                            }
                            long m2 = System.currentTimeMillis();
                            System.out.println("Finished in "+(m2-m1)/1000.0+" seconds");
                        }
                    }
                }
            }
        } else {
            System.out.println("Example usage: java -jar -Xmx1G TreeLiker.jar -batch settingsfile.treeliker");
        }
    }

}
