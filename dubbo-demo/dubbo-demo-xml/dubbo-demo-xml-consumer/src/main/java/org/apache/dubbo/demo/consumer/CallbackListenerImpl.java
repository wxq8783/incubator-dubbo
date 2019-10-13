package org.apache.dubbo.demo.consumer;

import org.apache.dubbo.callback.CallbackListener;

public class CallbackListenerImpl implements CallbackListener {
    @Override
    public void changed(String msg) {
        System.out.println("----------->回调的事件:"+msg);
    }
}
