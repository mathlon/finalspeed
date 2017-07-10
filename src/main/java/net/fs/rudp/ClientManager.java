// Copyright (c) 2015 D1SM.net

package net.fs.rudp;

import net.fs.utils.MLog;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;


class ClientManager {

    private HashMap<Integer, ClientControl> clientTable = new HashMap<>();

    private Route route;

    private final Object syn_clientTable = new Object();

    ClientManager(Route route) {
        this.route = route;
        Thread mainThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                scanClientControl();
            }
        });
        mainThread.start();
    }

    private void scanClientControl() {
        CopiedIterator it = getClientTableIterator();
        long current = System.currentTimeMillis();
        while (it.hasNext()) {
            ClientControl cc = clientTable.get(it.next());
            if (cc != null) {
                int receivePingTimeout = 8 * 1000;
                if (current - cc.getLastReceivePingTime() < receivePingTimeout) {
                    int sendPingInterval = 1000;
                    if (current - cc.getLastSendPingTime() > sendPingInterval) {
                        cc.sendPingMessage();
                    }
                } else {
                    //超时关闭client
                    MLog.println("超时关闭client " + cc.dstIp.getHostAddress() + ":" + cc.dstPort + " " + new Date());
                    synchronized (syn_clientTable) {
                        cc.close();
                    }
                    if (route.mode == Route.mode_client) {
                        System.exit(1);
                    }
                }
            }
        }
    }

    void removeClient(int clientId) {
        clientTable.remove(clientId);
    }

    private CopiedIterator getClientTableIterator() {
        CopiedIterator it;
        synchronized (syn_clientTable) {
            it = new CopiedIterator(clientTable.keySet().iterator());
        }
        return it;
    }

    ClientControl getClientControl(int clientId, InetAddress dstIp, int dstPort) {
        ClientControl c = clientTable.get(clientId);
        if (c == null) {
            c = new ClientControl(route, clientId, dstIp, dstPort);
            synchronized (syn_clientTable) {
                clientTable.put(clientId, c);
            }
        }
        return c;
    }

}
