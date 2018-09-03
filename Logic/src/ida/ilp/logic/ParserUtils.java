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

package ida.ilp.logic;

import ida.utils.tuples.Pair;

import java.util.Map;
/**
 * Class with several useful methods for constructs.building.
 * @author Ondra
 */
public class ParserUtils {
    
    /**
     * Parses term which can be either variable, Constant, PrologList or Function
     * @param c array of characters iterable which the string representration of the term is stored
     * @param start the index at which the string representation of the term to be parsed begins
     * @param endCharacter the char which is at the end of the parsed syntactical element, e.g. ')' for literals and functions or ']' for PrologLists
     * @return pair: the first element is the parsed Term, the second element is the offset of the index to the next token.
     */
    public static Pair<Term,Integer> parseTerm(char[] c, int start, char endCharacter, Map<Variable,Variable> variables, Map<Constant,Constant> constants){
        StringBuilder term = new StringBuilder();
        int roundBrackets = 0;
        int rectBrackets = 0;
        int index = start;
        final int CONSTANT_OR_VARIABLE = 1, FUNCTION = 2, LIST = 3;
        int type = CONSTANT_OR_VARIABLE;
        boolean ignoreNext = false;
        boolean inQuotes = false;
        boolean inDoubleQuotes = false;
        Term retVal = null;
        while (index  < c.length && c[index] == ' '){
            index++;
        }
        int tStart = index;
        while (index < c.length){
            if (c[index] == '\\' && !ignoreNext){
                ignoreNext = true;
            } else {
                if (!inQuotes && !inDoubleQuotes && c[index] == '\'' && !ignoreNext){
                    term.append(c[index]);
                    inQuotes = true;
                } else if (!inQuotes && !inDoubleQuotes && c[index] == '\"' && !ignoreNext){
                    term.append(c[index]);
                    inDoubleQuotes = true;
                } else if (inQuotes && c[index] == '\'' && !ignoreNext){
                    term.append(c[index]);
                    inQuotes = false;
                } else if (inDoubleQuotes && c[index] == '\"' && !ignoreNext){
                    term.append(c[index]);
                    inDoubleQuotes = false;
                } else if (!inQuotes && !inDoubleQuotes && c[index] == '[' && !ignoreNext){
                    if (type == CONSTANT_OR_VARIABLE && index == tStart){
                        type = LIST;
                    }
                    term.append(c[index]);
                    rectBrackets++;
                } else if (!inQuotes && !inDoubleQuotes && c[index] == ']' && rectBrackets > 0 && !ignoreNext){
                    term.append(c[index]);
                    rectBrackets--;
                } else if (!inQuotes && !inDoubleQuotes && c[index] == '(' && !ignoreNext){
                    if (type == CONSTANT_OR_VARIABLE){
                        type = FUNCTION;
                    }
                    term.append(c[index]);
                    roundBrackets++;
                } else if (!inQuotes && !inDoubleQuotes && c[index] == ')' && roundBrackets > 0 && !ignoreNext){
                    term.append(c[index]);
                    roundBrackets--;
                } else if (!inQuotes && !inDoubleQuotes && roundBrackets == 0 && rectBrackets == 0 && 
                        ((c[index] == endCharacter && index == c.length-1) || c[index] == ','/* || c[index] == ' '*/)){
                    break;
                } else if (!Character.isSpaceChar(c[index])){
                    term.append(c[index]);
                }
                ignoreNext = false;
            }
            index++;
        }
        String termString = term.toString().trim();
        switch (type){
            case CONSTANT_OR_VARIABLE:
                //type can only contain alphanumerical characters
                int typeBreak = -1;
                int i = 0;
                for (char ch : termString.toCharArray()){
                    if (ch == ':') {
                        typeBreak = i;
                        break;
                    } else if (!Character.isLetterOrDigit(ch)){
                        break;
                    }
                    i++;
                }
                String typeString = null;
                if (typeBreak != -1){
                    typeString = termString.substring(0, typeBreak);
                    termString = termString.substring(typeBreak+1);
                }
                if (termString.length() == 0){
                    retVal = Constant.construct("", typeString);
                } else if (Character.isUpperCase(termString.charAt(0)) || termString.charAt(0) == '_'){
                    Variable var = Variable.construct(termString, typeString);
                    if ((retVal = variables.get(var)) == null){
                        variables.put(var, var);
                        retVal = var;
                    }
                } else {
                    Constant constant = Constant.construct(termString, typeString);
                    if ((retVal = constants.get(constant)) == null){
                        constants.put(constant, constant);
                        retVal = constant;
                    }
                }
                break;
            case LIST:
                PrologList list = PrologList.parseList(termString, variables, constants);
                retVal = list;
                break;
            case FUNCTION:
                Function f = Function.parseFunction(termString, variables, constants);
                retVal = f;
                break;
        }
        return new Pair<Term,Integer>(retVal, index);
    }

    public static void main(String[] args){
        System.out.println(Literal.parseLiteral("pred(X , Y)"));
    }
}