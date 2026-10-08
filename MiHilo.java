
public class MiHilo implements Runnable{

    //Variables
    Process proceso;
    long inicio;

    //Constructor
    public MiHilo(Process proceso, long inicio) {
        this.proceso = proceso;
        this.inicio = inicio;
    }

    @Override
    public void run() {

       tiempoEjecucion(proceso);
    }

    




    
}
