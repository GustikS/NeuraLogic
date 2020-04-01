/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.utils.molecules.preprocessing.molecules;

/**
 *
 * @author Ondra
 */
public class Atom {
    
    private String name;
    
    private String type;
    
    private double charge;
    
    private double x, y, z;
    
    private int hashCode = -1;

    private int usedConstructor;
    
    public Atom(String name, String type){
        this.name = name;
        this.type = type;
        this.usedConstructor = 1;
    }
    
    public Atom(String name, String type, double charge){
        this(name, type);
        this.charge = charge;
        this.usedConstructor = 2;
    }
    
    public Atom(String name, String type, double charge, double x, double y, double z){
        this(name, type, charge);
        this.x = x;
        this.y = y;
        this.z = z;
        this.usedConstructor = 3;
    }
    
    private void computeHashCode(){
        int hash = 5;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 23 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.charge) ^ (Double.doubleToLongBits(this.charge) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        this.hashCode = hash;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if (this.hashCode() != obj.hashCode()){
            return false;
        }
        if (obj instanceof Atom){
            Atom a = (Atom)obj;
            return a.name.equals(this.name) && a.type.equals(this.type) &&
                    a.charge == this.charge && a.x == this.x && a.y == this.y && a.z == this.z;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (this.hashCode == -1){
            computeHashCode();
        }
        return this.hashCode;
    }

    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the charge
     */
    public double getCharge() {
        return charge;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @return the z
     */
    public double getZ() {
        return z;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Atom[");
        sb.append(this.name);
        sb.append(", type = ");
        sb.append(this.type);
        if (this.usedConstructor > 1){
            sb.append(", charge = ");
            sb.append(this.charge);
        }
        if (this.usedConstructor > 2){
            sb.append(", x = ");
            sb.append(this.x);
            sb.append(", y = ");
            sb.append(this.y);
            sb.append(", z = ");
            sb.append(this.z);
        }
        sb.append(")");
        return sb.toString();
    }

}
