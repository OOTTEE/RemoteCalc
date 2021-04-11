package me.ote.polishcalc.calculator;

import io.netty.util.internal.StringUtil;

import java.util.List;

public class Operation {
    final List<String> value;

    public Operation(List<String> value) {
        this.value = value;
    }

    public List<String> getChain() {
        return value;
    }

    public String getOperationStr() {
        return StringUtil.join(" " , value).toString();
    }

}