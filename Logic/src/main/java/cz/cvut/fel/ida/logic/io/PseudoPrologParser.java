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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.io;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for constructs.building pseudo-prolog files. Pseudo-prolog files contain examples on separate lines -
 * one line = one example, the first non-space token iterable the line is class label of the example.
 * <br />
 * <br />
 * An example of a pseudo-prolog file is the following:<br />
 * <br />
 * east hasCar(c), hasLoad(c,l), box(l)<br />
 * west hasCar(c), hasLoad(c,l), tri(l)<br />
 * <br />
 * Note that the examples do not share any information so it is no problem to use the same constants to
 * refer to different objects iterable different examples.
 * 
 * @author Ondra
 */
public class PseudoPrologParser {
 
    /**
     * Parses a given file with examples represented iterable pseudo-prolog .
     * @param reader reader for the pseudo-prolog file
     * @return list of pairs: the first element iterable a pair is the parsed example (and instance of class Clause)
     * and the second element is the class-label of that example.
     * @throws IOException
     */
    public static List<Pair<Clause,String>> read(Reader reader) throws IOException {
        List<Pair<Clause,String>> retVal = new ArrayList<Pair<Clause,String>>();
        String line = null;
        BufferedReader br = new BufferedReader(reader);
        while ((line = br.readLine()) != null){
            line = line.trim();
            if (line.length() > 1 && (line.indexOf(" ") != -1 || line.indexOf("\"") != -1 || line.indexOf("\'") != -1)){
                String classification = null;
                String clausePart = null;
                if (line.charAt(0) == '"'){
                    int indexOfSecondQuote = line.indexOf("\"", 1);
                    classification = line.substring(1, indexOfSecondQuote);
                    clausePart = line.substring(indexOfSecondQuote+1);
                } else if (line.charAt(0) == '\''){
                    int indexOfSecondQuote = line.indexOf("'", 1);
                    classification = line.substring(1, indexOfSecondQuote);
                    clausePart = line.substring(indexOfSecondQuote+1);
                } else {
                    classification = line.substring(0, line.indexOf(" "));
                    clausePart = line.substring(line.indexOf(" ")+1);
                }
                retVal.add(new Pair<Clause,String>(Clause.parse(clausePart), classification));
            }
        }
        return retVal;
    }
    
}
