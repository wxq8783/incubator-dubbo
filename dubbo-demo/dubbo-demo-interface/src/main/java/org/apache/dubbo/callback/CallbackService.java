package org.apache.dubbo.callback;

public interface CallbackService {

    void addListener(String key , CallbackListener listener);
}
