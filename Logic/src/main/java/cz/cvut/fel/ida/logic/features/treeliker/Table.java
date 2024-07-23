/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker;

import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.math.StringUtils;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.collections.MultiList;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.io.*;
import java.util.*;

/**
 * Class for representing attribute-value tables with capability to export its contents to WEKA's arff format.
 * 
 * @param <E> type of examples (it can be e.g. Integer or whatever)
 * @param <A> type of attributes
 * @author Ondra
 */
public class Table<E,A> {

    private int examplesCount;

    private MultiMap<A,String> attributeValues = new MultiMap<A,String>();

    //attribute -> [[example,value]]
    private MultiMap<A, Pair<E,String>> attributes = new MultiMap<A,Pair<E,String>>();

    private MultiMap<A,String> additionalUnseenValues = new MultiMap<A,String>();

    private Map<E,String> classification = new LinkedHashMap<E,String>();

    private Set<E> examples = new HashSet<E>();

    private Set<String> additionalUnseenClassifications = new HashSet<String>();

    private Map<A,Integer> sizes = new LinkedHashMap<A,Integer>();

    private final static String NUMERIC = "NUMERIC";
    
    /**
     * Constructs a new empty Table
     */
    public Table(){

    }

    /**
     * Applies the given function on all attributes and replaces them by the result.
     * @param fun the function to be applied
     */
    public void processAttributes(Sugar.Fun<A,A> fun){
        List<Pair<A,A>> pairs = new ArrayList<Pair<A,A>>();
        for (A t : attributes.keySet()){
            pairs.add(new Pair(t, fun.apply(t)));
        }
        for (Pair<A,A> pair : pairs){
            Set<Pair<E,String>> values = attributes.remove(pair.r);
            attributes.putAll(pair.s, values);
        }
    }

    /**
     * Sets/adds classification of the given example
     * @param example the example for which we want to set its class-label
     * @param classif the class-label
     */
    public void addClassification(E example, String classif){
        examples.add(example);
        classification.put(example, classif);
    }

    /**
     * 
     * @param example example
     * @return classification of <em>example</em>
     */
    public String getClassification(E example){
        return classification.get(example);
    }

    /**
     * Sets/adds value of attribute <em>attribute</em> to <em>value</em> for the example <em>example</em>
     * @param example the example
     * @param attribute the attribute
     * @param value the value
     */
    public void add(E example, A attribute, String value){
        add(example, attribute, value, Integer.MAX_VALUE);
    }

    /**
     Sets/adds value of attribute <em>attribute</em> to <em>value</em> for the example <em>example</em>
     * @param example the example
     * @param attribute the attribute
     * @param value the value
     * @param size size of the attribute (this can play role when attributes are filtered and the smallest - according to these values - are retained).
     */
    public void add(E example, A attribute, String value, int size){
        examples.add(example);
        attributes.put(attribute, new Pair(example, value));
        attributeValues.put(attribute, value);
        sizes.put(attribute, size);
    }

    /**
     * Removes the given attribute from the table (from all examples for which it had a value)
     * @param attribute the attribute to be removed
     */
    public void removeAttribute(A attribute){
        if (attribute instanceof String && attribute.equals("classification")){
            this.classification.clear();
        }
        this.attributes.remove(attribute);
        this.attributeValues.remove(attribute);
        this.sizes.remove(attribute);
    }

    /**
     * 
     * @param attribute attribute for which we want the values for all examples
     * @return Map in which values of the selected attribute for the particular examples are contained.
     * The keys of this map are the examples, the values are the values of the attribute.
     */
    public Map<E,String> getAttributeVector(A attribute){
        return Sugar.<E,String>mapFromPairs(attributes.get(attribute));
    }


    /**
     * 
     * @return set of all attributes contained in this table
     */
    public Set<A> attributes(){
        return attributes.keySet();
    }

    /**
     * 
     * @return set of all examples contained in this table
     */
    public Set<E> examples(){
        return this.examples;
    }

    /**
     * Filters attributes according to redundancy - two attributes are considered redundant if
     * they have the same values for examples in the table.
     * @return set of filtered attributes (when there are redundant attributes, the one with smallest size is retained)
     */
    public Set<A> filteredAttributes(){
        MultiMap<Set<Pair<E,String>>,A> filter = new MultiMap<Set<Pair<E,String>>,A>();
        for (Map.Entry<A,Set<Pair<E,String>>> entry : this.attributes.entrySet()){
            filter.put(entry.getValue(), entry.getKey());
        }
        Set<A> retVal = new HashSet<A>();
        Sugar.MyComparator<A> sizeComparator = new Sugar.MyComparator<A>() {
            @Override
            public boolean isABetterThanB(A a, A b) {
                if (sizes.containsKey(a) && sizes.containsKey(b)){
                    return sizes.get(a) < sizes.get(b) || (sizes.get(a) == sizes.get(b) && a.toString().compareTo(b.toString()) < 0);
                } else {
                    return a.toString().compareTo(b.toString()) < 0;
                }
            }
        };
        for (Set<A> set : filter.values()){
            retVal.add(Sugar.findBest(set, sizeComparator));
        }
        return retVal;
    }

    @Override
    public String toString(){
        StringWriter sw = new StringWriter();
        try {
            save(sw);
        } catch (IOException ioe){

        }
        return sw.toString();
    }

    /**
     * Saves the table into arff (WEKA) format using the given Writer. Redundant 
     * attributes are removed (only one non-redundant attribute is retained for each class
     * of mutually redundant attributes).
     * @param out the writer to which the file should be saved.
     * @throws IOException
     */
    public void save(Writer out) throws IOException {
        List<A> selectedAttributes = Sugar.listFromCollections(filteredAttributes());
        Collections.sort(selectedAttributes, new Comparator<A>(){
            @Override
            public int compare(A o1, A o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        PrintWriter pw1 = new PrintWriter(out);
        printHeader(selectedAttributes, pw1);
        pw1.println("\n@data");
        printRows(selectedAttributes, pw1);
        pw1.flush();
        //System.out.println("(save) number of attributes after filtering: "+selectedAttributes.size());
    }

    /**
     * Makes this table compatiable with the given <em>table</em> - does not work 
     * when the two tables differ in the attributes that they contain - the compatibility
     * is enforced only through adding values which did not occur in this table but occured in the given
     * <em>table</em>. To make two tables <em>a</em>, <em>b</em> really compatible, one needs to call 
     * the method twice: a.makeCompatible(b); and b.makeCompatible(a);
     * 
     * @param table
     */
    public void makeCompatible(Table<E,A> table){
        for (A key : table.attributes.keySet()){
            for (Pair<E,String> pair : table.attributes.get(key)){
                if (!pair.s.equals("?")){
                    this.additionalUnseenValues.put(key, pair.s);
                }
            }
        }
        for (String classLabel : table.classification.values()){
            if (!classLabel.equals("?")){
                this.additionalUnseenClassifications.add(classLabel);
            }
        }
    }

    /**
     * Saves the table without performing filtering of redundant attributes.
     * @param out the writer to which the file should be saved.
     * @throws IOException
     */
    public void saveWithoutFiltering(Writer out) throws IOException {
        Set<Integer> exs = new HashSet<Integer>();
        for (int i = 0; i < examplesCount; i++){
            exs.add(i);
        }
        PrintWriter pw1 = new PrintWriter(out);
        List<A> selectedAttributes = Sugar.listFromCollections(this.attributes.keySet());
        Collections.sort(selectedAttributes, new Comparator<A>(){
            @Override
            public int compare(A o1, A o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        printHeader(selectedAttributes, pw1);
        pw1.println("\n@data");
        printRows(selectedAttributes, pw1);
        pw1.flush();
        //System.out.println("(save) number of attributes after filtering: "+selectedAttributes.size());
    }

    /**
     * Adds content of the given table to this table.
     * @param table the table whose content should be added to this table.
     */
    public void addAll(Table<E,A> table){
        for (A attribute : table.attributes.keySet()){
            for (Pair<E,String> pair : table.attributes.get(attribute)){
                //System.out.println("Adding attribute: "+attribute+" with value "+pair.s+" to example "+pair.r);
                if (table.sizes.containsKey(attribute)){
                    this.add(pair.r, attribute, pair.s, table.sizes.get(attribute));
                } else {
                    this.add(pair.r, attribute, pair.s);
                }
            }
        }
    }

    private void printHeader(List<A> selectedAttributes, PrintWriter pw) throws IOException {
        pw.print("@relation propositionalization\n\n");
        for (A attribute : selectedAttributes){
            pw.print("@attribute '"+attribute.toString().replaceAll("\\'", "\\\\'") +"' "+attributeType(attribute)+"\n");
        }
        if (classification.size() > 0){
            pw.print("@attribute 'classification' "+classificationType()+"\n");
        }
        pw.print("\n");
    }

    private String classificationType(){
        for (String val : Sugar.setFromCollections(this.classification.values())){
            if (!StringUtils.isNumeric(val) && !val.equals("?")){
                return collectionToWekaString(Sugar.setFromCollections(classification.values(), this.additionalUnseenClassifications));
            }
        }
        return NUMERIC;
    }

    private String attributeType(A attribute){
        for (Pair<E,String> pair : attributes.get(attribute)){
            if (!StringUtils.isNumeric(pair.s) && !pair.s.equals("?")){
                return nominalAttribute(attribute);
            }
        }
        return NUMERIC;
    }

    /**
     * 
     * @param attribute attribute we are interested in
     * @return all different values that the given attributes acquires
     */
    public Set<String> getAttributeValues(A attribute){
        return this.attributeValues.get(attribute);
    }

    private String nominalAttribute(A attribute){
        Set<String> valuesSet = new LinkedHashSet<String>();
        for (Pair<E,String> o : attributes.get(attribute)){
            if (!o.s.equals("?")){
                valuesSet.add(o.s);
            }
        }
        valuesSet.addAll(this.additionalUnseenValues.get(attribute));
        return collectionToWekaString(valuesSet);
    }

    /**
     * Adds another possible value for the given attribute.
     * @param attribute the attribute for which the possible value should be added.
     * @param value the value that should be added to possible-values of the selected attribute
     */
    public void addAdditionalUnseenValue(A attribute, String value){
        this.additionalUnseenValues.put(attribute, value);
    }
    
    /**
     * Adds another possible class-label
     * @param value the new possible class-label
     */
    public void addAdditionalUnseenClassification(String value){
        this.additionalUnseenClassifications.add(value);
    }

    private String collectionToWekaString(Set<String> valuesSet){
        List<String> values = Sugar.listFromCollections(valuesSet);
        Collections.sort(values);
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Object value : values){
            String str = value.toString();
            if (str.charAt(0) == '\'' && str.charAt(str.length()-1) == '\''){
                str = str.substring(1, str.length()-1);
            }
            str = str.replaceAll("\'", "\\\'");
            sb.append("'").append(str).append("'");
            sb.append(",");
        }
        sb.delete(sb.length()-1, sb.length());
        sb.append("}");
        return sb.toString();
    }

    private void printRows(List<A> selectedAttributes, PrintWriter pw) throws IOException {
        MultiList<E,String> rows = new MultiList<E,String>();
        for (A t : selectedAttributes){
            for (Pair<E,String> pair : attributes.get(t)){
                rows.put(pair.r, pair.s);
            }
        }
        for (E example : rows.keySet()){
            List<String> row = rows.get(example);
            int index = 0;
            for (String inRow : row){
                if (inRow.charAt(0) == '\'' && inRow.charAt(inRow.length()-1) == '\''){
                    inRow = inRow.substring(1, inRow.length()-1);
                }
                inRow = inRow.replaceAll("\'", "\\\'");
                //missing values
                if (inRow.equals("?")){
                    pw.print("?");
                } else {
                    pw.print("'"+inRow+"'");
                }
                if (index < row.size()-1){
                    pw.print(", ");
                }
                index++;
            }
            if (classification.containsKey(example)){
                String classLabel = classification.get(example);
                if (classLabel.charAt(0) == '\'' && classLabel.charAt(classLabel.length()-1) == '\''){
                    classLabel = classLabel.substring(1,classLabel.length()-1);
                }
                if (classLabel.equals("?")){
                    pw.print(classLabel);
                } else {
                    pw.print(", '"+classLabel+"'");
                }
            }
            pw.print("\n");
        }
    }

    /**
     * 
     * @return number of attributes
     */
    public int countOfAttributes(){
        return attributes.size();
    }

    /**
     * Parses a table from an arff (WEKA) file.
     * @param reader the reader of the arff file
     * @return parsed table (examples are integers, attributes are strings)
     * @throws IOException
     */
    public static Table<Integer,String> load(Reader reader) throws IOException {
        Map<Integer,String> attributesOrdering = new HashMap<Integer,String>();
        Table table = new Table();
        int attributeIndex = 0;
        boolean data = false;
        int example = 0;
        for (String line : Sugar.readLines(reader)){
            line = line.trim();
            if (line.startsWith("@attribute")){
                String attribute = null;
                if (line.contains("'")){
                    attribute = line.substring(line.indexOf("'")+1, line.lastIndexOf("'"));
                } else {
                    attribute = line.substring("@attribute".length()).trim();
                }
                attributesOrdering.put(attributeIndex, attribute);
                attributeIndex++;
            }
            if (line.startsWith("@data")){
                data = true;
            } else if (data && line.length() > 0){
                String[] array = line.split(",");
                for (int i = 0; i < array.length; i++){
                    String attribute = attributesOrdering.get(i);
                    if (!attribute.equalsIgnoreCase("classification")){
                        table.add(example, attribute, StringUtils.unquote(array[i]), array[i].length());
                    } else {
                        table.addClassification(example, StringUtils.unquote(array[i]));
                    }
                }
                example++;
            }
        }
        return table;
    }
    
    /**
     * TYransforms the table and returns the transformed version (the original table is not changed).
     * @param <U> new type of examples
     * @param <V> new type of attributes
     * @param exampleTransformer function for transformation of examples
     * @param attributeTransformer function for transformation of attributes
     * @param valueTransformer function for transforming values of attributes
     * @return transformed table
     */
    public <U,V> Table<U,V> transform(Sugar.Fun<E,U> exampleTransformer, Sugar.Fun<A,V> attributeTransformer, Sugar.Fun<String,String> valueTransformer){
        Table<U,V> newTable = new Table<U,V>();
        for (Map.Entry<A,Set<Pair<E,String>>> entry : attributes.entrySet()){
            for (Pair<E,String> exampleValuePair : entry.getValue()){
                newTable.add(exampleTransformer.apply(exampleValuePair.r), 
                        attributeTransformer.apply(entry.getKey()), 
                        valueTransformer.apply(exampleValuePair.s), 
                        sizes.get(entry.getKey()));
            }
        }
        for (Map.Entry<A,Set<String>> additValue : additionalUnseenValues.entrySet()){
            for (String val : additValue.getValue()){
                newTable.addAdditionalUnseenValue(attributeTransformer.apply(additValue.getKey()), valueTransformer.apply(val));
            }
        }
        for (Map.Entry<E,String> clEntry : classification.entrySet()){
            newTable.addClassification(exampleTransformer.apply(clEntry.getKey()), valueTransformer.apply(clEntry.getValue()));
        }
        for (String additional : additionalUnseenClassifications){
            newTable.addAdditionalUnseenClassification(valueTransformer.apply(additional));
        }
        return newTable;
    }
}