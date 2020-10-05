package BachecaAnnunci;

/*
    Questa classe è stata implementata per contenere i dati del diagramma a torta.
    Viene utilizzata dalla classe ConnessioneDB ogni volta che il diagramma a torta
    viene aggiornato.
*/

public class DatiDiagrammaTorta {
    private String categoria;
    private int quante;
    public DatiDiagrammaTorta(String c, int q){
        categoria = c;
        quante = q;
    }
}
