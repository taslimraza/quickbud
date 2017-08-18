package com.app.android.quickbud.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.android.quickbud.R;
import com.app.android.quickbud.network.ChatMessage;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.Globals;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mobi11 on 26/1/16.
 */
public class ChatAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ChatMessage> chatMessages;
    private int currentPosition;
    private ImageView rightChatImage;

    public ChatAdapter(Context context, ArrayList<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        Globals.setCustomTypeface(context);
    }

    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        currentPosition = position;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_user2_item, parent, false);
        }

        TextView rightMessageTxt = (TextView) convertView.findViewById(R.id.message_text_1);
        TextView leftMessageTxt = (TextView) convertView.findViewById(R.id.message_text);
        RelativeLayout leftLayout = (RelativeLayout) convertView.findViewById(R.id.left_msg);
        RelativeLayout rightLayout = (RelativeLayout) convertView.findViewById(R.id.bubble);
        TextView leftUserName = (TextView) convertView.findViewById(R.id.left_username);
        TextView rightUserName = (TextView) convertView.findViewById(R.id.right_username);
        ImageView leftChatImage = (ImageView) convertView.findViewById(R.id.left_chat_image);
        rightChatImage = (ImageView) convertView.findViewById(R.id.right_chat_image);
        TextView rightMsgTimeTxt = (TextView) convertView.findViewById(R.id.right_msg_time_text);
        TextView leftMsgTimeTxt = (TextView) convertView.findViewById(R.id.left_msg_time_text);
        final ImageView rightLoadingSpinner = (ImageView) convertView.findViewById(R.id.right_loading_spinner);
        final ImageView leftLoadingSpinner = (ImageView) convertView.findViewById(R.id.left_loading_spinner);
        RelativeLayout rightCaptionLayout = (RelativeLayout) convertView.findViewById(R.id.right_caption_layout);
        RelativeLayout leftCaptionLayout = (RelativeLayout) convertView.findViewById(R.id.left_caption_layout);
        TextView rightCaptionText = (TextView) convertView.findViewById(R.id.right_image_caption_text);
        TextView leftCaptionText = (TextView) convertView.findViewById(R.id.left_image_caption_text);
        TextView rightTimeText = (TextView) convertView.findViewById(R.id.right_time_text);
        TextView leftTimeText = (TextView) convertView.findViewById(R.id.left_time_text);

        if (chatMessages.get(position).getBody() != null) {
            if (chatMessages.get(position).isMine) {
                if (chatMessages.get(position).getBody().contains(".jpeg")) {

                    String imageUrl;
                    String caption = null;
                    if (chatMessages.get(position).getBody().contains("@")) {
                        String[] splitBody = chatMessages.get(position).getBody().split("@");
                        imageUrl = splitBody[0];
                        if (splitBody.length > 1) {
                            caption = splitBody[1];
                        }
                    } else {
                        imageUrl = chatMessages.get(position).getBody();
                    }

                    rightLoadingSpinner.setVisibility(View.VISIBLE);

                    Glide.with(context).load(imageUrl)
                            .asBitmap().placeholder(R.mipmap.camera_placeholder)
                            .listener(new RequestListener<String, Bitmap>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    rightLoadingSpinner.setVisibility(View.GONE);
                                    int imageHeight = resource.getHeight();
                                    int imageWidth = resource.getWidth();
                                    Log.i("GetImageSize", "height - " + imageHeight + "width - " + imageWidth);

                                    chatMessages.get(position).setBitmap(resource);
                                    return false;
                                }
                            })
                            .into(new BitmapImageViewTarget(rightChatImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    Bitmap image = chatMessages.get(position).getBitmap();
                                    super.setResource(image);
//                                    int width = image.getWidth();
//                                    int height = image.getHeight();
//                                    Log.i("GetFinalImageSize", "height - " + height + "width - " + width);
//                                    if (width > height){
//                                        Bitmap bitmap = Bitmap.createBitmap(resource, 100, 0, height, height);
//                                        super.setResource(bitmap);
//                                    }else {
//                                        Bitmap bitmap = Bitmap.createBitmap(resource, 0, 100, width, width);
//                                        super.setResource(bitmap);
//                                    }
                                }
                            });
//                            .into(rightChatImage);

                    rightChatImage.setVisibility(View.VISIBLE);
                    rightMessageTxt.setVisibility(View.GONE);
                    rightCaptionLayout.setVisibility(View.VISIBLE);
                    rightTimeText.setText(chatMessages.get(position).getTime());
                    rightCaptionText.setText(caption);
                    rightCaptionText.setVisibility(View.VISIBLE);
                    rightMsgTimeTxt.setVisibility(View.GONE);
                    rightChatImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (chatMessages.get(position).getBitmap() != null) {
                                Bitmap bmp = chatMessages.get(position).getBitmap();
                                String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, null, null);
                                chatMessages.get(position).setImageUri(Uri.parse(path));
                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setData(chatMessages.get(position).getImageUri());
                                intent.setDataAndType(Uri.parse(path), "image/*");
                                context.startActivity(intent);

                            }
                        }
                    });

                } else {
                    rightMessageTxt.setText(chatMessages.get(position).getBody());
                    rightMessageTxt.setTypeface(Globals.quickChatMsg);
                    rightMessageTxt.setVisibility(View.VISIBLE);
                    rightChatImage.setVisibility(View.GONE);
                    rightMsgTimeTxt.setText(chatMessages.get(position).getTime());
                    rightMsgTimeTxt.setVisibility(View.VISIBLE);
                    rightLoadingSpinner.setVisibility(View.GONE);
                    rightCaptionLayout.setVisibility(View.GONE);
                    rightCaptionText.setVisibility(View.GONE);
                }
//            rightMessageTxt.setText(chatMessages.get(position).body);
                if (chatMessages.get(position).isFirstMsg) {
                    rightUserName.setText(chatMessages.get(position).getUserName());
                    rightUserName.setVisibility(View.VISIBLE);
                } else {
                    rightUserName.setVisibility(View.GONE);
                }
                rightUserName.setTypeface(Globals.quickChatUserName);
                leftLayout.setVisibility(View.GONE);
                rightLayout.setVisibility(View.VISIBLE);
            } else {
                if (chatMessages.get(position).getBody().contains(".jpeg")) {

                    String imageUrl;
                    String caption = null;
                    if (chatMessages.get(position).getBody().contains(".jpeg@")) {
                        String[] splitBody = chatMessages.get(position).getBody().split("@");
                        imageUrl = splitBody[0];
                        if (splitBody.length > 1) {
                            caption = splitBody[1];
                        }
                    } else {
                        imageUrl = chatMessages.get(position).getBody();
                    }

                    leftLoadingSpinner.setVisibility(View.VISIBLE);
                    Glide.with(context).load(imageUrl)
                            .asBitmap().placeholder(R.mipmap.camera_placeholder)
                            .listener(new RequestListener<String, Bitmap>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    leftLoadingSpinner.setVisibility(View.GONE);
//                                    int imageHeight = resource.getHeight();
//                                    int imageWidth = resource.getWidth();
//                                    Log.i("GetImageSize", "height - " + imageHeight + "width - " + imageWidth);
                                    chatMessages.get(position).setBitmap(resource);
                                    return false;
                                }
                            })
                            .into(new BitmapImageViewTarget(leftChatImage) {
                                @Override
                                protected void setResource(Bitmap resource) {

                                    Bitmap image = chatMessages.get(position).getBitmap();
                                    super.setResource(image);
//                                    int width = image.getWidth();
//                                    int height = image.getHeight();
//                                    Log.i("GetFinalImageSize", "height - " + height + "width - " + width);
//                                    if (width > height){
//                                        Bitmap bitmap = Bitmap.createBitmap(resource, 100, 0, height, height);
//                                        super.setResource(bitmap);
//                                    }else {
//                                        Bitmap bitmap = Bitmap.createBitmap(resource, 0, 100, width, width);
//                                        super.setResource(bitmap);
//                                    }
//                                    if(resource.getWidth() > 300 && resource.getHeight() > 300){
//                                        Bitmap bitmap = Bitmap.createBitmap(resource, 0, 0, width, width);
//                                        super.setResource(bitmap);
//                                    }else {
//                                        super.setResource(resource);
//                                    }
                                }
                            });
//                            .into(leftChatImage);

                    leftChatImage.setVisibility(View.VISIBLE);
                    leftTimeText.setText(chatMessages.get(position).getTime());
                    leftCaptionLayout.setVisibility(View.VISIBLE);
                    leftCaptionText.setText(caption);
                    leftCaptionText.setVisibility(View.VISIBLE);
                    leftMessageTxt.setVisibility(View.GONE);
                    leftMsgTimeTxt.setVisibility(View.GONE);
                    leftChatImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (chatMessages.get(position).getBitmap() != null) {
                                Bitmap bmp = chatMessages.get(position).getBitmap();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                                byte[] imageBytes = baos.toByteArray();
                                String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, null, null);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setData(chatMessages.get(position).getImageUri());
                                intent.setDataAndType(Uri.parse(path), "image/*");
                                context.startActivity(intent);
                            }
                        }
                    });
                } else {
                    leftMessageTxt.setText(chatMessages.get(position).getBody());
                    leftMsgTimeTxt.setText(chatMessages.get(position).getTime());
                    leftMsgTimeTxt.setVisibility(View.VISIBLE);
                    leftMessageTxt.setTypeface(Globals.quickChatMsg);
                    leftMessageTxt.setVisibility(View.VISIBLE);
                    leftChatImage.setVisibility(View.GONE);
                    leftLoadingSpinner.setVisibility(View.GONE);
                    leftCaptionLayout.setVisibility(View.GONE);
                    leftCaptionText.setVisibility(View.GONE);
                }
//            leftMessageTxt.setText(chatMessages.get(position).body);

                if (chatMessages.get(position).isFirstMsg) {
                    leftUserName.setText(chatMessages.get(position).getUserName());
                    leftUserName.setVisibility(View.VISIBLE);
                } else {
                    leftUserName.setVisibility(View.GONE);
                }

                leftUserName.setTypeface(Globals.quickChatUserName);
                rightLayout.setVisibility(View.GONE);
                leftLayout.setVisibility(View.VISIBLE);
            }
        } else if (chatMessages.get(position).getImageUri() != null) {
            final Bitmap bitmap = chatMessages.get(position).getBitmap();
            if (chatMessages.get(position).isMine) {

//                rightChatImage.setImageBitmap(bitmap);
                Glide.with(context).load(chatMessages.get(position).getImageUri()).asBitmap()
                        .into(rightChatImage);

//                int width = bitmap.getWidth();
//                int height = bitmap.getHeight();
//                if (width > height){
//                    rightChatImage.setImageBitmap(Bitmap.createBitmap(bitmap, 100, 0, height, height));
//                }else {
//                    rightChatImage.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 100, width, width));
//                }

//                rightChatImage.setImageBitmap(Bitmap.createBitmap(bitmap, 100, 100, 400, 400));
//                rightChatImage.setImageBitmap(bitmap);
//                chatViewHolder.rightChatImage.setImageBitmap(chatMessages.get(position).getBitmap());
                rightChatImage.setVisibility(View.VISIBLE);
                rightMessageTxt.setVisibility(View.GONE);
                rightCaptionLayout.setVisibility(View.VISIBLE);
                rightCaptionText.setText(chatMessages.get(position).getCaption());
                rightCaptionText.setVisibility(View.VISIBLE);
                rightTimeText.setText(chatMessages.get(position).getTime());
                rightMsgTimeTxt.setVisibility(View.GONE);
                rightLoadingSpinner.setVisibility(View.GONE);
                rightChatImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Log.i("ImageUri", chatMessages.get(position).getImageUri().toString());
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
////                        intent.setData(chatMessages.get(position).getImageUri());
//                        intent.setDataAndType(chatMessages.get(position).getImageUri(), "image/*");
//                        context.startActivity(intent);

                        if (chatMessages.get(position).getBitmap() != null) {
                            Bitmap bmp = chatMessages.get(position).getBitmap();
//                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                                byte[] imageBytes = baos.toByteArray();
                            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, null, null);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setData(chatMessages.get(position).getImageUri());
                            intent.setDataAndType(Uri.parse(path), "image/*");
                            context.startActivity(intent);
                        }

                    }
                });

//            rightMessageTxt.setText(chatMessages.get(position).body);
                rightUserName.setText(chatMessages.get(position).getUserName());
                rightMessageTxt.setTypeface(Globals.quickChatMsg);
                leftLayout.setVisibility(View.GONE);
                rightLayout.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

//    private class ChatViewHolder{
//
//        TextView rightMessageTxt, leftMessageTxt, leftUserName, rightUserName, rightMsgTimeTxt,
//                rightImageTimeTxt, leftTimeTxt;
//        ImageView leftChatImage, rightChatImage, rightLoadingSpinner, leftLoadingSpinner;
//        RelativeLayout leftLayout, rightLayout;
//
//        public ChatViewHolder(View view){
//            rightMessageTxt = (TextView) view.findViewById(R.id.message_text_1);
//            leftMessageTxt = (TextView) view.findViewById(R.id.message_text);
//            leftLayout = (RelativeLayout) view.findViewById(R.id.left_msg);
//            rightLayout = (RelativeLayout) view.findViewById(R.id.bubble);
//            leftUserName = (TextView) view.findViewById(R.id.left_username);
//            rightUserName = (TextView) view.findViewById(R.id.right_username);
//            leftChatImage = (ImageView) view.findViewById(R.id.left_chat_image);
//            rightChatImage = (ImageView) view.findViewById(R.id.right_chat_image);
//            rightMsgTimeTxt = (TextView) view.findViewById(R.id.msg_time_text);
//            rightImageTimeTxt = (TextView) view.findViewById(R.id.image_time_text);
//            leftTimeTxt = (TextView) view.findViewById(R.id.left_time_text);
//            rightLoadingSpinner = (ImageView) view.findViewById(R.id.right_loading_spinner);
//            leftLoadingSpinner = (ImageView) view.findViewById(R.id.left_loading_spinner);
//        }
//    }

    public void add(ChatMessage chatMessage) {
        chatMessages.add(chatMessage);
    }

    private void getImage(String imageUrl) {
        String url = Config.QUICK_CHAT_IMAGE + imageUrl;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("QTAuthorization", Config.SESSION_TOKEN_ID);
                return headers;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.right_chat_image:
//                if (chatMessages.get(currentPosition).getBitmap() != null){
//                    Bitmap bmp = chatMessages.get(currentPosition).getBitmap();
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                    byte[] imageBytes = baos.toByteArray();
//
//                    Intent intent = new Intent(context, ChatImageActivity.class);
//                    intent.putExtra("image_bitmap", imageBytes);
//                    context.startActivity(intent);
//                }else {
//
//                }
//                break;
//
//            case R.id.left_chat_image:
//                if (chatMessages.get(currentPosition).getBody() != null){
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chatMessages.get(currentPosition).getBody()));
//                    context.startActivity(intent);
//                }
//                break;
//        }
//    }
}
