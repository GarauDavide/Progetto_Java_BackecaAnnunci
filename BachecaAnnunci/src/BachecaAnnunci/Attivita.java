package BachecaAnnunci;
import java.io.Serializable;
import ServerValidazione.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/* 
    Questa classe ha la struttura delle attività che verranno salvate in formato
    xml tramite la classe LogXMLAttivita.
*/

public class Attivita{
    private String indirizzoIp;
    private String dataAttivita;
    private String tipoAttivita;
    
    public Attivita(String ii, String ta){
        indirizzoIp = ii;
        dataAttivita = calcolaDataCorrente();
        tipoAttivita = ta;
    }
    
    private String calcolaDataCorrente(){
        SimpleDateFormat sdf = new SimpleDateFormat(); // creo l'oggetto
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");    //ne setto il template di stampa
	String stringaData = sdf.format(new Date()); //genero la data con il template deciso
        System.out.println(stringaData);
        return stringaData;
    }
}