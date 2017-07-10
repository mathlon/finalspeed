// Copyright (c) 2015 D1SM.net

package net.fs.rudp;

public class SendRecord {

    private int sendSize;

    private int ackedSize;

    SendRecord() {

    }

    void addResended() {
    }

    void addSended(int size) {
        sendSize += size;
    }

    void addSended_First() {
    }

    public int getSendSize() {
        return sendSize;
    }

    int getAckedSize() {
        return ackedSize;
    }

    //接收到的数据大小
    void setAckedSize(int ackedSize) {
        if (ackedSize > this.ackedSize) {
            this.ackedSize = ackedSize;
        }
    }

    void setTimeId() {
    }

}
