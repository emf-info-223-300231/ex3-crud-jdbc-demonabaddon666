package app.workers;

import app.beans.Personne;
import app.exceptions.MyDBException;

import java.util.ArrayList;
import java.util.List;

public class PersonneManager {
    private int index = 0;
    private List<Personne> listePersonnes;

    public PersonneManager(List<Personne> personnes) {
        listePersonnes = personnes;
    }

    public Personne courantPersonne(){
        return listePersonnes.get(index);
    }
    public Personne debutPersonne(){
        return listePersonnes.get(index = 0);
    }

    public Personne finPersonne(){
        return listePersonnes.get(index = listePersonnes.size()-1);
    }

    public Personne PrecedentPersonne() throws MyDBException{
        return listePersonnes.get(index > 0 ? (index = index-1) : 0);
    }

    public Personne suivantPersonne() throws MyDBException {
        return listePersonnes.get(index < listePersonnes.size()-1 ? (index = index+1) : index);
    }

    public void setPersonnes(List<Personne> p){
        listePersonnes = p;
    }
}
