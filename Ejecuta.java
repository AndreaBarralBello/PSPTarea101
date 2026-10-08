
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

        int retorno = lanzarProceso(args);

        if (retorno == 0) {
            System.out.println("[PADRE] El proceso hijo terminó correctamente (código 0)");

        } else if (retorno == -1) {
            //comando introducido con error, sí se ejecutó 2
            System.out.println("[PADRE] ERROR No se pudo crear el proceso hijo, comando inexistente (código -1)");
        } else if (retorno == -3) {
            System.out.println("[PADRE] TIMEOUT: el hijo tardó más de 2 segundos y fue destruido");
        } else {
            System.out.println("[PADRE] El proceso falló (código " + retorno + ")");
        }
    }

    public static int lanzarProceso(String[] args) {
        int devolver = -1;

        try {

            // Preparo el proceso hijo
            ProcessBuilder pb = new ProcessBuilder(args);

            //redirectErrorStream evita el deadlock, ya que une el stdout y el stderr en un solo canal, redirige el 
            //stderr hacia el stdout 4
            //pq a veces el hijo llena el stderr y se para, entonces el hijo está esperando a que termine el padre
            //y el padre no puede pq el stderr está lleno. Por eso se bloquea - DEADLOCK
            pb.redirectErrorStream(true);

            //Se crea el proceso hijo
            Process process = pb.start();

            //Se obtiene el stdout del hijo. Lo escribe mientras trabaja
            InputStream is = process.getInputStream();

            //Conveirte UTF-8 en String de Java
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            System.out.println("Salida del proceso hijo " + Arrays.toString(args));

            //Lo leemos
            String linea;

            while ((linea = br.readLine()) != null) {
                System.out.println(linea);

            }
            is.close();

            boolean terminado = process.waitFor(2, TimeUnit.SECONDS);

            if (terminado) {
                devolver = process.exitValue();
            } else {
                process.destroyForcibly();
                devolver = -3;
            }

            //el proceso devuelve un entero al finalizar, por eso cuando finalice
            //guarda esa variable y es lo que devuelve este método. no se ejecuta, pq no pasa 3
        } catch (IOException e) {

            System.out.println(e.getLocalizedMessage());
        } catch (InterruptedException ex) {

            System.out.println("[PADRE] El proceso padre fue interrumpido");
        }

        return devolver;
    }
}
