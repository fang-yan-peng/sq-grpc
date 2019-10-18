package com.sq.etcd;

public interface StateListener {

    int DISCONNECTED = 0;

    int CONNECTED = 1;

    void stateChanged(int connected);

}
