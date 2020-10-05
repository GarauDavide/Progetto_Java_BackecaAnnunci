package BachecaAnnunci;

import ServerValidazione.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import com.thoughtworks.xstream.XStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import ServerValidazione.*;

/*
    Questa classe ha il compito di madnare al server l'attività da validare.
    Essa contiene un oggetto attivita con tutti i suoi vari attributi
*/

public class LogXMLAttivita{
    private final String ipServ = new ParametriConfigurazione().caricaParametriConfigurazione().getIpServer();
    private final int portaServ = new ParametriConfigurazione().caricaParametriConfigurazione().getPortaServer();
    private Attivita attivita;
    
    public LogXMLAttivita(Attivita a){
        attivita = a;
    }
    
    public LogXMLAttivita(){
        attivita = null;
    }
    
    public void serializzaAttivitaXML(){
        XStream xstream = new XStream();
        xstream.useAttributeFor(Attivita.class , "tipoAttivita"); 
        //costruisco la stringa da inviare al server per essere validata.
        String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + "\n" + xstream.toXML(this.attivita);
        try{
            Files.write(Paths.get("LogAttivita.txt"), str.getBytes(), StandardOpenOption.APPEND);
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Ho serializzato l'attivita'");
        //Ora mando al server per salvarla ulteriormente e validarla in base agli standard
        try(
                ObjectOutputStream oos = new ObjectOutputStream(new Socket(
                        ipServ,
                        portaServ
                ).getOutputStream());
        ){
            oos.writeObject(str);
            oos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}