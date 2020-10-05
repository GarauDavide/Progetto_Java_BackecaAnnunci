package BachecaAnnunci;
import ServerValidazione.*;
import static com.sun.javafx.css.FontFace.FontFaceSrcType.URL;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.*;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.print.PrintColor.COLOR;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import static javafx.scene.input.DataFormat.URL;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class Bacheca extends Application {
  private TextField nomeUtente;
  private TextField parolaChiaveCerca;
  private PieChart diagrammaTorta;
  private ChoiceBox<String> cboxSelezionaTipologia;
  private String indirizzoIp;
  private TextArea messaggioAnnuncio;
  private ChoiceBox<String> cboxSelezionaCategoria;
  private ChoiceBox<String> cboxSelezionaTipologiaRicerca;
  private ChoiceBox<String> cboxSelezionaCategoriaRicerca;
  private FormCache annuncioFormCache;
  private ObservableList<String> parConfCat;
  public static void main(String[] args) {
    Application.launch(args);
  }
  
  /* Funzione che setta l'indirizzo IP dell'host client */
  public void settaIndirizzoIp(){ 
        try{
            InetAddress host=InetAddress.getLocalHost();
            indirizzoIp=host.getHostAddress();
        }catch(UnknownHostException e){ 
            System.out.println("Errore indirizzo ip host: " + e.getMessage());
        }
    }
  
  /* Funzione che viene chiamata alla pressione del tasto PULISCI AREA che si
     si trova nell'area per pubblicare un nuovo post in Bacheca */
  public void pulisciAreaPubblica(){
      messaggioAnnuncio.setText("Scrivi messaggio annuncio..");
      cboxSelezionaTipologia.setValue(null);
      cboxSelezionaCategoria.setValue(null);
  }
  
  /* Funzione che viene chiamata alla pressione del tasto PULISCI AREA che si
     si trova nell'area per cercare un post in Bacheca */
  public void pulisciAreaCerca(){
      parolaChiaveCerca.setText("Inserisci parola chiave da cercare..");
      cboxSelezionaTipologiaRicerca.setValue(null);
      cboxSelezionaCategoriaRicerca.setValue(null);
  }
  
  /* Funzione che viene chiamata al momento della chiusura della applicazione
     Salva in un file .bin tutti i dati dell'annuncio che sono stati inseriti 
     e non è ancora stato premuto il pulsante PUBBLICA o PULISCI AREA */
  public void salvaDatiAllaChiusuraInCache(){
    new FormCache(
        new Annuncio(
            "", 
            nomeUtente.getText(), 
            cboxSelezionaTipologia.getValue(), 
            cboxSelezionaCategoria.getValue(), 
            messaggioAnnuncio.getText())
    ).salvaCacheInput();
    System.out.println("Cache salvata correttamente.");
  }
  
  /* Funzione che carica i dati salvati nel file .bin . Viene chiamata al momento
        della apertura della applicazione */
  public void caricaDatiDallaCache(){
      annuncioFormCache = new FormCache().caricaCacheInput();
      nomeUtente.setText(annuncioFormCache.cacheAnnuncio.getUtente());
      cboxSelezionaTipologia.setValue(annuncioFormCache.cacheAnnuncio.getTipologia());
      cboxSelezionaCategoria.setValue(annuncioFormCache.cacheAnnuncio.getCategoria());
      messaggioAnnuncio.setText(annuncioFormCache.cacheAnnuncio.getMessaggio());
      System.out.println("Cache caricata correttamente.");
  }
  
  /* Questa funzione viene chiamata subito dalla funzione start.
     Permette di validare il file xml per vedere se l'utente ha settato bene i parametri
     di configurazione accessibili direttamente dal file. */
  public void controlloParametriDiConfigurazione() {
        XStream xstream = new XStream();
        try{
            FileReader letturaFile = new FileReader("ParametriConfigurazione.xml");
            if(validaParametri(letturaFile))
                letturaFile.close();
        }catch(Exception e){
            System.out.println("Errore nel caricamento dei parametri di configurazione: " + e.getMessage());
        }
        System.out.println("File e parametri di configurazione validati correttamente.");
    }
    
    /* FUnzione che valida il file xml dei parametri di configurazione */
    public boolean validaParametri(FileReader fileLettura){
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Document doc = db.parse(new InputSource(fileLettura));
            Schema s = sf.newSchema(new StreamSource(new File("SchemaParametriConfigurazione.xsd")));
            s.newValidator().validate(new DOMSource(doc));
            return true;
        }catch(Exception e){
            System.out.println("Validazione dei parametri di configurazine non andata a buon fine: "+e.getMessage());
            return false;
        }
    }
  
  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Bacheca Annunci");
    
    //controllo (validazione) dei parametri di configurazione caricati da file xml
    controlloParametriDiConfigurazione();
    //setto un indirizzo ip al client
    settaIndirizzoIp();
    //carico tutte le categorie salvate nel database
    parConfCat = new ConnessioneDB().CaricaParametriCategorie();
    
    /*VBox principale
        Questa vbox comprende tutti gli elementi in scalata dell'interfaccia Bacheca
        Dentro questa VBox, ci saranno altri annidamenti di oggetti VBox e HBox
        che andranno a formare le varie aree dell'interfaccia Bacheca.
    */
    VBox vboxPrincipale = new VBox(10);
    vboxPrincipale.setId("Finestra");
    vboxPrincipale.setPadding(
        new Insets(25, 25, 25, 25)
    );
    //setto il titolo alla finestra - applicazione client
    final Label titoloBachecaAnnunci = new Label("Bacheca Annunci - Compra e Vendi Nuovo o Usato");
    titoloBachecaAnnunci.setId("titoloBachecaAnnunci");
    titoloBachecaAnnunci.setFont(new Font("Arial", 15));
    
    /* Tabella dove vengono pubblicati i post
        Questa tabella è il cuore della Bacheca. In quest'ultima vengono 
        stampati/visualizzati tutti i dati scaricati da DataBase in base alla operazione che
        viene fatta. Questa tableview ha come tipo di contenuto dei suoi elementi
        oggetti di classe Annuncio. Infatti ogni riga della tabella è rappresentata
        dalla composizione completa di un annuncio (a parte il nome utente) 
        Il font della tabella puo essere caricato da file di configurazione */
    TableView<Annuncio> tv = new TableView<Annuncio>();
    tv.setStyle(new ParametriConfigurazione().caricaParametriConfigurazione().getFontTabella());
    tv.setMaxSize(720, 355);
    tv.setMinSize(720, 355);
    
    TableColumn<Annuncio, String> colonnaDataOra = new TableColumn<Annuncio, String>("Data - Ora");
    colonnaDataOra.setCellValueFactory(new PropertyValueFactory<Annuncio, String>("dataAnnuncio"));
    colonnaDataOra.setMaxWidth(130);
    colonnaDataOra.setMinWidth(130);
    
    TableColumn<Annuncio,String> colonnaUtente = new TableColumn<Annuncio,String>("Utente");
    colonnaUtente.setCellValueFactory(new PropertyValueFactory<Annuncio, String>("utente"));
    colonnaUtente.setMaxWidth(250);
    colonnaUtente.setMinWidth(175);
    
    TableColumn<Annuncio,String> colonnaTipologia = new TableColumn<Annuncio,String>("Tipologia");
    colonnaTipologia.setCellValueFactory(new PropertyValueFactory<Annuncio, String>("tipologia"));
    colonnaTipologia.setMaxWidth(80);
    colonnaTipologia.setMinWidth(80);
    
    TableColumn<Annuncio,String> colonnaCategoria = new TableColumn<Annuncio,String>("Categoria");
    colonnaCategoria.setCellValueFactory(new PropertyValueFactory<Annuncio, String>("categoria"));
    colonnaCategoria.setMaxWidth(70);
    colonnaCategoria.setMinWidth(70);
    
    TableColumn<Annuncio,String> colonnaMessaggio = new TableColumn<Annuncio,String>("Messaggio");
    colonnaMessaggio.setCellValueFactory(new PropertyValueFactory<Annuncio, String>("messaggio"));
    colonnaMessaggio.setMaxWidth(250);
    colonnaMessaggio.setMinWidth(250);
    /* Dopo aver settato e definito tutti gli elementi che andranno a comporre la
        tabella li inserisco */
    tv.getColumns().addAll(
        colonnaDataOra,
        colonnaUtente,
        colonnaTipologia,
        colonnaCategoria,
        colonnaMessaggio
    );
    
    //sincronizzo la tabella appena avvio la applicazione scaricando tutti gli annunci presenti nel DB
    tv.setItems(new ConnessioneDB().sincronizzaAnnunci());
    
    /* Creo la VBox secondaria per inserimento dati post e ricerca post 
        Dentro questa vbox andranno poi ad esserci altre due vbox che sarà
        l'area per la publicazione di un post e quella per la ricerca di un post */
    VBox vBoxSecondaria = new VBox(5);
    
    //Titolo vbox secondaria
    final Label titoloVBoxSecondaria = new Label("Scegli se PUBBLICARE o effettuare una RICERCA");
    titoloVBoxSecondaria.setId("titoloVBoxSecondaria");
    titoloVBoxSecondaria.setFont(
        new Font("Arial", 15)
    );
    
    //Creo HBox per il bottone Sincronizza e per la textfield del nome utente
    HBox hboxSincronizzaENomeUtente = new HBox(25);
    
    //Creo label nome utente
    final Label labelNomeUtente = new Label("Nome Utente:");
    labelNomeUtente.setId("labelNomeUtente");
    labelNomeUtente.setUnderline(true);
    
    /*Creo la textfield per inserire il nome utente
        Questo textfield ha la proprieta che se possiede il focus viene analizzata
        la stringa che esso contiene. Se la stringa è "Inserisci la tua mail.."
        quando riceve il focus l'area di testo si cancella in modo da poter scrivere
        il nome utente senza dover cancellare quello che c'e al suo interno.
        Se non è "Inserisci la tua mail.." non succede nulla, rimane il valore presente. */
    nomeUtente = new TextField("Inserisci la tua mail..");
    nomeUtente.setMaxWidth(300);
    nomeUtente.setMinWidth(300);
    nomeUtente.setFocusTraversable(false);
    nomeUtente.focusedProperty().addListener(new ChangeListener<Boolean>(){
        @Override
        public void changed(ObservableValue<? extends Boolean> arg0, Boolean vecchioValoreProprieta, Boolean nuovoValoreProprieta){
            if (nuovoValoreProprieta){
                if(nomeUtente.getText().equals("Inserisci la tua mail.."))
                    nomeUtente.setText("");
            }
            else{
                if(nomeUtente.getText().equals(""))
                    nomeUtente.setText("Inserisci la tua mail..");
            }
        }
    });
    
    /*Creo il pulsante per sincronizzare la Bacheca
        Questo pulsante aggiorna tutti i dati della tabella(annucni).
        Successivamente aggiorna il diagramma a torta e registra l'attività svolta
        nel file di log. */
    Button sincronizza = new Button("Sincronizza");
    sincronizza.setOnAction(new EventHandler<ActionEvent>() {   
        @Override
        public void handle(ActionEvent event) {
            ConnessioneDB connessione = new ConnessioneDB();
            ObservableList<Annuncio> annunci = connessione.sincronizzaAnnunci();
            tv.setItems(annunci);
            diagrammaTorta.setData(new ConnessioneDB().aggiornaDiagrammaTorta());
            new LogXMLAttivita(new Attivita(indirizzoIp, "Premuto SINCRONIZZA")).serializzaAttivitaXML();
        }
    });
    
    //Inserisco la label nome utente, il texfield nome utente ed il tasto sincronizza
    hboxSincronizzaENomeUtente.getChildren().addAll(
        labelNomeUtente,
        nomeUtente,
        sincronizza
    );
    
    //creo i due titoli per le sezioni xbox secondarie
    final Label titoloPubblica = new Label("-> Pubblica un post in Bacheca:");
    titoloPubblica.setId("titoloPubblica");
    titoloPubblica.setUnderline(true);
    
    final Label titoloRicerca = new Label("-> Ricerca parola chiave per tipologia acquisto e categoria:");
    titoloRicerca.setId("titoloRicerca");
    titoloRicerca.setUnderline(true);
    
    //Creo la vBox che conterrà tutti gli oggetti dell'area pubblica il post
    VBox vBoxPubblicaPost = new VBox(10);
    
    /*Creo l'area di testo per digitare il messaggio del post. Anche questa stesso evento
        onfocus funzionante allo stesso modo del nome utente */
    messaggioAnnuncio = new TextArea("Scrivi messaggio annuncio..");
    messaggioAnnuncio.setMinSize(400, 30);
    messaggioAnnuncio.setMaxSize(400, 30);
    messaggioAnnuncio.focusedProperty().addListener(new ChangeListener<Boolean>(){
        @Override
        public void changed(ObservableValue<? extends Boolean> arg0, Boolean vecchioValoreProprieta, Boolean nuovoValoreProprieta){
            if (nuovoValoreProprieta){
                if(messaggioAnnuncio.getText().equals("Scrivi messaggio annuncio.."))
                    messaggioAnnuncio.setText("");
            }
            else{
                if(messaggioAnnuncio.getText().equals(""))
                    messaggioAnnuncio.setText("Scrivi messaggio annuncio..");
            }
        }
    });
    
    //Creo il label per la tipologia da selezionare
    final Label labelTipologia = new Label("Tipologia acquisto");
    labelTipologia.setId("labelTipologia");
    
    //creo il la choise box per poter scegliere la tipologia di acquisto o vendita
    cboxSelezionaTipologia = new ChoiceBox<>();
    cboxSelezionaTipologia.getItems().addAll(
        "Vendo Nuovo", 
        "Vendo Usato", 
        "Cerco Nuovo", 
        "Cerco Usato"
    );
    
    //Creo hBox per inserire choise box per selezionare la tipologia e label
    HBox hBoxTipologia = new HBox(10);
    
    //inserisco gli oggetti che fanno parte della tipologia: label e choisebox
    hBoxTipologia.getChildren().addAll(
        labelTipologia,
        cboxSelezionaTipologia
    );
    
    //Creo hBox per inserire choise box per selezionare la categoria e label
    HBox hBoxCategoria = new HBox(10);
    
    //Creo il label per la categoria da selezionare
    final Label labelCategoria = new Label("Categoria");
    labelCategoria.setId("labelTipologia");
    
    /*Creo la choiceBox per la selezione della categoria..
    Come possiamo vedere vengono caricate da DataBase al momento dell'apertura 
    della applicazione. */
    cboxSelezionaCategoria = new ChoiceBox<>();
    cboxSelezionaCategoria.setItems(parConfCat);
    
    //inserisco gli oggetti che fanno parte della categoria: label e choisebox
    hBoxCategoria.getChildren().addAll(
        labelCategoria,
        cboxSelezionaCategoria
    );
    
    /*Creo il bottone per la pubblicazione del messaggio.
    Alla pressione di questo bottone inserisco nel db l'annuncio, aggiorno la tabella
    sincronizzando gli annunci e successivamente aggiorno anche il diagramma a torta. 
    Infine pulisco l'area della pubblicazione dell'annuncio e notifico l'attivita nel file di log. */
    Button bottonePubblica = new Button("Pubblica");
    bottonePubblica.setOnAction(new EventHandler<ActionEvent>() {   
        @Override
        public void handle(ActionEvent event) {
            ConnessioneDB connessione = new ConnessioneDB();
            connessione.passoParametriNuovoAnnuncioAllClasseConnessioneDB(nomeUtente.getText(), cboxSelezionaTipologia.getValue(), cboxSelezionaCategoria.getValue(), messaggioAnnuncio.getText());
            ObservableList<Annuncio> annunci = connessione.sincronizzaAnnunci();
            tv.setItems(annunci);
            diagrammaTorta.setData(new ConnessioneDB().aggiornaDiagrammaTorta());
            pulisciAreaPubblica();
            new LogXMLAttivita(new Attivita(indirizzoIp, "Premuto PUBBLICA")).serializzaAttivitaXML();
        }
    });
    
    Button bottonePulisciPubblica = new Button("Pulisci campi");
    bottonePulisciPubblica.setOnAction(new EventHandler<ActionEvent>() {   
        @Override
        public void handle(ActionEvent event) {
            pulisciAreaPubblica();
            new LogXMLAttivita(new Attivita(indirizzoIp, "Premuto PULISCI CAMPI area PUBBLICA")).serializzaAttivitaXML();
        }
    });
    
    //creo il bottone per ripulire la zone publica
    HBox hBoxPubblicaPulisci = new HBox(155);
    hBoxPubblicaPulisci.getChildren().addAll(
        bottonePubblica,
        bottonePulisciPubblica
    );
    
    //creo una linea per separare le due parti secondarie di sopra e sotto
    final Separator lineSeparaSopraSottoSecondario = new Separator();
    
    //Inserisco gli oggetti nell VBox che riguarda la pubblicazione del post
    vBoxPubblicaPost.getChildren().addAll(
        titoloPubblica,
        messaggioAnnuncio,
        hBoxTipologia,
        hBoxCategoria,
        hBoxPubblicaPulisci
    );
    
    //creo la VBox dell area ricerca post
    VBox vBoxCercaPost = new VBox(10);
    
    //Creo il textfield per la parola chiave da cercare
    parolaChiaveCerca = new TextField("Inserisci parola chiave da cercare..");
    parolaChiaveCerca.setMinSize(300, 30);
    parolaChiaveCerca.setMaxSize(300, 30);
    parolaChiaveCerca.focusedProperty().addListener(new ChangeListener<Boolean>(){
        @Override
        public void changed(ObservableValue<? extends Boolean> arg0, Boolean vecchioValoreProprieta, Boolean nuovoValoreProprieta){
            if (nuovoValoreProprieta){
                if(parolaChiaveCerca.getText().equals("Inserisci parola chiave da cercare.."))
                    parolaChiaveCerca.setText("");
            }
            else{
                if(parolaChiaveCerca.getText().equals(""))
                    parolaChiaveCerca.setText("Inserisci parola chiave da cercare..");
            }
        }
    });
    
    //Creo hbox per inseire label e area di testo per inserie la tipologia da cercare
    HBox hBoxSelezionaTipologiaRicerca = new HBox(10);
    
    //creo la label per la tipologia di ricerca
    final Label labelTipologiaRicerca = new Label("Tipologia acquisto");
    labelTipologiaRicerca.setId("labelTipologiaRicerca");
    
    //creo la choise box per la tipologia di ricerca
    cboxSelezionaTipologiaRicerca = new ChoiceBox<>();
    cboxSelezionaTipologiaRicerca.getItems().addAll(
        "Vendo Nuovo", 
        "Vendo Usato",
        "Cerco Nuovo", 
        "Cerco Usato"
    );
    
    //Inserisco label e choise box per la tipologia di ricerca
    hBoxSelezionaTipologiaRicerca.getChildren().addAll(
        labelTipologiaRicerca,
        cboxSelezionaTipologiaRicerca
    );
    
    //creo la hbox per inserire label categoria e choise box da ceracre
    HBox hBoxSelezionaCategoriaRicerca = new HBox(10);
    
    //Creo la label per la categoria per la ricerca. label choise box
    final Label labelCategoriaRicerca = new Label("Categoria");
    labelCategoriaRicerca.setId("labelCategoriaRicerca");
    
    //creo la choise box della categoria per la ricerca
    cboxSelezionaCategoriaRicerca = new ChoiceBox<>();
    cboxSelezionaCategoriaRicerca.setItems(parConfCat);
    
    //Inserisco gli oggetti nella hbox contenente categoria ricerca
    hBoxSelezionaCategoriaRicerca.getChildren().addAll(
        labelCategoriaRicerca,
        cboxSelezionaCategoriaRicerca
    );
    
    //creo il bottone per la ricerca delle parole chiavi/categoria
    Button bottoneCerca = new Button("Cerca");
    bottoneCerca.setOnAction(new EventHandler<ActionEvent>() {   
        @Override
        public void handle(ActionEvent event) {
            ConnessioneDB connessione = new ConnessioneDB();
            ObservableList<Annuncio> annunci = connessione.trovaAnnunciConParametri(parolaChiaveCerca.getText(), cboxSelezionaTipologiaRicerca.getValue(), cboxSelezionaCategoriaRicerca.getValue());
            tv.setItems(annunci);
            pulisciAreaCerca();
            new LogXMLAttivita(new Attivita(indirizzoIp, "Premuto CERCA")).serializzaAttivitaXML();
        }
    });
    
    //creo il bottone per cancellare e ripulire i campi per la ricerca
    Button bottonePulisciZoneRicerca = new Button("Pulisci campi");
    bottonePulisciZoneRicerca.setOnAction(new EventHandler<ActionEvent>() {   
        @Override
        public void handle(ActionEvent event) {
            pulisciAreaCerca();
            new LogXMLAttivita(new Attivita(indirizzoIp, "Premuto PULISCI CAMPI area CERCA")).serializzaAttivitaXML();
        }
    });
    
    //creo hobox per per mettere il pulsante cerca ed il pulsante pilisci
    HBox hboxCercaRipulisci = new HBox(173);
    
    hboxCercaRipulisci.getChildren().addAll(
        bottoneCerca,
        bottonePulisciZoneRicerca
    );
    //Inserisco tutti gli oggetti dell'area cerca post
    vBoxCercaPost.getChildren().addAll(
        titoloRicerca,
        parolaChiaveCerca,
        hBoxSelezionaTipologiaRicerca,
        hBoxSelezionaCategoriaRicerca,
        hboxCercaRipulisci
    );
    
    //Inserisco oggetti nella vbox secondaria
    vBoxSecondaria.getChildren().addAll(
        titoloVBoxSecondaria,
        vBoxPubblicaPost,
        lineSeparaSopraSottoSecondario,
        vBoxCercaPost
    );
    
    //Creo un contenitore a colonne
    HBox hboxTabellaEVBoxSecondaria = new HBox(35);
    hboxTabellaEVBoxSecondaria.getChildren().addAll(
        tv,
        vBoxSecondaria
    );
    
    //creo una linea per separare le due parti principali di sopra e sotto
    final Separator lineSeparaSopraSotto = new Separator();
    
    //costruisco il diagramma a torta
    diagrammaTorta = new PieChart(new ConnessioneDB().aggiornaDiagrammaTorta());
    diagrammaTorta.setTitle("Annunci Bacheca - Le " + (new ConnessioneDB().getNumeroCaterieDiagrammaATorta()) + " Categorie più presenti:");
    diagrammaTorta.setLegendSide(Side.RIGHT);
    diagrammaTorta.setAnimated(true);
    diagrammaTorta.setLabelLineLength(10);
    diagrammaTorta.setMaxSize(778, 250);
    
    //Metto tutta la struttura dentro la vbox principale
    vboxPrincipale.getChildren().addAll(
        titoloBachecaAnnunci,
        hboxSincronizzaENomeUtente,
        hboxTabellaEVBoxSecondaria,
        lineSeparaSopraSotto,
        diagrammaTorta
    );
    
    Group root = new Group();
    root.setFocusTraversable(false);
    root.getChildren().addAll(
        vboxPrincipale
    );
     
    Scene scene = new Scene(root, 1320, 750);
    scene.getStylesheets().add("stili.css");
    primaryStage.setScene(scene);
    primaryStage.setOnShowing(new EventHandler<WindowEvent>(){
        public void handle(WindowEvent we){ 
            caricaDatiDallaCache();
            new LogXMLAttivita(new Attivita(indirizzoIp, "APERTURA APPLICAZIONE")).serializzaAttivitaXML();
        }
    });
    
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
        public void handle(WindowEvent we){
            if(nomeUtente.getText().equals(""))
                nomeUtente.setText("Inserisci la tua mail..");
            if(messaggioAnnuncio.getText().equals(""))
                messaggioAnnuncio.setText("Scrivi messaggio annuncio..");
            if(parolaChiaveCerca.getText().equals(""))
                parolaChiaveCerca.setText("Inserisci parola chiave da cercare..");
            salvaDatiAllaChiusuraInCache();
            new LogXMLAttivita(new Attivita(indirizzoIp, "CHIUSURA APPLICAZIONE")).serializzaAttivitaXML();
        }
    });
    
    primaryStage.setMaxHeight(750);
    primaryStage.setMaxWidth(1220);
    primaryStage.show();
  }
}