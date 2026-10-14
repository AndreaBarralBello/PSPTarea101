import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*Hilo que lee y vacía continuamente el buffer del proceso hijo
* Es fundamental para evitar el deadlock, ya que las tuberías tienen un tamaño
* limitado. Si el hijo escribe mucho, y nadie lo lee, el buffer se llena y el
* hijo se bloquea. Si en ese momento además el padre está en waitFor() ninguno
* de los dos puede abanzar y se produce un deadlock

*/
public class MiHilo implements Runnable{

    //Variables
    Process proceso;


    //Constructor
    public MiHilo(Process proceso) {
        this.proceso = proceso;
    
    }

    @Override
    public void run() {

        /*process.getInputStream() devuelve stodout y el stderr gracias 
        * a redirectErrorStream. Se envuelve en InputStreamReader (de bytes a caracteres),
        * y en BufferedReader lee línea a línea
         */
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
