import java.io.IOException;
import java.util.concurrent.TimeUnit;

/* Es la clase principal que actúa como una mini-terminal
   Lanza un comando del sistema operativo como un proceso hijo,
   captura su salida y analiza su código de retorno
 */
public class Ejecuta {

    public static void main(String[] args) {

        // Si no se introduce ningún comando
        if (args.length == 0) {
            System.out.println("ERROR: debes indicar un comando");
            return;
        }

        // Se lanza el proceso hijo y recogemos su código de retorno
        int retorno = lanzarProceso(args);

        /* Analizo los distintos tipos de valores del código de retorno.
           Lo recojo en el main porque está todo mejor organizado en un solo
           bloque de código */
        if (retorno == 0) {
            System.out.println("[PADRE] El proceso hijo terminó correctamente (código 0)");
        } else if (retorno == -1) {
            System.out.println("[PADRE] ERROR No se pudo crear el proceso hijo comando inexistente (código -1)");
        } else if (retorno == -3) {
            System.out.println("[PADRE] TIMEOUT: el hijo tardó más de 2 segundos");
        } else {
            System.out.println("[PADRE] El proceso falló (código " + retorno + ")");
        }
    }

    /* Lanza el proceso hijo, arranca el hilo que lo lee (MiHilo) y espera a que finalice
     * @param args comando y argumentos a ejecutar
     * @return la salida del hijo
     */
    static int lanzarProceso(String[] args) {

        // Variables
        int devolver = -1;
        Process process = null;
        Thread esteHilo = null;

        try {

            // Creo el proceso hijo
            ProcessBuilder pb = new ProcessBuilder(args);

            pb.redirectErrorStream(true);

            // Se lanza el proceso hijo
            process = pb.start();

            /* Creo un hilo MiHilo que lee continuamente el stdout del hijo.
             * Esto es imprescindible, ya que si el hilo principal se quedara esperando
             * con waitFor() sin que nadie vaciara el buffer, el hijo se bloquearía al escribir
             * y se produciría deadlock */
            MiHilo hilo = new MiHilo(process);
            esteHilo = new Thread(hilo);
            esteHilo.start();

            boolean terminadoATiempo = tiempoEjecucion(process);

            // Si no terminó a tiempo
            if (!terminadoATiempo) {
                return -3;
            }

            // Recogo el código de retorno real del hijo
            devolver = process.exitValue();

        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        } finally {
            // Espero a que el hilo lector termine de volcar la salida
            if (esteHilo != null) {
                try {
                    esteHilo.join(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return devolver;
    }

    /* Espera al proceso hijo con un tiempo máximo de 2 segundos
     * si no termina, lo mata con destroyForcibly()
     *
     * @return true si terminó a tiempo, false si se agotó el tiempo
     */
    static boolean tiempoEjecucion(Process process) {

        long tiempoInicial = System.currentTimeMillis();

        try {

            boolean terminadoATiempo = process.waitFor(2, TimeUnit.SECONDS);
            long duracion = System.currentTimeMillis() - tiempoInicial;

            if (terminadoATiempo) {
                System.out.println("[PADRE] El proceso terminó a tiempo (" + duracion + " ms)");
                return true;
            } else {
                // El hijo tardó más de 2 segundos
                System.out.println("\n -> ¡Tiempo agotado! El proceso sigue corriendo en segundo plano");
                // Se mata al proceso
                process.destroyForcibly();
                process.waitFor();
                System.out.println("\n -> Proceso destruido. ¿Está vivo? " + process.isAlive());
                return false;
            }

        } catch (InterruptedException ex) {
            System.out.println("ERROR al intentar procesar el tiempo del proceso");
            return false;
        }
    }
}