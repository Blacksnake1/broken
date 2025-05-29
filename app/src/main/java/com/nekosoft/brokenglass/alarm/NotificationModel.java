package com.nekosoft.brokenglass.alarm;

public class NotificationModel {

    private int layoutRemote;
    private int layoutRemoteExpand;

    public NotificationModel(int layoutRemote, int layoutRemoteExpand) {
        this.layoutRemote = layoutRemote;
        this.layoutRemoteExpand = layoutRemoteExpand;
    }

    public int getLayoutRemote() {
        return layoutRemote;
    }

    public void setLayoutRemote(int layoutRemote) {
        this.layoutRemote = layoutRemote;
    }

    public int getLayoutRemoteExpand() {
        return layoutRemoteExpand;
    }

    public void setLayoutRemoteExpand(int layoutRemoteExpand) {
        this.layoutRemoteExpand = layoutRemoteExpand;
    }
}