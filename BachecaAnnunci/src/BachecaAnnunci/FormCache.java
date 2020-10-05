package BachecaAnnunci;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
    Questa classe salva i dati dell'annuncio in cache.
    Contiene due funzioni che vengono chiamate al momento della chiusura della 
    applicazione. Salva in un file .bin tutti i dati dell'annuncio che sono stati inseriti 
    e non è ancora stato premuto il pulsante PUBBLICA/CERCA o PULISCI AREA.
    Le due funzioni servono una per salvare e l'altra per leggere/caricare i dati
    da file .bin. E' per questo che abbiamo reso la classe serializzabile assieme
    alla classe Annuncio.
*/ 

public class FormCache implements Serializable{  
    public Annuncio cacheAnnuncio;
    FormCache(Annuncio annuncio){
        cacheAnnuncio = annuncio;
    }
    FormCache(){
        cacheAnnuncio = null;
    }
    
    public void salvaCacheInput(){
        try{
            FileOutputStream fos = new FileOutputStream("cacheDiInput.bin");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        }catch(Exception e){
            System.out.println("Errore salvataggio da cache Input: " + e.getMessage());
        }
    }
     
 public FormCache caricaCacheInput(){
        FormCache tmp = null;
        try{
            FileInputStream fis = new FileInputStream("cacheDiInput.bin");
            ObjectInputStream ois = new ObjectInputStream(fis);
            tmp = (FormCache) ois.readObject();
          
        }catch(Exception e){
            System.out.println("Errore caricamento da cache Input: " + e.getMessage());
        }
        return tmp;
    }
}