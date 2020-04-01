/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.utils.molecules.preprocessing.molecules;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Ondra
 */
public class Molecule {
    
    private String name;
    
    private TreeMap<String,Atom> atoms = new TreeMap<String,Atom>();
    
    private Set<Bond> bonds = new LinkedHashSet<Bond>();
    
    private int hashCode = -1;
    
    public Molecule(String name){
        this.name = name;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void addAtom(Atom atom){
        this.hashCode = -1;
        this.atoms.put(atom.getName(), atom);
    }
    
    public void addBond(Bond bond){
        this.hashCode = -1;
        this.bonds.add(bond);
    }
    
    public Collection<Atom> atoms(){
        return this.atoms.values();
    }
    
    public Atom getAtom(String name){
        return this.atoms.get(name);
    }
    
    public Collection<Bond> bonds(){
        return this.bonds;
    }
    

    @Override
    public int hashCode() {
        if (this.hashCode == -1){
            int hash = 5;
            hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 79 * hash + (this.atoms != null ? this.atoms.hashCode() : 0);
            hash = 79 * hash + (this.bonds != null ? this.bonds.hashCode() : 0);
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
        final Molecule other = (Molecule) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.atoms != other.atoms && (this.atoms == null || !this.atoms.equals(other.atoms))) {
            return false;
        }
        if (this.bonds != other.bonds && (this.bonds == null || !this.bonds.equals(other.bonds))) {
            return false;
        }
        return true;
    }

    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Molecule[");
        sb.append(this.name);
        sb.append(", ");
        sb.append(atoms);
        sb.append(", ");
        sb.append(bonds);
        sb.append("]");
        return sb.toString();
    }
    
}
