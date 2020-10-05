package BachecaAnnunci;
import ServerValidazione.*;
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.io.File;
import java.util.List;

/*
    Classe che ha la funzione di andare a leggere i file xml per configurare alcuni aspetti
    e proprietà dell'applicazione. Essa verrà utilizzata da tutte le classi che avranno bisogno
    dei loro parametri di configurazione. Esempio: Bacheca, ConnessioneDB, ServerXMLValida.
*/

@XStreamAlias("ParametriConfigurazione")
public class ParametriConfigurazione {
    @XStreamAlias("ipServer")
    private String ipServer;
    @XStreamAlias("portaServer")
    private int portaServer;
    @XStreamAlias("catDiagramTorta")
    private int catDiagramTorta;
    @XStreamAlias("giorniCancellazionePost")
    private int giorniCancellazionePost;
    @XStreamAlias("fontTabella")
    private String fontTabella;
    
    public ParametriConfigurazione caricaParametriConfigurazione(){
        ParametriConfigurazione tmp = null;
        XStream xstream = new XStream();
        try{
            xstream.processAnnotations(ParametriConfigurazione.class);
            tmp = (ParametriConfigurazione) xstream.fromXML(new File("ParametriConfigurazione.xml"));
        }catch(Exception e){
            e.printStackTrace();
        }
        return tmp;
    }
    public int getCatDiagramTorta(){
        return catDiagramTorta;
    }
    public int getPortaServer(){
        return portaServer;
    }
    public String getIpServer(){
        return ipServer;
    }
    public int getGiorniCancellazionePost(){
        return giorniCancellazionePost;
    }
    public String getFontTabella(){
        return fontTabella;
    }
}
