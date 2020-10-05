package BachecaAnnunci;
import ServerValidazione.*;
import java.io.Serializable;

/*
    Le varie funzioni implementate sono le quelle che permettono l'incapsulamento.
    La funzione controlloMessaggio(String s) ha la funzione di mandare a capo il testo
    ogni N caratteri in modo che nella colonna della tabella la stringa messaggio
    va a capo senza che la TableView intervenga con lo scroll orizzontale.
*/

public class Annuncio implements Serializable{
    private String dataAnnuncio;
    private String utente;
    private String tipologia;
    private String categoria;
    private String messaggio;
    public Annuncio(String d, String u, String t, String c, String m){
        dataAnnuncio = d;
        utente = u;
        tipologia = t;
        categoria = c;
        messaggio = controllaMessaggio(m);
    }
    public String getDataAnnuncio() {
        return dataAnnuncio;
    }
    public String getUtente() {
        return utente;
    }
    public String getTipologia() {
        return tipologia;
    }
    public String getCategoria() {
        return categoria;
    }
    public String getMessaggio() {
        return messaggio;
    }
    private String controllaMessaggio(String m){
        String sms = m;
        if(sms.length() >= 38){
            char[] caratteri = sms.toCharArray();
            int grandezza = (caratteri.length) + ((int) Math.floor(caratteri.length / 38) * 2);
            char[] nuovaStringaConACapo = new char[grandezza];
            int num = 1;
            int scorriCaratteri = 0;
            int variazione = 0;
            for(int i = 0; scorriCaratteri < caratteri.length && i < nuovaStringaConACapo.length; i ++){
                if((i - variazione) >= (38 * num)){
                    if(caratteri[scorriCaratteri] != ' '){
                        if(caratteri[scorriCaratteri - 1] != ' '){
                            nuovaStringaConACapo[i] = '-';
                            nuovaStringaConACapo[++ i] = '\n';
                            variazione += 2;
                        }else
                            nuovaStringaConACapo[i] = '\n';
                }else{
                        nuovaStringaConACapo[i] = '\n';
                        variazione ++;
                    }
                    num ++;
                    while(caratteri[scorriCaratteri] == ' '){
                        scorriCaratteri ++;
                        variazione ++;
                    }
                }else{
                    nuovaStringaConACapo[i] = caratteri[scorriCaratteri];
                    scorriCaratteri ++;
                }
            }
            sms = String.valueOf(nuovaStringaConACapo);
        }
        return sms;
    }
}