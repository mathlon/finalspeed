// Copyright (c) 2015 D1SM.net

package net.fs.client;

import java.util.ArrayList;

class ClientConfig {

    private String serverAddress = "";

    private int serverPort;

    private int remotePort;

    private int downloadSpeed;
    int uploadSpeed;

    private boolean direct_cn = true;

    private int socks5Port = 1083;

    private String remoteAddress;

    private String protocal = "tcp";

    private boolean autoStart = false;

    private ArrayList<String> recentAddressList = new ArrayList<>();

    String getServerAddress() {
        return serverAddress;
    }

    void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    int getServerPort() {
        return serverPort;
    }

    void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    int getRemotePort() {
        return remotePort;
    }

    void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    boolean isDirect_cn() {
        return direct_cn;
    }

    void setDirect_cn(boolean direct_cn) {
        this.direct_cn = direct_cn;
    }

    int getDownloadSpeed() {
        return downloadSpeed;
    }

    void setDownloadSpeed(int downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    int getUploadSpeed() {
        return uploadSpeed;
    }

    void setUploadSpeed(int uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    int getSocks5Port() {
        return socks5Port;
    }

    void setSocks5Port(int socks5Port) {
        this.socks5Port = socks5Port;
    }

    String getRemoteAddress() {
        return remoteAddress;
    }

    void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    String getProtocal() {
        return protocal;
    }

    void setProtocal(String protocal) {
        this.protocal = protocal;
    }

    boolean isAutoStart() {
        return autoStart;
    }

    void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    ArrayList<String> getRecentAddressList() {
        return recentAddressList;
    }

}
