package ServerValidazione;
import BachecaAnnunci.*;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
    Questa classe implementa il server che andrà a validare l'attivita secondo una struttura del file 
    contenuta nel file xsd. Questa classe contiene una sola funzione oltre al main che utilizza
    tutti i vari moduli messi a dosposizione da Java. Utilizzando le regole
    W3C_XML_SCHEMA_NS_URI valida le stringhe xml che li vengono mandate tramite socket.
*/

public class ServerXMLValida{
    public static void main(String[] args) {
        int sessioni = 0;
        try {
                ServerSocket servsock = new ServerSocket(8080, 7);  
                while(sessioni++ < 1000) {
                    System.out.println("Sono in ascolto e aspetto un stringa xml da validare..");
                    Socket sock = servsock.accept(); 
                    ObjectInputStream ois = new ObjectInputStream(sock.getInputStream()); 
                    String log = (String) ois.readObject();
                    if(ServerXMLValida.validaXML(log)){
                        FileWriter fw = new FileWriter("./fileApplicazioneServer/LogAttivita.txt", true);
                        fw.write(log);
                        fw.close();
                    }
                    ois.close();
                }
                servsock.close();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Non validato");
        }
    }
                        
      
      public static boolean validaXML(String XMLdaValidare){  //(0)
        try{
            DocumentBuilder docbuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            SchemaFactory schemafactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Document doc = docbuild.parse(new InputSource(new StringReader(XMLdaValidare)));
            Schema schema = schemafactory.newSchema(new StreamSource(new File("SchemaAttivita.xsd")));
            schema.newValidator().validate(new DOMSource(doc));
        }catch(Exception e){
            if(e instanceof SAXException) 
                System.out.println("Errore di validazione "+e); 
            else 
                System.out.println("Errore nella funzione validaXML "+e.getMessage());
        } 
        System.out.println("Ho validato la la stringa xml con successo..");
        return true;
      }
}
