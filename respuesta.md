## Juan Camilo Melo Diego Rozo
# Parte I – Punto 1: Consumo de CPU con JVisualVM

## Evidencia recolectada

**Imagen 1 – Pestaña "Monitor" de JVisualVM**


![img.png](img.png)

1. El consumo de CPU es alto porque el hilo consumidor esta metido en un ciclo infinito preguntando a cada rato si hay elementos en la cola, lo que se llama espera activa. La clase responsable de esto es Consumer en su metodo run.

**Imagen 2 – Pestaña "Threads" (Timeline), primer intervalo**

![img_1.png](img_1.png)

2. Al poner wait y notify se arreglo el problema y el consumo bajo casi a cero porque el hilo ya no se queda dando vueltas preguntando, sino que se duerme hasta que el productor le avise que hay algo nuevo.

**Imagen 3 – Pestaña "Threads" (Timeline), segundo intervalo**
![img_2.png](img_2.png)

3. Al probar con el productor rapido y el consumidor lento, el limite de stock funciono bien. La cola no se paso de los 100 elementos y no hubo un consumo alto de cpu ni errores.

## Análisis: ¿a qué se debe este consumo?

El consumo de CPU observado en el Monitor no proviene del productor (que duerme 1 segundo entre cada elemento producido, liberando el procesador), sino del hilo consumidor, que se mantiene en estado RUNNABLE de forma permanente.
Revisando el código de la clase consume el hilo consumidor ejecuta un ciclo while que pregunta continuamente si hay elementos disponibles en la cola, sin ceder nunca el procesador cuando esta está vacía. Esto es lo que se conoce como espera activa el hilo nunca se "duerme", sino que sigue ejecutándose (consumiendo CPU) aunque no tenga trabajo real que hacer.

Esto es exactamente lo que confirma el timeline de JVisualVM: mientras el productor pasa la mayor parte del tiempo "Sleeping", el consumidor permanece siempre "Running" al 100%.

## Conclusión

- Causa del consumo de CPU: espera activa (busy-waiting) en el ciclo de consumo.
- Clase responsable: Consumer (método run()), por no usar mecanismos de coordinación entre hilos (wait()/notify(), o una BlockingQueue con take()) que permitan que el hilo se suspenda cuando no hay elementos que consumir, en lugar de seguir preguntando en un ciclo infinito.

---

Parte II

Para el buscador de listas negras, lo que hicimos fue meter un AtomicInteger para llevar la cuenta global de cuantas veces aparece la IP. Apenas llegue a 5, los hilos ven eso y hacen un break para dejar de buscar y no perder tiempo. 
Para que no haya problemas al guardar las listas donde se encontro la ip, usamos una lista sincronizada (Collections.synchronizedList) asi evitamos que hayan errores de concurrencia cuando varios hilos traten de agregar cosas al mismo tiempo.
