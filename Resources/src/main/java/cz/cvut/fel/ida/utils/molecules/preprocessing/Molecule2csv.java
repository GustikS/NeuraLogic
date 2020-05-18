package cz.cvut.fel.ida.utils.molecules.preprocessing;

import cz.cvut.fel.ida.utils.molecules.preprocessing.molecules.Atom;
import cz.cvut.fel.ida.utils.molecules.preprocessing.molecules.Bond;
import cz.cvut.fel.ida.utils.molecules.preprocessing.molecules.Molecule;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

public class Molecule2csv extends ConvertMol2ToPsPr {
    private static final Logger LOG = Logger.getLogger(Molecule2csv.class.getName());

    public static void mol2tocsvs(String filename, String outDir) throws IOException, ParseException {
        File sourceFile = Paths.get(filename).toFile();

        FileReader mol2Reader = new FileReader(sourceFile);
        List<Molecule> molecules = readMolecules(mol2Reader);
        collectTypes(molecules);

        PrintWriter atom_types = getWriter(Paths.get(outDir, "atom_type.csv").toFile());
        PrintWriter bond_types = getWriter(Paths.get(outDir, "bond_type.csv").toFile());
        PrintWriter bonds = getWriter(Paths.get(outDir, "bond.csv").toFile());

        for (Molecule mol : molecules) {
            for (Atom atom : mol.atoms()) {
                atom_types.write(makeCSVline("", sanitize(atomName, atom.getName()), sanitize(atom.getType())));
            }
            atom_types.write("---\n");
            for (Bond bond : mol.bonds()) {
                bond_types.write(makeCSVline("", sanitize(bondName, bond.getBondId(), "l"), sanitize(bondName, "_", bond.getType())));
                bond_types.write(makeCSVline("", sanitize(bondName, bond.getBondId(), "r"), sanitize(bondName, "_", bond.getType())));
            }
            bond_types.write("---\n");
            for (Bond bond : mol.bonds()) {
                bonds.write(makeCSVline("", sanitize(atomName, bond.getA().getName()), sanitize(atomName, bond.getB().getName()), sanitize(bondName, bond.getBondId(), "l")));
                bonds.write(makeCSVline("", sanitize(atomName, bond.getB().getName()), sanitize(atomName, bond.getA().getName()), sanitize(bondName, bond.getBondId(), "r")));
            }
            bonds.write("---\n");
        }
        atom_types.close();
        bond_types.close();
        bonds.close();
    }

    private static String makeCSVline(String... cols) {
        StringBuilder sb = new StringBuilder();
        for (String col : cols) {
            sb.append(col);
            sb.append(";");
        }
        sb.append("\n");
        return sb.toString();
    }

    private static void collectTypes(List<Molecule> molecules) {
        for (Molecule molecule : molecules) {
            for (Atom atom : molecule.atoms()) {
                String atomType = sanitize(atom.getType());
                allAtomTypes.add(atomType);
            }
            for (Bond bond : molecule.bonds()) {
                String bondType = sanitize(bondName, "_", bond.getType());
                allBondTypes.add(bondType);
            }
        }
    }

    public static void exportTypes(String outDir) throws IOException {
        PrintWriter atoms = getWriter(Paths.get(outDir, "atom_types.txt").toFile());
        for (String atype : allAtomTypes) {
            atoms.write(atype);
            atoms.println();
        }
        atoms.close();
        PrintWriter bonds = getWriter(Paths.get(outDir, "bond_types.txt").toFile());
        for (String btype : allBondTypes) {
            bonds.write(btype);
            bonds.println();
        }
        bonds.close();
    }

    private static String sanitize(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part.replaceAll("\\.", "_").toLowerCase());
        }
        return sb.toString();
    }
}
