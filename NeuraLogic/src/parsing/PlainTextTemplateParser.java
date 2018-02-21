package parsing;

import constructs.example.WeightedFact;
import constructs.template.Atom;
import constructs.template.Template;
import constructs.template.WeightedRule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gusta on 8.3.17.
 */
public class PlainTextTemplateParser implements TemplateParser {
    String comment = "#";
    private String lineEnd = ".";

    @Override
    public boolean isValid(String input) {
        return false;
    }

    public Template parseTemplate(Reader reader) throws IOException {
        Set<WeightedRule> rules = new HashSet<>();
        Set<WeightedFact> facts = new HashSet<>();
        Set<Atom> atoms = new HashSet<>();

        BufferedReader br = new BufferedReader(reader);
        String line = null;
        String unfinishedLine = null;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith(comment)) {
                continue;
            }
            if (unfinishedLine != null) {
                line = unfinishedLine + line;
                unfinishedLine = null;
            }
            if (line.lastIndexOf(lineEnd) == line.length() - 1 && line.lastIndexOf(lineEnd) != 0) {
                if (line.contains(":-")) {
                    WeightedRule wr = parseRule(line);
                    atoms.addAll(wr.getAllAtoms());
                } else if (line.contains("/")) {
                    atoms.add(parseAtomSpecification(line));
                } else {
                    facts.addAll(parseFacts(line));
                }
            } else {
                unfinishedLine = line;
            }
        }
        return null;
    }

    private Collection<? extends WeightedFact> parseFacts(String line) {
    }

    private Atom parseAtomSpecification(String line) {
    }

    private WeightedRule parseRule(String line) {
    }

}