// Copyright (c) 2015 D1SM.net

package net.fs.rudp;

import java.net.InetAddress;

public class ConnectionUDP {
    InetAddress dstIp;
    public int dstPort;
    Sender sender;
    Receiver receiver;
    public UDPOutputStream uos;
    public UDPInputStream uis;
    Route route;
    private boolean connected = true;

    int connectId;

    ClientControl clientControl;

    private boolean localClosed = false, remoteClosed = false, destroied = false;

    boolean stopnow = false;

    public ConnectionUDP(Route ro, InetAddress dstIp, int dstPort, int mode, int connectId, ClientControl clientControl) throws Exception {
        this.clientControl = clientControl;
        this.route = ro;
        this.dstIp = dstIp;
        this.dstPort = dstPort;
        if (mode == 1) {
            //MLog.println("                 发起连接RUDP "+dstIp+":"+dstPort+" connectId "+connectId);
        } else if (mode == 2) {

            //MLog.println("                 接受连接RUDP "+dstIp+":"+dstPort+" connectId "+connectId);
        }
        this.connectId = connectId;
        try {
            sender = new Sender(this);
            receiver = new Receiver(this);
            uos = new UDPOutputStream(this);
            uis = new UDPInputStream(this);
            if (mode == 2) {
                ro.createTunnelProcessor().process(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            connected = false;
            route.connTable.remove(connectId);
            e.printStackTrace();
            //#MLog.println("                 连接失败RUDP "+connectId);
            synchronized (this) {
                notifyAll();
            }
            throw e;
        }
        //#MLog.println("                 连接成功RUDP "+connectId);
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public String toString() {
        return dstIp + ":" + dstPort;
    }

    boolean isConnected() {
        return connected;
    }

    public void close_local() {
        if (!localClosed) {
            localClosed = true;
            if (!stopnow) {
                sender.sendCloseMessage_Conn();
            }
            destroy(false);
        }
    }

    void close_remote() {
        if (!remoteClosed) {
            remoteClosed = true;
            destroy(false);
        }
    }

    //完全关闭
    void destroy(boolean force) {
        if (!destroied) {
            if ((localClosed && remoteClosed) || force) {
                destroied = true;
                connected = false;
                uis.closeStream_Local();
                uos.closeStream_Local();
                sender.destroy();
                receiver.destroy();
                route.removeConnection(this);
                clientControl.removeConnection(this);
            }
        }
    }

    void live() {
    }
}
