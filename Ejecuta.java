import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Ejecuta {
     

    public static void main(String[] args) {
     

        //Si no se introduce ningún comando
        if (args.length == 0) {
            System.out.println("ERROR : debes indicar un comando");
            return;
        }

        int retorno =lanzarProceso(args);

        if (retorno == 0) {
            System.out.println("[PADRE] El proceso hijo terminó correctamente (código 0)");

        } else if (retorno == -1) {
            System.out.println("[PADRE] ERROR No se pudo crear el proceso hijo comando inexistente (código -1)");
        } else if (retorno == -3) {
            System.out.println("[PADRE] TIMEOUT: el hijo tardó más de 2 segundos ");
        } else {
            System.out.println("[PADRE] El proceso falló (código " + retorno + ")");
        }
    }





 static int lanzarProceso(String[] args) {

    int devolver = -1;
    Process proceso = null;
    Thread esteHilo = null;

  

    try {

        // Preparo el proceso hijo
        ProcessBuilder pb = new ProcessBuilder(args);

        // redirectErrorStream evita el deadlock, ya que une el stdout y el stderr en un
        // solo canal, redirige el
        // stderr hacia el stdout 4
        // pq a veces el hijo llena el stderr y se para, entonces el hijo está esperando
        // a que termine el padre
        // y el padre no puede pq el stderr está lleno. Por eso se bloquea - DEADLOCK
        pb.redirectErrorStream(true);

        // Se crea el proceso hijo
        Process process = pb.start();


        //Intanciamos aquí la clase MiHilo, que lee el buffer sin .waitFor()
        MiHilo hilo = new MiHilo(process);
        esteHilo = new Thread(hilo);
        esteHilo.start();

        boolean terminadoATiempo = tiempoEjecucion(process);

        //Si no terminó a tiempo
        if(!terminadoATiempo){

            return -3;
        }


        // el proceso devuelve un entero al finalizar, por eso cuando finalice
        // guarda esa variable y es lo que devuelve este método. no se ejecuta, pq no
        // pasa 3
    } catch (IOException e) {

        System.out.println(e.getLocalizedMessage());
    }

    return devolver;
}

// Creo un método para mostrar por pantalla si el proceso se terminó a tiempo
static boolean tiempoEjecucion(Process process) {

    // Variables
    boolean terminadoATiempo;

    long duracion;
    long tiempoInicial= System.currentTimeMillis();

    // Guardo el tiempo de inicio del proceso
    tiempoInicial = System.currentTimeMillis();

    try {

        terminadoATiempo = process.waitFor(2, TimeUnit.SECONDS);
        duracion = System.currentTimeMillis() - tiempoInicial;

        if (terminadoATiempo) {

            System.out.println("[PADRE] El proceso terminó a tiempo ");
            return true;

        } else {
            System.out.println("\n -> Tiempo agotado! El proceso sigue corriendo en segundo plano");
            // se mata al proceso
            process.destroyForcibly();
            System.out.println("\n -> Proceso destruído. ¿Está vivo? " + process.isAlive());
            return false;

        }
    } catch (InterruptedException ex) {

        System.out.println("ERROR al intentar procesar el tiempo del proceso");
    }

}





}
    

