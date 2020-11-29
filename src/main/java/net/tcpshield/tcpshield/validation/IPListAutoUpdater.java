package net.tcpshield.tcpshield.validation;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class IPListAutoUpdater {

    private final IPListUpdater ipListUpdater;

    public IPListAutoUpdater(IPListUpdater ipListUpdater) {
        this.ipListUpdater = ipListUpdater;
    }

    public void init() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ipListUpdater.update();
            }
        }, TimeUnit.HOURS.toMillis(12), TimeUnit.HOURS.toMillis(12));
    }

}
