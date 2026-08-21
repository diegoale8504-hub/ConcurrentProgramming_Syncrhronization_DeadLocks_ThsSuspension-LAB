package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 *
 * @author hcadavid
 */
public class Main {

    public static void main(String[] args) {
        String ip;
        int threadsCount = 10;

        if (args.length >= 2) {
            ip = args[0];
            threadsCount = Integer.parseInt(args[1]);
        } else if (args.length == 1) {
            ip = args[0];
            threadsCount = 4;
        } else {
            ip = "202.24.34.55"; // IP no confiable conocida en el dataset
        }

        System.out.println("Verificando IP " + ip + " con " + threadsCount + " hilos...");
        HostBlackListsValidator validator = new HostBlackListsValidator();
        List<Integer> occurrences = validator.checkHost(ip, threadsCount);
        
        System.out.println("La IP fue reportada en " + occurrences.size() + " listas negras.");
        if (!occurrences.isEmpty()) {
            System.out.println("Listas encontradas: " + occurrences);
        }
    }
}
