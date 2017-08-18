package com.app.android.quickbud.utils;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mobi11 on 2/10/15.
 */
public class HttpRequest {
    Context context;
    URL connetUrl;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;

    public HttpRequest(Context context){
        this.context=context;
    }

    public String getNearByRestaurant(String url){
        try {
            stringBuilder = new StringBuilder();
            connetUrl=new URL(url);
            HttpURLConnection connection= (HttpURLConnection) connetUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream inputStream=connection.getInputStream();
            bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            String read=bufferedReader.readLine();
            while (read != null){
                stringBuilder.append(read);
                read=bufferedReader.readLine();
            }
        } catch (MalformedURLException e) {
            Toast.makeText(context,"Please Check your URL!",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            return null;
//            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


}
