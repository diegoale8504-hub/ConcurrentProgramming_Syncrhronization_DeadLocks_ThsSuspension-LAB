# Parte I – Punto 1: Consumo de CPU con JVisualVM

## Evidencia recolectada

**Imagen 1 – Pestaña "Monitor" de JVisualVM**


![img.png](img.png)

Como se observa en la imagen anterior, el proceso `edu.eci.arst.concprg.prodcons.StartProduction` mantiene un **consumo de CPU constante (~8%)** durante toda la ejecución, a pesar de que, en teoría, la mayor parte del tiempo la cola de productos está vacía (el productor es lento: produce cada 1 segundo).
 
---

**Imagen 2 – Pestaña "Threads" (Timeline), primer intervalo**

![img_1.png](img_1.png)
 
---

**Imagen 3 – Pestaña "Threads" (Timeline), segundo intervalo**

![img_3.png](img_3.png)

En las dos imagenes de la pestaña Threads se observa que:

- Thread-0 (el hilo `Producer`) aparece en color morado ("Sleeping") la mayor parte del tiempo, lo cual es coherente con el Thread.sleep(1000) que hay en su ciclo `run()`.
- Thread-3 (el hilo `Consumer`) aparece en color verde ("Running") el 100% del tiempo, de manera continua e ininterrumpida, sin nunca pasar a estado de espera o bloqueo.
---

## Análisis: ¿a qué se debe este consumo?

El consumo de CPU observado en el Monitor no proviene del productor (que duerme 1 segundo entre cada elemento producido, liberando el procesador), sino del hilo consumidor, que se mantiene en estado RUNNABLE de forma permanente.

Revisando el código de la clase `Consumer`:

```java
@Override
public void run() {
    while (true) {
        if (queue.size() > 0) {
            int elem = queue.poll();
            System.out.println("Consumer consumes " + elem);
        }
        // No hay ningún bloqueo/espera aquí
    }
}
```

El hilo consumidor ejecuta un ciclo `while(true)` que **pregunta continuamente** (`queue.size() > 0`) si hay elementos disponibles en la cola, sin ceder nunca el procesador cuando esta está vacía. Esto es lo que se conoce como **espera activa (busy-waiting / polling)**: el hilo nunca se "duerme", sino que sigue ejecutándose (consumiendo CPU) aunque no tenga trabajo real que hacer.

Esto es exactamente lo que confirma el timeline de JVisualVM: mientras el productor pasa la mayor parte del tiempo "Sleeping", el consumidor permanece siempre "Running" al 100%.

## Conclusión

- Causa del consumo de CPU: espera activa (busy-waiting) en el ciclo de consumo.
- Clase responsable: Consumer (método run()), por no usar mecanismos de coordinación entre hilos (wait()/notify(), o una BlockingQueue con take()) que permitan que el hilo se suspenda cuando no hay elementos que consumir, en lugar de seguir preguntando en un ciclo infinito.