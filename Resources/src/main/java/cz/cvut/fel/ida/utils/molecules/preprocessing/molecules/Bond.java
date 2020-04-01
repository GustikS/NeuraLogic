/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.utils.molecules.preprocessing.molecules;

/**
 *
 * @author Ondra
 */
public class Bond {
    
    private Atom a;
    
    private Atom b;
    
    private String type;
    
    private int hashCode;

    private String bondId;
    
    public Bond(String bondId, Atom a, Atom b, String type){
        this.bondId = bondId;
        this.a = a;
        this.b = b;
        this.type = type;
    }

    /**
     * @return the a
     */
    public Atom getA() {
        return a;
    }

    /**
     * @return the b
     */
    public Atom getB() {
        return b;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Bond[");
        sb.append(this.a);
        sb.append(", ");
        sb.append(this.b);
        sb.append(", ");
        sb.append(this.type);
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == -1){
            int hash = 5;
            hash = 97 * hash + (this.a != null ? this.a.hashCode() : 0);
            hash = 97 * hash + (this.b != null ? this.b.hashCode() : 0);
            hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
            this.hashCode = hash;
        }
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Bond other = (Bond) obj;
        if (this.a != other.a && (this.a == null || !this.a.equals(other.a))) {
            return false;
        }
        if (this.b != other.b && (this.b == null || !this.b.equals(other.b))) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        return true;
    }


    public String getBondId() {
        return bondId;
    }
}
