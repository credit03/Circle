package com.guoyi.circle.mvp.presenter;

/**
 * Created by Credit on 2017/3/6.
 */

public abstract class LoadFalgs {
    public static final int NOTE_LOAD = 0X100;
    public static final int ALREADEY_LOAD = 0X150;

    public static final int OP_ADD = 0x200;
    public static final int OP_DELETE = 0x210;


    public static final int LOAD_OP = 0x300;
    public static final int LOAD_MORE = 0x310;
    public static final int LOAD_REFRESH = 0x320;

    public static final int FAVORT_OP = 0x500;


    public static final int POST_OP = 0x600;

    public static final int COMMENT_OP = 0x700;


    public int LOAD_STATE = NOTE_LOAD;


}
