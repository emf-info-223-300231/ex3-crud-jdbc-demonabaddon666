package app.workers;

import app.beans.Personne;
import app.exceptions.MyDBException;
import java.util.List;

public interface DbWorkerItf {

  void connecterBdMySQL( String nomDB ) throws MyDBException;
  void connecterBdHSQLDB( String nomDB ) throws MyDBException;
  void connecterBdAccess( String nomDB ) throws MyDBException;
  void deconnecter() throws MyDBException; 

  void creer(Personne federer) throws MyDBException;

  Personne lire(int lastPK) throws MyDBException;

  void modifier(Personne p1) throws MyDBException;

  void effacer(Personne p);

  List<Personne> lirePersonnes() throws MyDBException;
}
