import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MiHilo implements Runnable{

    //Variables
    Process proceso;


    //Constructor
    public MiHilo(Process proceso) {
        this.proceso = proceso;
    
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(proceso.getInputStream(), "UTF-8"))) {

            String linea;
            while ((linea = br.readLine()) != null) {
               System.out.println("[HILO] "+linea);
            }

        } catch (IOException e) {
            System.out.println("[HILO] Error leyendo la salida: " + e.getMessage());
        }
    }
    




    
}
