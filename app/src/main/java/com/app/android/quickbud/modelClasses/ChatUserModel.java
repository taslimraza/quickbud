package com.app.android.quickbud.modelClasses;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mobi11 on 23/9/15.
 */
public class ChatUserModel {
    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public TextView getUserName() {
        return userName;
    }

    public void setUserName(TextView userName) {
        this.userName = userName;
    }

    public TextView getChat_msg() {
        return chat_msg;
    }

    public void setChat_msg(TextView chat_msg) {
        this.chat_msg = chat_msg;
    }

    ImageView image;
    TextView userName;
    TextView chat_msg;
}
