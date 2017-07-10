// Copyright (c) 2015 D1SM.net

package net.fs.rudp;

import net.fs.rudp.message.PingMessage;
import net.fs.rudp.message.PingMessage2;
import net.fs.utils.MLog;
import net.fs.utils.MessageCheck;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class ClientControl {

    private int clientId;


    private Object synlock = new Object();

    private HashMap<Integer, SendRecord> sendRecordTable = new HashMap<>();


    HashMap<Integer, SendRecord> sendRecordTable_remote = new HashMap<>();


    private long startSendTime = 0;

    private int maxSpeed = (int) (1024 * 1024);

    private int initSpeed = (int) maxSpeed;

    private int currentSpeed = initSpeed;

    private final Object syn_timeid;

    private long sended = 0;

    private long markTime = 0;

    private long lastSendPingTime, lastReceivePingTime = System.currentTimeMillis();

    private Random ran = new Random();

    private HashMap<Integer, Long> pingTable = new HashMap<>();

    int pingDelay = 250;

    private int clientId_real = -1;

    private long needSleep_All, trueSleep_All;

    private Route route;

    InetAddress dstIp;

    int dstPort;

    private HashMap<Integer, ConnectionUDP> connTable = new HashMap<>();

    private final Object syn_connTable = new Object();

    private String password;

    ResendManage resendMange;

    boolean closed = false;

    {
        resendMange = new ResendManage();
    }

    ClientControl(Route route, int clientId, InetAddress dstIp, int dstPort) {
        this.clientId = clientId;
        this.route = route;
        this.dstIp = dstIp;
        this.dstPort = dstPort;
        syn_timeid = new Object();
    }

    void onReceivePacket(DatagramPacket dp) {
        int sType;
        sType = MessageCheck.checkSType(dp);
        if (sType == net.fs.rudp.message.MessageType.sType_PingMessage) {
            PingMessage pm = new PingMessage(dp);
            sendPingMessage2(pm.getPingId(), dp.getAddress(), dp.getPort());
            currentSpeed = pm.getDownloadSpeed() * 1024;
        } else if (sType == net.fs.rudp.message.MessageType.sType_PingMessage2) {
            PingMessage2 pm = new PingMessage2(dp);
            lastReceivePingTime = System.currentTimeMillis();
            Long t = pingTable.get(pm.getPingId());
            if (t != null) {
                pingDelay = (int) (System.currentTimeMillis() - t);
                String protocal;
                if (route.isUseTcpTun()) {
                    protocal = "tcp";
                } else {
                    protocal = "udp";
                }
                //MLog.println("    receive_ping222: "+pm.getPingId()+" "+new Date());
                MLog.println("delay_" + protocal + " " + pingDelay + "ms " + dp.getAddress().getHostAddress() + ":" + dp.getPort());
            }
        }
    }

    void sendPacket(DatagramPacket dp) throws IOException {

        //加密

        route.sendPacket(dp);
    }

    void addConnection(ConnectionUDP conn) {
        synchronized (syn_connTable) {
            connTable.put(conn.connectId, conn);
        }
    }

    void removeConnection(ConnectionUDP conn) {
        synchronized (syn_connTable) {
            connTable.remove(conn.connectId);
        }
    }

    public void close() {
        closed = true;
        route.clientManager.removeClient(clientId);
        synchronized (syn_connTable) {
            Iterator<Integer> it = getConnTableIterator();
            while (it.hasNext()) {
                final ConnectionUDP conn = connTable.get(it.next());
                if (conn != null) {
                    Route.es.execute(() -> {
                        conn.stopnow = true;
                        conn.destroy(true);
                    });

                }
            }
        }
    }

    private CopiedIterator getConnTableIterator() {
        CopiedIterator it;
        synchronized (syn_connTable) {
            it = new CopiedIterator(connTable.keySet().iterator());
        }
        return it;
    }

    void updateClientId(int newClientId) {
        clientId_real = newClientId;
        sendRecordTable.clear();
        sendRecordTable_remote.clear();
    }

    void onSendDataPacket() {

    }

    void sendPingMessage() {
        int pingid = Math.abs(ran.nextInt());
        long pingTime = System.currentTimeMillis();
        pingTable.put(pingid, pingTime);
        lastSendPingTime = System.currentTimeMillis();
        PingMessage lm = new PingMessage(0, route.localclientId, pingid, Route.localDownloadSpeed, Route.localUploadSpeed);
        lm.setDstAddress(dstIp);
        lm.setDstPort(dstPort);
        try {
            sendPacket(lm.getDatagramPacket());
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private void sendPingMessage2(int pingId, InetAddress dstIp, int dstPort) {
        PingMessage2 lm = new PingMessage2(0, route.localclientId, pingId);
        lm.setDstAddress(dstIp);
        lm.setDstPort(dstPort);
        try {
            sendPacket(lm.getDatagramPacket());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    SendRecord getSendRecord(int timeId) {
        SendRecord record;
        synchronized (syn_timeid) {
            record = sendRecordTable.get(timeId);
            if (record == null) {
                record = new SendRecord();
                record.setTimeId();
                sendRecordTable.put(timeId, record);
            }
        }
        return record;
    }

    int getCurrentTimeId() {
        long current = System.currentTimeMillis();
        if (startSendTime == 0) {
            startSendTime = current;
        }
        return (int) ((current - startSendTime) / 1000);
    }

    //纳秒
    synchronized void sendSleep(long startTime, int length) {
        if (route.mode == 1) {
            currentSpeed = Route.localUploadSpeed;
        }
        if (sended == 0) {
            markTime = startTime;
        }
        sended += length;
        //10K sleep
        if (sended > 10 * 1024) {
            long needTime = (long) (1000 * 1000 * 1000f * sended / currentSpeed);
            long usedTime = System.nanoTime() - markTime;
            if (usedTime < needTime) {
                long sleepTime = needTime - usedTime;
                needSleep_All += sleepTime;

                long moreTime = trueSleep_All - needSleep_All;
                if (moreTime > 0) {
                    if (sleepTime <= moreTime) {
                        sleepTime = 0;
                        trueSleep_All -= sleepTime;
                    }
                }

                long s = needTime / (1000 * 1000);
                int n = (int) (needTime % (1000 * 1000));
                long t1 = System.nanoTime();
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(s, n);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    trueSleep_All += (System.nanoTime() - t1);
                }
            }
            sended = 0;
        }

    }

    Object getSynlock() {
        return synlock;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    int getClientId_real() {
        return clientId_real;
    }

    void setClientId_real(int clientId_real) {
        this.clientId_real = clientId_real;
        lastReceivePingTime = System.currentTimeMillis();
    }

    long getLastSendPingTime() {
        return lastSendPingTime;
    }

    long getLastReceivePingTime() {
        return lastReceivePingTime;
    }

    void setPassword() {
        this.password = null;
    }

}
