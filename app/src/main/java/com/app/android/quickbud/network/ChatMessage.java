package com.app.android.quickbud.network;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by mobi11 on 26/1/16.
 */
public class ChatMessage {

    public String body, userName, caption, image;
    public String Date, Time;
    public String msgid;
    public boolean isMine, isFirstMsg;// Did I send the message.
    private Bitmap bitmap;
    private Uri imageUri;
    private int msgIndex;

    public ChatMessage(){
    }
//    public ChatMessage(String Sender, String Receiver, String messageString,
//                       String ID, boolean isMINE) {
//        body = messageString;
//        isMine = isMINE;
//        sender = Sender;
//        msgid = ID;
//        receiver = Receiver;
//        senderName = sender;
//    }
//


    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public boolean isFirstMsg() {
        return isFirstMsg;
    }

    public void setFirstMsg(boolean firstMsg) {
        isFirstMsg = firstMsg;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getMsgIndex() {
        return msgIndex;
    }

    public void setMsgIndex(int msgIndex) {
        this.msgIndex = msgIndex;
    }
}
