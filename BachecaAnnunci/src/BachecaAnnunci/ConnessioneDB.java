package BachecaAnnunci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

/*
    Questa classe implementa tutte le richieste al DataBase che effettua l'applicazione
*/

public class ConnessioneDB{
    private final int NumeroCaterieDiagrammaATorta = new ParametriConfigurazione().caricaParametriConfigurazione().getCatDiagramTorta();
    private final int giorniCancellazionePost = new ParametriConfigurazione().caricaParametriConfigurazione().getGiorniCancellazionePost();
    
    private String calcolaDataCorrente(){
        SimpleDateFormat sdf = new SimpleDateFormat(); // creo l'oggetto
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");    //ne setto il template di stampa
	String stringaData = sdf.format(new Date()); //genero la data con il template deciso
        //System.out.println(stringaData);
        return stringaData;
    }
    
    //Funzione che da il giusto id alla categoria, in modo da risultare corretta nel DB
    private int dammiIdCategoria(String s){
        int id = 0;
        switch(s){
            case "Automobili" :  id = 1; break;
            case "Immobili" :  id = 2; break;
            case "Cellulari" :  id = 3; break;
            case "Computer" :  id = 4; break;
            case "Libri" :  id = 5; break;
            case "Film" :  id = 6; break;
            case "Elettronica" :  id = 7; break;
            case "Musica" :  id = 8; break;
            case "VideoGiochi" :  id = 9; break;
            case "Abbigliamento" :  id = 10; break;
            case "Sport" : id = 11; break;
            default : id = 0; break;
        }
        return id;
    }
    
    //Funzione che carica l0intero elenco di tutte le categorie presenti nel DB
    public ObservableList<String> CaricaParametriCategorie(){
        ObservableList<String> tmpCategorie = FXCollections.observableArrayList();
        try(
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bacheca_annunci", "root", "");
            Statement stm = con.createStatement();
        ){
            ResultSet res = stm.executeQuery("SELECT * FROM categorie ORDER BY nomeCategoria");
            while(res.next()){
                tmpCategorie.add(new String(res.getString("nomeCategoria")));
            }
            con.close();
        }catch(SQLException sqle){
            System.err.println(sqle.getMessage());
        }
        return tmpCategorie;
    }
    
    //Funzione che aggiorna i dati del diagramma a torta
    public ObservableList<PieChart.Data> aggiornaDiagrammaTorta(){
        ObservableList<PieChart.Data> listaDatiPerDiagrammaTorta = FXCollections.observableArrayList();
        try(
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bacheca_annunci", "root", "");
            Statement stm = con.createStatement();
        ){
            ResultSet res = stm.executeQuery("SELECT * FROM datidiagrammatorta ORDER BY quante DESC");
            int n = NumeroCaterieDiagrammaATorta;
            while(res.next() && n > 0){
                listaDatiPerDiagrammaTorta.add(
                        new PieChart.Data(
                                res.getString("categoria"),
                                (int)(((res.getInt("quante"))*100)/ res.getInt("totalePost"))
                        )
                );
                n --;
            }
            con.close();
        }catch(SQLException sqle){
            System.err.println(sqle.getMessage());
        }
        return listaDatiPerDiagrammaTorta;
    }
    
    //Funzione che inserisce nuovo annuncio nel database
    public void passoParametriNuovoAnnuncioAllClasseConnessioneDB(String u, String t, String c, String m){
        if(u.equals("") || u.equals("Inserisci la tua mail..") ||
                t == null ||
                c == null ||
                m.equals("Scrivi messaggio annuncio..") || m.equals("")){
                    System.out.println("Parametri non completi. Post non pubblicato.");
                    return;
        }
        try(
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bacheca_annunci", "root", "");
            PreparedStatement ps = con.prepareStatement( "INSERT INTO annunci VALUES (?, ?, ?, ?, ?, ?)");
        ){
            ps.setInt(1, 0);
            ps.setString(2, calcolaDataCorrente());
            ps.setString(3, u);
            ps.setString(4, t);
            ps.setInt(5, dammiIdCategoria(c));
            ps.setString(6, m);
            ps.executeUpdate();
            con.close();
        }catch(SQLException sqle){
            System.err.println(sqle.getMessage());
        }
    }
    
    //funzione che scarica tutti i dati dal db->tutti gli annunci
    public ObservableList<Annuncio> sincronizzaAnnunci(){
        ObservableList<Annuncio> listaAnnunciDB = FXCollections.observableArrayList();
        try(
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bacheca_annunci", "root", "");
            Statement stm = con.createStatement();
        ){
            stm.execute("DELETE FROM annunci WHERE DATEDIFF(curdate(), dataCompleta) >= " + giorniCancellazionePost);
            ResultSet res = stm.executeQuery("SELECT a.dataCompleta, a.utente, a.tipologia, b.nomeCategoria, a.messaggio FROM annunci AS a JOIN categorie AS b on a.categoria = b.idcategorie ORDER BY dataCompleta DESC");
            while(res.next()){
                listaAnnunciDB.add(
                        new Annuncio(
                                res.getString("a.dataCompleta"),
                                res.getString("a.utente"),
                                res.getString("a.tipologia"),
                                res.getString("b.nomeCategoria"),
                                res.getString("a.messaggio")
                        )
                );
            }
            con.close();
        }catch(SQLException sqle){
            System.err.println(sqle.getMessage());
        }
        return listaAnnunciDB;
    }
    
    /*
        Funzione che interroga il db in base ai parametri di ingresso che seleziona
        l'utente nella sezione CERCA della Bacheca. La query al db viene costruita
        dinamicamente in modo che la ricerca soddisfi tutti i casi in base ai parametri
        di ricerca che selezione l'utente.
    */
    public ObservableList<Annuncio> trovaAnnunciConParametri(String pc, String t, String c){
        ObservableList<Annuncio> annunciTmp = FXCollections.observableArrayList();
        pc = (pc.equals("Inserisci parola chiave da cercare..") || pc.equals("")) ? "%" : "%" + pc + "%";
        String stringaQuery = "SELECT a.dataCompleta, a.utente, a.tipologia, b.nomeCategoria, a.messaggio FROM annunci AS a JOIN categorie AS b on a.categoria = b.idcategorie WHERE ";
        stringaQuery += (t == null) ? "a.tipologia IS NOT NULL AND " : "a.tipologia = ? AND ";
        stringaQuery += (c == null) ? "a.categoria IS NOT NULL AND " : "a.categoria = ? AND ";
        stringaQuery += "a.messaggio LIKE ? ORDER BY dataCompleta DESC";
        try(
             Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bacheca_annunci", "root", "");
             PreparedStatement ps = con.prepareStatement(stringaQuery);
        ){
            int cont = 0;
            if(t != null){
                ps.setString(++ cont, t);
            }
            if(c != null){
                ps.setInt(++ cont, dammiIdCategoria(c));
            }
            ps.setString(++ cont, pc);
            
            ResultSet res = ps.executeQuery();
            while(res.next()){
                annunciTmp.add(
                        new Annuncio(
                                res.getString("a.dataCompleta"),
                                res.getString("a.utente"),
                                res.getString("a.tipologia"),
                                res.getString("b.nomeCategoria"),
                                res.getString("a.messaggio")
                        )
                );
            }
            con.close();
        }catch(SQLException sqle){
            System.err.println(sqle.getMessage());
        }
        return annunciTmp;
    }
    
    public int getNumeroCaterieDiagrammaATorta(){
        return NumeroCaterieDiagrammaATorta;
    }
}