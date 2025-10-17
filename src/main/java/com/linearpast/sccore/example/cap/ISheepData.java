package com.linearpast.sccore.example.cap;

import com.linearpast.sccore.capability.data.ICapabilitySync;

/**
 * 接口继承ICapabilitySync是必需的，但是接口是非必需的（你可以在注册时直接使用cap类本身） <br>
 * 用于共享一些可能会用到的cap的公共方法
 */
public interface ISheepData extends ICapabilitySync {
    Integer getValue();
    void setValue(Integer value);
}
