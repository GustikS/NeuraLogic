/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.utils.molecules.preprocessing;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Constant;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.io.PseudoPrologParser;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.math.StringUtils;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.molecules.preprocessing.molecules.Atom;
import cz.cvut.fel.ida.utils.molecules.preprocessing.molecules.Bond;
import cz.cvut.fel.ida.utils.molecules.preprocessing.molecules.Molecule;

import java.io.*;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Ondra (+ Gusta)
 */
public class ConvertMol2ToPsPr {

    private static final Logger LOG = Logger.getLogger(ConvertMol2ToPsPr.class.getName());
    /**
     * to use if there are no inherent ids by the molecules
     */
    public static String defaultMolName = "molecule";

    public static String defaultPredictionTargetName = "predict";

    static Set<String> allAtomTypes = new HashSet<>();
    static Set<String> allBondTypes = new HashSet<>();

    public static int embeddingDim = 3;

    static String atomName = "a";
    static String bondName = "b";

    /**
     * decimal dots vs commas....
     *
     * @param numberString
     * @return
     * @throws ParseException
     */
    public static double parseDoubleProper(String numberString) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        Number number = format.parse(numberString);
        return number.doubleValue();
    }

    /**
     * Do the conversion assuming default file names in the provided dir
     * - we assume there are *.mol2 (*.ids) and (*.q) file besides !!
     *
     * @param dirPath
     * @param filename
     * @throws IOException
     */
    public static void convertMol2InDir(String dirPath, String filename) throws IOException, ParseException {
        FileReader mol2Reader = new FileReader(Paths.get(dirPath, filename + ".mol2").toFile());
        FileReader idsReader = null;
        try {
            idsReader = new FileReader(Paths.get(dirPath, filename + ".ids").toFile());
        } catch (FileNotFoundException e) {
            //pass
        }
        FileReader queryReader = null;
        PrintWriter queriesWriter = null;
        try {
            queryReader = new FileReader(Paths.get(dirPath, filename + ".q").toFile());

            File queriesFile = Paths.get(dirPath, filename + "Queries.txt").toFile();
            queriesWriter = getWriter(queriesFile);
        } catch (FileNotFoundException e) {
            //pass
        }

        PrintWriter examplesWriter = getWriter(Paths.get(dirPath, filename + "Examples.txt").toFile());

        if (queryReader == null) {  //don't care about labels
            convertMol2ToProlog(mol2Reader, examplesWriter);
        } else {
            if (idsReader == null) {    //use default order
                convertMol2ToProlog(mol2Reader, examplesWriter);
                convertClassLabels(queryReader, queriesWriter);
            } else {    //reorder w.r.t. provided molecule ids
                convertMol2ToProlog(mol2Reader, idsReader, queryReader, examplesWriter);
            }
        }
        exportLRNNembeddings(dirPath);
    }

    protected static PrintWriter getWriter(File path) throws IOException {
        File examplesFile = path;
        examplesFile.getParentFile().mkdirs();
        examplesFile.createNewFile();
        return new PrintWriter(examplesFile);
    }

    private static void exportLRNNembeddings(String dirPath) throws IOException {
        PrintWriter atoms = getWriter(new File(dirPath + "/atomEmbeddings"));
        for (String atomType : allAtomTypes) {
            atoms.println("{" + embeddingDim + ",1} atom_embed(A) :- " + atomType + "(A).");
        }
        atoms.println("atom_embed/1 {" + embeddingDim + ",1}");
        atoms.flush();
        atoms.close();

        PrintWriter bonds = getWriter(new File(dirPath + "/bondEmbeddings"));
        for (String bondType : allBondTypes) {
            bonds.println("{" + embeddingDim + ",1} bond_embed(B) :- " + bondType + "(B).");
        }
        bonds.println("bond_embed/1 {" + embeddingDim + ",1}");
        bonds.flush();
        bonds.close();
    }


    /**
     * No labels version - the new version
     *
     * @throws IOException
     * @throws ParseException
     */
    public static void convertMol2ToProlog(FileReader mol2Reader, PrintWriter examplesWriter) throws IOException, ParseException {
        List<Molecule> molecules = readMolecules(mol2Reader);
        for (Molecule mol : molecules) {
            examplesWriter.println(moleculeToLRNNClause(mol) + ".");
        }
        examplesWriter.flush();
        examplesWriter.close();
    }

    /**
     * the old version
     *
     * @param mol2Reader
     * @param idsReader
     * @param classLabelsReader
     * @param writer
     * @throws IOException
     * @throws ParseException
     */
    @Deprecated
    public static void convertMol2ToProlog(Reader mol2Reader, Reader idsReader, Reader classLabelsReader, PrintWriter writer) throws IOException, ParseException {
        Map<String, String> classLabels = readClassLabels(idsReader, classLabelsReader);
        PrintWriter pw = new PrintWriter(writer);
        for (Molecule mol : readMolecules(mol2Reader)) {
            pw.println(classLabels.get(mol.getName()) + " " + moleculeToClause(mol));
        }
        pw.flush();
        pw.close();
    }

    public static Clause moleculeToLRNNClause(Molecule molecule) {

        Set<Literal> literals = new HashSet<Literal>();
        for (Atom atom : molecule.atoms()) {
            String atomType = Constant.construct(atom.getType()).name().replaceAll("\\.", "_").toLowerCase();
            literals.add(new Literal(atomType, Constant.construct(atomName + atom.getName())));
            literals.add(new Literal("charge", Constant.construct(atomName + atom.getName()), Constant.construct(String.valueOf(atom.getCharge()))));
            allAtomTypes.add(atomType);
        }
        for (Bond bond : molecule.bonds()) {
            String bondType = bondName + "_" + Constant.construct(bond.getType()).name();

            literals.add(new Literal("bond", Constant.construct(atomName + bond.getA().getName()), Constant.construct(atomName + bond.getB().getName()), Constant.construct(bondName + bond.getBondId() + "l")));
            literals.add(new Literal(bondType, Constant.construct(bondName + bond.getBondId()+ "l")));
            literals.add(new Literal("bond", Constant.construct(atomName + bond.getB().getName()), Constant.construct(atomName + bond.getA().getName()), Constant.construct(bondName + bond.getBondId() + "r")));
            literals.add(new Literal(bondType, Constant.construct(bondName + bond.getBondId()+ "r")));
            allBondTypes.add(bondType);
        }
        return new Clause(literals);
    }

    @Deprecated
    public static Clause moleculeToClause(Molecule molecule) {
        Set<Literal> literals = new HashSet<Literal>();
        for (Atom atom : molecule.atoms()) {
            literals.add(new Literal("atm", Constant.construct(atom.getName()), Constant.construct(atom.getType()), Constant.construct(String.valueOf(atom.getCharge()))));
        }
        for (Bond bond : molecule.bonds()) {
            literals.add(new Literal("bond", Constant.construct(bond.getA().getName()), Constant.construct(bond.getA().getName()), Constant.construct(bond.getB().getName()), Constant.construct(bond.getB().getName()),
                    Constant.construct(bond.getType())));
            literals.add(new Literal("bond", Constant.construct(bond.getB().getName()), Constant.construct(bond.getB().getName()), Constant.construct(bond.getA().getName()), Constant.construct(bond.getA().getName()),
                    Constant.construct(bond.getType())));
        }
        return new Clause(literals);
    }

    public static void convertClassLabels(Reader idsReader, PrintWriter queriesWriter) throws IOException {
        List<String> lines = Sugar.readLines(idsReader);
        lines.forEach(line -> queriesWriter.println(line + " " + defaultPredictionTargetName + "."));
        queriesWriter.flush();
        queriesWriter.close();
    }

    public static Map<String, String> readClassLabels(Reader idsReader, Reader classLabelsReader) throws IOException {
        Map<String, String> retVal = new HashMap<String, String>();

        Iterator<String> iter1;
        if (idsReader == null) {    //if there is no inherent molecules ids reader, just provide an incremental numbering of molecules
            iter1 = new Iterator<String>() {
                private int counter = 1;

                @Override
                public boolean hasNext() {
                    return true;
                }

                @Override
                public String next() {
                    return defaultMolName + " " + counter++;
                }
            };
        } else {
            List<String> ids = Sugar.readLines(idsReader);
            iter1 = ids.iterator();
        }

        List<String> classLabels = Sugar.readLines(classLabelsReader);

        Iterator<String> iter2 = classLabels.iterator();

        while (iter1.hasNext() && iter2.hasNext()) {
            retVal.put(iter1.next(), iter2.next());
        }
        return retVal;
    }

    public static List<Molecule> readMolecules(Reader mol2Reader) throws IOException, ParseException {
        int counter = 0;
        String line = null;
        BufferedReader br1 = new BufferedReader(mol2Reader);
        LinkedHashMap<String, Molecule> molecules = new LinkedHashMap<>();
        Molecule mol = null;
        final int OTHER = 0, AFTER_TRIPOS_MOLECULE = 1, AFTER_TRIPOS_ATOM = 2,
                AFTER_TRIPOS_BOND = 3;
        int state = OTHER;
        while ((line = br1.readLine()) != null) {
            line = line.trim();
            if (line.length() > 0) {
                if (line.startsWith("@<TRIPOS>BOND")) {
                    state = AFTER_TRIPOS_BOND;
                    continue;
                }
                if (line.startsWith("@<TRIPOS>ATOM")) {
                    state = AFTER_TRIPOS_ATOM;
                    continue;
                }
                if (line.startsWith("@<TRIPOS>MOLECULE")) {
                    if (mol != null) {
                        molecules.put(mol.getName(), mol);
                    }
                    state = AFTER_TRIPOS_MOLECULE;
                    continue;
                }
                if (state == AFTER_TRIPOS_ATOM) {
                    String[] splitted = line.split("[ ]+");
                    Atom atom = new Atom(splitted[0], splitted[5], parseDoubleProper(splitted[8]), parseDoubleProper(splitted[2]), parseDoubleProper(splitted[3]), parseDoubleProper(splitted[4]));
                    mol.addAtom(atom);
                } else if (state == AFTER_TRIPOS_MOLECULE) {
                    mol = new Molecule(line);
                    state = OTHER;
                } else if (state == AFTER_TRIPOS_BOND) {
                    String[] splitted = line.split("[ ]+");
                    Bond bond = new Bond(splitted[0], mol.getAtom(splitted[1]), mol.getAtom(splitted[2]), splitted[3]);
                    mol.addBond(bond);
                }
            }
        }
        molecules.put(mol.getName(), mol);  //don't forget the Last one!
        return new ArrayList<>(molecules.values());
    }

    public static void convertPsPr2Tilde(Reader reader, Writer writer) throws IOException {
        List<Pair<Clause, String>> examples = PseudoPrologParser.read(reader);
        PrintWriter pw = new PrintWriter(writer);
        int index = 0;
        for (Pair<Clause, String> pair : examples) {
            pw.println("begin(model(example_" + index + ")).");
            if (pair.s.equals("+") || pair.s.equals("+1")) {
                pw.println("positive.");
            } else if (pair.s.equals("-") || pair.s.equals("-1")) {
                pw.println("negative.");
            } else {
                pw.println(pair.s + ".");
            }
            List<String> literals = new ArrayList<String>();
            for (Literal l : pair.r.literals()) {
                Literal newLit = new Literal(l.predicate().name, l.arity());
                for (int i = 0; i < l.arity(); i++) {
                    if (StringUtils.isNumeric(l.get(i).name())) {
                        newLit.set(l.get(i), i);
                    } else {
                        newLit.set(Constant.construct(l.get(i).name().toLowerCase().replaceAll("\\.", "")), i);
                    }
                }
                literals.add(newLit + ".");

            }
            Collections.sort(literals);
            for (String line : literals) {
                pw.println(line);
            }
            pw.println("end(model(example_" + index + ")).");
            index++;
        }
        pw.flush();
    }
}
