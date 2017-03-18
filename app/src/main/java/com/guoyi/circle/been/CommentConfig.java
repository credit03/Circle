package com.guoyi.circle.been;

/**
 * Created by yiwei on 16/3/2.
 */
public class CommentConfig {
    public static enum Type {
        PUBLIC(0), REPLY(1);

        private int value;

        private Type(int value) {
            this.value = value;
        }

    }

    public int postId;
    public int circlePosition;
    public int commentPosition;
    public Type commentType;
    public UserBean replyUser;

    @Override
    public String toString() {
        String replyUserStr = "";
        if (replyUser != null) {
            replyUserStr = replyUser.toString();
        }
        return "circlePosition = " + circlePosition
                + "; commentPosition = " + commentPosition
                + "; commentType ＝ " + commentType
                + "; replyUser = " + replyUserStr;
    }
}
