package com.linearpast.sccore.example.cap;

import com.linearpast.sccore.capability.data.ICapabilitySync;

public interface ISheepData extends ICapabilitySync {
    Integer getValue();
    void setValue(Integer value);
}
