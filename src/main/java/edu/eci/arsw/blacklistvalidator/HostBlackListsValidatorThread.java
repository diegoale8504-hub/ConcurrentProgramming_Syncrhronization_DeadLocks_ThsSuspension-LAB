package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HostBlackListsValidatorThread extends Thread {

    private final String ipAddress;
    private final int startServer;
    private final int endServer;
    private final AtomicInteger globalOccurrencesCount;
    private final List<Integer> globalBlackListOccurrences;
    private final int alarmCount;
    private int checkedServersCount;

    public HostBlackListsValidatorThread(String ipAddress, int startServer, int endServer,
                                         AtomicInteger globalOccurrencesCount,
                                         List<Integer> globalBlackListOccurrences,
                                         int alarmCount) {
        this.ipAddress = ipAddress;
        this.startServer = startServer;
        this.endServer = endServer;
        this.globalOccurrencesCount = globalOccurrencesCount;
        this.globalBlackListOccurrences = globalBlackListOccurrences;
        this.alarmCount = alarmCount;
        this.checkedServersCount = 0;
    }

    @Override
    public void run() {
        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();

        for (int i = startServer; i <= endServer; i++) {
            // Detención temprana: verificar si el conteo global alcanzó el límite de alarma
            if (globalOccurrencesCount.get() >= alarmCount) {
                break;
            }

            checkedServersCount++;

            if (skds.isInBlackListServer(i, ipAddress)) {
                synchronized (globalBlackListOccurrences) {
                    globalBlackListOccurrences.add(i);
                }
                globalOccurrencesCount.incrementAndGet();
            }
        }
    }

    public int getCheckedServersCount() {
        return checkedServersCount;
    }
}
