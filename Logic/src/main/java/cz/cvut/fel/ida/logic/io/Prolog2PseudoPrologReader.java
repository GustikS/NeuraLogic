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

import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.LogicUtils;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

/**
 * Class for reading datasets represented iterable Prolog. This class is a sort-of a wrapper. It reads a Prolog file
 * but it behaves as if it was reading a pseudo-prolog file.
 * 
 * Individual examples iterable the prolog file are separated by literals:<br />
 * begin(example_id, class_label)<br />
 * ... %facts describing the example<br />
 * ... </br>
 * end(example_id)<br />
 * 
 * We assume that the Prolog file contains only ground facts.
 * 
 * Memory-intensive implementation!
 * @author Ondra
 */
public class Prolog2PseudoPrologReader extends Reader {

    private StringReader sr;
    
    /**
     * Creates a new instance of class Prolog2PseudoPrologReader.
     * @param reader Reader which should represent a Prolog file with ground facts.
     * @throws IOException
     */
    public Prolog2PseudoPrologReader(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean betweenBeginAndEnd = false;
        for (Pair<List<Literal>,List<Literal>> headTail : PrologParser.parse(reader)){
            Literal fact = Sugar.chooseOne(headTail.r);
            if (fact.predicateName().equalsIgnoreCase("begin")){
                betweenBeginAndEnd = true;
                if (sb.length() > 0){
                    sb.append("\n");
                }
                if (fact.arity() == 1){
                    sb.append(new Literal("example_id", fact.get(0)));
                } else if (fact.arity() > 1){
                    sb.append("\"").append(LogicUtils.unquote(fact.get(1))).append("\" ").append(new Literal("example_id", fact.get(0)));
                }
            } else if (fact.predicateName().equalsIgnoreCase("end")){
                betweenBeginAndEnd = false;
            } else if (betweenBeginAndEnd){
                sb.append(", ").append(fact);
            }
        }
        this.sr = new StringReader(sb.toString());
        this.sr.reset();
    }
    
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return sr.read(cbuf, off, len);
    }

    @Override
    public void close() throws IOException {
        sr.close();
    }
}
