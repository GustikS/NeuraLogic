/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * PrologReader.java
 *
 * Created on 24. leden 2008, 22:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ida.ilp.logic.io;

import ida.ilp.logic.Clause;
import ida.ilp.logic.Literal;
import ida.utils.Sugar;
import ida.utils.tuples.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
/**
 * Class for constructs.building Prolog files.
 * @author Ondra
 */
public class PrologParser {
    
    /** Creates a new instance of PrologReader */
    private PrologParser() {
    }
    
    /**
     * Parses a Prolog file into a list of rules and facts.
     * @param reader Reader of the prolog file
     * @param ignoredPredicates predicateNames which should be ignored
     * @return list of pairs head -: body, facts are parsed into pairs iterable which the first element is the fact (i.e. that literal) and the second element is null.
     * @throws IOException
     */
    public static List<Pair<List<Literal>, List<Literal>>> parse(Reader reader, String ...ignoredPredicates) throws IOException {
        final Set<String> ignoredPredicatesSet = Sugar.setFromCollections(Arrays.asList(ignoredPredicates));
        BufferedReader b = new BufferedReader(reader);
        List<Pair<List<Literal>,List<Literal>>> list = new ArrayList<Pair<List<Literal>,List<Literal>>>();
        String line = null;
        String unfinishedLine = null;
        while ((line = b.readLine()) != null){
            line = line.trim();
            if (unfinishedLine != null){
                line = unfinishedLine + line;
                unfinishedLine = null;
            }
            if (line.lastIndexOf('.') == line.length()-1 && line.lastIndexOf('.') != 0){
                try {
                    if (line.trim().length() > 0 && line.indexOf("%") == -1){
                        Pair<List<Literal>,List<Literal>> parsedLine = parseLine(line);
                        if (parsedLine.r != null){
                            parsedLine.r = Sugar.removeNulls(Sugar.funcall(parsedLine.r, new Sugar.Fun<Literal,Literal>(){
                                public Literal apply(Literal t) {
                                    if (t != null && !ignoredPredicatesSet.contains(t.predicateName())){
                                        return t;
                                    } else {
                                        return null;
                                    }
                                }
                            }));
                        }
                        if (parsedLine.s != null){
                            parsedLine.s = Sugar.removeNulls(Sugar.funcall(parsedLine.s, new Sugar.Fun<Literal,Literal>(){
                                public Literal apply(Literal t) {
                                    if (t != null && !ignoredPredicatesSet.contains(t.predicateName())){
                                        return t;
                                    } else {
                                        return null;
                                    }
                                }
                            }));
                        }
                        if ((parsedLine.r != null && parsedLine.r.size() > 0) || (parsedLine.s != null && parsedLine.s.size() > 0)){
                            list.add(parsedLine);
                        }
                    }
                } catch (RuntimeException rte){
                    System.out.println("Exception occured when constructs.building line: "+line);
                    throw rte;
                }
            } else {
                unfinishedLine = line;
            }
        }
        return list;
    }
    
    /**
     * Parses a line iterable Prolog format into a pair head :- body
     * @param line line to be parsed
     * @return pair head -: body, facts are parsed into pairs iterable which the first element is the fact (i.e. that literal) and the second element is null.
     * @throws IOException
     */
    public static Pair<List<Literal>, List<Literal>> parseLine(String line){
        if (line.indexOf("%") != -1){
            line = line.substring(0, line.indexOf("%"));
        }
        line = line.trim();
        if (line.lastIndexOf('.') == line.length()-1){
            line = line.substring(0, line.length()-1);
        }
        if (line.indexOf(":-") != -1){
            String[] split = line.split(":-");
            String head = split[0];
            String tail = split[1];
            return new Pair<List<Literal>, List<Literal>>(Sugar.listFromCollections(Clause.parse(head).literals()), Sugar.listFromCollections(Clause.parse(tail).literals()));
        } else {
            return new Pair<List<Literal>, List<Literal>>(Sugar.listFromCollections(Clause.parse(line).literals()), null);
        }
    }
    
}
