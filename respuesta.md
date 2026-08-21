Parte I

1. El consumo de CPU es alto porque el hilo consumidor esta metido en un ciclo infinito preguntando a cada rato si hay elementos en la cola, lo que se llama espera activa. La clase responsable de esto es Consumer en su metodo run.

2. Al poner wait y notify se arreglo el problema y el consumo bajo casi a cero porque el hilo ya no se queda dando vueltas preguntando, sino que se duerme hasta que el productor le avise que hay algo nuevo.

3. Al probar con el productor rapido y el consumidor lento, el limite de stock funciono bien. La cola no se paso de los 100 elementos y no hubo un consumo alto de cpu ni errores.

Parte II

Para el buscador de listas negras, lo que hicimos fue meter un AtomicInteger para llevar la cuenta global de cuantas veces aparece la IP. Apenas llegue a 5, los hilos ven eso y hacen un break para dejar de buscar y no perder tiempo. 
Para que no haya problemas al guardar las listas donde se encontro la ip, usamos una lista sincronizada (Collections.synchronizedList) asi evitamos que hayan errores de concurrencia cuando varios hilos traten de agregar cosas al mismo tiempo.
