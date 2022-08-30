package app.presentation;

import app.beans.Personne;
import app.exceptions.MyDBException;
import app.helpers.DateTimeLib;
import app.helpers.JfxPopup;
import app.workers.DbWorker;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import app.workers.PersonneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import java.io.File;
import app.workers.DbWorkerItf;
import javafx.application.Platform;

/**
 *
 * @author PA/STT
 */
public class MainCtrl implements Initializable {

  // DBs à tester
  private enum TypesDB {
    MYSQL, HSQLDB, ACCESS
  };

  // DB par défaut
  final static private TypesDB DB_TYPE = TypesDB.MYSQL;

  private DbWorkerItf dbWrk;
  private PersonneManager manPers;
  private boolean modeAjout;

  @FXML
  private TextField txtNom;
  @FXML
  private TextField txtPrenom;
  @FXML
  private TextField txtPK;
  @FXML
  private TextField txtNo;
  @FXML
  private TextField txtRue;
  @FXML
  private TextField txtNPA;
  @FXML
  private TextField txtLocalite;
  @FXML
  private TextField txtSalaire;
  @FXML
  private CheckBox ckbActif;
  @FXML
  private Button btnDebut;
  @FXML
  private Button btnPrevious;
  @FXML
  private Button btnNext;
  @FXML
  private Button btnEnd;
  @FXML
  private Button btnSauver;
  @FXML
  private Button btnAnnuler;
  @FXML
  private DatePicker dateNaissance;

  private enum Option{
    CREATE,MODIFY,OTH
  }
  private Option opt = Option.OTH;

  /*
   * METHODES NECESSAIRES A LA VUE
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    dbWrk = new DbWorker();
    ouvrirDB();
    try {
      manPers = new PersonneManager(dbWrk.lirePersonnes());
      afficherPersonne(manPers.PrecedentPersonne());
    } catch (MyDBException e) {
    }
  }

  @FXML
  public void actionPrevious(ActionEvent event) {
    try {
      afficherPersonne(manPers.PrecedentPersonne());
    } catch (MyDBException ex) {
      JfxPopup.displayError("ERREUR", "Une erreur s'est produite", ex.getMessage());
    }
  }

  @FXML
  public void actionNext(ActionEvent event) {
    try {
      afficherPersonne(manPers.suivantPersonne());
    } catch (MyDBException ex) {
      JfxPopup.displayError("ERREUR", "Une erreur s'est produite", ex.getMessage());
    }
  }
  
  @FXML
  private void actionEnd(ActionEvent event) {
    afficherPersonne(manPers.finPersonne());
  }

  @FXML
  private void debut(ActionEvent event) {
    afficherPersonne(manPers.debutPersonne());
  }

  @FXML
  private void menuAjouter(ActionEvent event) {
    btnSauver.setVisible(true);
    btnNext.setVisible(false);
    btnDebut.setVisible(false);
    btnPrevious.setVisible(false);
    btnEnd.setVisible(false);
    opt = Option.CREATE;
  }
  

  @FXML
  private void menuModifier(ActionEvent event) {
    btnSauver.setVisible(true);
    btnNext.setVisible(false);
    btnDebut.setVisible(false);
    btnPrevious.setVisible(false);
    btnEnd.setVisible(false);
    opt = Option.MODIFY;
  }

  @FXML
  private void menuEffacer(ActionEvent event) {
    btnAnnuler.setVisible(true);
    btnAnnuler.setText("Effacer");
    btnNext.setVisible(false);
    btnDebut.setVisible(false);
    btnPrevious.setVisible(false);
    btnEnd.setVisible(false);
  }

  @FXML
  private void menuQuitter(ActionEvent event) {
    quitter();
  }

  @FXML
  private void annulerPersonne(ActionEvent event) {
    try {
      dbWrk.effacer(manPers.courantPersonne());
      btnSauver.setVisible(false);
      btnNext.setVisible(true);
      btnDebut.setVisible(true);
      btnPrevious.setVisible(true);
      btnEnd.setVisible(true);
      manPers.setPersonnes(dbWrk.lirePersonnes());
    } catch (MyDBException e) {
      throw new RuntimeException(e);
    }
    afficherPersonne(manPers.debutPersonne());
  }

  @FXML
  private void sauverPersonne(ActionEvent event) {
    try {
      if (Option.CREATE == opt) {
        dbWrk.creer(new Personne(
                Integer.parseInt(txtPK.getText()),
                txtNom.getText(),
                txtPrenom.getText(),
                DateTimeLib.localDateToDate(dateNaissance.getValue()),
                Integer.parseInt(txtNo.getText()),
                txtRue.getText(),
                Integer.parseInt(txtNPA.getText()),
                txtLocalite.getText(),
                ckbActif.isSelected(),
                Double.parseDouble(txtSalaire.getText()),
                new Date()
        ));
      } else if (Option.MODIFY == opt) {
          dbWrk.modifier(new Personne(
                  manPers.courantPersonne().getPkPers(),
                  txtNom.getText(),
                  txtPrenom.getText(),
                  DateTimeLib.localDateToDate(dateNaissance.getValue()),
                  Integer.parseInt(txtNo.getText()),
                  txtRue.getText(),
                  Integer.parseInt(txtNPA.getText()),
                  txtLocalite.getText(),
                  ckbActif.isSelected(),
                  Double.parseDouble(txtSalaire.getText()),
                  new Date()
          ));
      }
      btnSauver.setVisible(false);
      btnNext.setVisible(true);
      btnDebut.setVisible(true);
      btnPrevious.setVisible(true);
      btnEnd.setVisible(true);
      manPers.setPersonnes(dbWrk.lirePersonnes());
    } catch (MyDBException e) {
      throw new RuntimeException(e);
    }
    rendreVisibleBoutonsDepl(true);
  }

  public void quitter() {
    try {
      dbWrk.deconnecter(); // ne pas oublier !!!
    } catch (MyDBException ex) {
      System.out.println(ex.getMessage());
    }
    Platform.exit();
  }

  /*
   * METHODES PRIVEES 
   */
  private void afficherPersonne(Personne p) {
    if (p != null) {
      txtPrenom.setText(p.getPrenom());
      txtNom.setText(p.getNom());
      txtNo.setText(String.valueOf(p.getNoRue()));
      txtNPA.setText(String.valueOf(p.getNpa()));
      txtPK.setText(String.valueOf(p.getPkPers()));
      txtLocalite.setText(p.getLocalite());
      txtSalaire.setText(String.valueOf(p.getSalaire()));
      txtRue.setText(p.getRue());
      dateNaissance.setValue(DateTimeLib.dateToLocalDate(p.getDateNaissance()));
    }
  }

  private void ouvrirDB() {
    try {
      switch (DB_TYPE) {
        case MYSQL:
          dbWrk.connecterBdMySQL("223_personne_1table");
          break;
        case HSQLDB:
          dbWrk.connecterBdHSQLDB("../data" + File.separator + "223_personne_1table");
          break;
        case ACCESS:
          dbWrk.connecterBdAccess("../data" + File.separator + "223_Personne_1table.accdb");
          break;
        default:
          System.out.println("Base de données pas définie");
      }
      System.out.println("------- DB OK ----------");
      btnAnnuler.setVisible(false);
      btnSauver.setVisible(false);
    } catch (MyDBException ex) {
      JfxPopup.displayError("ERREUR", "Une erreur s'est produite", ex.getMessage());
      System.exit(1);
    }
  }
  
    private void rendreVisibleBoutonsDepl(boolean b) {
      btnDebut.setVisible(b);
      btnPrevious.setVisible(b);
      btnNext.setVisible(b);
      btnEnd.setVisible(b);
      btnAnnuler.setVisible(!b);
      btnSauver.setVisible(!b);
    }

  private void effacerContenuChamps() {
    txtNom.setText("");
    txtPrenom.setText("");
    txtPK.setText("");
    txtNo.setText("");
    txtRue.setText("");
    txtNPA.setText("");
    txtLocalite.setText("");
    txtSalaire.setText("");
    ckbActif.setSelected(false);
  }

}
