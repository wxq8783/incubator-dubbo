package org.apache.dubbo.demo.provider;

import org.apache.dubbo.callback.CallbackListener;
import org.apache.dubbo.callback.CallbackService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CallbackServiceImpl implements CallbackService {

    private final Map<String,CallbackListener> listeners = new ConcurrentHashMap<>();

    public void addListener(String key,CallbackListener listener){
        listeners.put(key,listener);
    }

    public CallbackServiceImpl() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        for(Map.Entry<String,CallbackListener> entry : listeners.entrySet()){
                            entry.getValue().changed(getChanged(entry.getKey()));
                        }
                        Thread.sleep(5000);
                    }catch (Throwable t){

                    }

                }
            }
        });
        t.start();
    }

    private String getChanged(String key){
        return "Changed:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
