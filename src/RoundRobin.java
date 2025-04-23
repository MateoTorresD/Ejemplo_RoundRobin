import java.util.*;

class Proceso {
    String nombre;
    int tiempoRafaga;
    int tiempoRestante;
    int tiempoLlegada;
    int tiempoEspera = 0;
    int tiempoFinal;

    public Proceso(String nombre, int tiempoRafaga, int tiempoLlegada) {
        this.nombre = nombre;
        this.tiempoRafaga = tiempoRafaga;
        this.tiempoRestante = tiempoRafaga;
        this.tiempoLlegada = tiempoLlegada;
    }
}

public class RoundRobin {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese el número de procesos: ");
        int n = sc.nextInt();
        List<Proceso> procesos = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            System.out.print("Nombre del proceso " + (i + 1) + ": ");
            String nombre = sc.next();
            System.out.print("Tiempo de ráfaga de " + nombre + ": ");
            int rafaga = sc.nextInt();
            System.out.print("Tiempo de llegada de " + nombre + ": ");
            int llegada = sc.nextInt();
            procesos.add(new Proceso(nombre, rafaga, llegada));
        }

        System.out.print("Ingrese el quantum: ");
        int quantum = sc.nextInt();

        roundRobin(procesos, quantum);
    }

    static void roundRobin(List<Proceso> listaOriginal, int quantum) {
        Queue<Proceso> cola = new LinkedList<>();
        List<Proceso> lista = new ArrayList<>(listaOriginal);
        int tiempo = 0;
        int completados = 0;

        while (completados < lista.size()) {
            // Agregar a la cola los procesos que ya han llegado
            for (Proceso p : lista) {
                if (p.tiempoLlegada <= tiempo && !cola.contains(p) && p.tiempoRestante > 0) {
                    cola.add(p);
                }
            }

            if (cola.isEmpty()) {
                tiempo++;
                continue;
            }

            Proceso actual = cola.poll();

            int ejecucion = Math.min(quantum, actual.tiempoRestante);
            tiempo += ejecucion;
            actual.tiempoRestante -= ejecucion;

            // Agregar procesos que llegaron mientras este proceso estaba en ejecución
            for (Proceso p : lista) {
                if (p != actual && p.tiempoLlegada <= tiempo && !cola.contains(p) && p.tiempoRestante > 0) {
                    cola.add(p);
                }
            }

            if (actual.tiempoRestante > 0) {
                cola.add(actual);
            } else {
                actual.tiempoFinal = tiempo;
                actual.tiempoEspera = actual.tiempoFinal - actual.tiempoLlegada - actual.tiempoRafaga;
                completados++;
            }
        }

        System.out.println("\nResultados:");
        int totalEspera = 0, totalRetorno = 0;
        for (Proceso p : lista) {
            int retorno = p.tiempoFinal - p.tiempoLlegada;
            System.out.println("Proceso " + p.nombre +
                    " | Espera: " + p.tiempoEspera +
                    " | Retorno: " + retorno);
            totalEspera += p.tiempoEspera;
            totalRetorno += retorno;
        }

        System.out.printf("\nPromedio de espera: %.2f\n", (double) totalEspera / lista.size());
        System.out.printf("Promedio de retorno: %.2f\n", (double) totalRetorno / lista.size());
    }
}
