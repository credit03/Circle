package com.guoyi.circle.been;

import java.io.Serializable;

/**
 * Created by Credit on 2017/2/28.
 */

public class ReturnMsg implements Serializable {
    public String Msg;
    public int Is;
    public Object Data;

    public Object getData() {
        return Data;
    }

    public void setData(Object data) {
        Data = data;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public int getIs() {
        return Is;
    }

    public void setIs(int is) {
        Is = is;
    }

    @Override
    public String toString() {
        return "ReturnMsg{" +
                "Msg='" + Msg + '\'' +
                ", Is=" + Is +
                ", Data=" + Data +
                '}';
    }

    public void setReturnMsg(ReturnMsg m) {
        this.Is = m.getIs();
        this.Msg = m.getMsg();
    }
}
