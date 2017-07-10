// Copyright (c) 2015 D1SM.net

package net.fs.rudp;

public class UDPOutputStream {
    public ConnectionUDP conn;
    private Sender sender;

    UDPOutputStream(ConnectionUDP conn) {
        this.conn = conn;
        this.sender = conn.sender;
    }

    public void write(byte[] data, int length) throws ConnectException, InterruptedException {
        sender.sendData(data, 0, length);
    }

    public void closeStream_Local() {
        sender.closeStream_Local();
    }

}
