package com.app.android.quickbud.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobi44 on 23/11/15.
 */
public class CameraHandler {

    public static int IMAGE_PIC_CODE = 1000, CROP_CAMERA_REQUEST = 1001,
            CROP_GALLARY_REQUEST = 1002;
    private Intent imageCaptureintent;
    private boolean isActivityAvailable;
    Context context;
    private List<ResolveInfo> cameraList;
    List<Intent> cameraIntents;
    Uri outputFileUri;
    Intent galleryIntent;
    Uri selectedImageUri;
    private String cameraImageFilePath, absoluteCameraImagePath;
    Bitmap bitmap;
    ImageView ivPicture;

    public CameraHandler(Context context) {
        this.context = context;
        setFileUriForCameraImage();
    }

    public void setIvPicture(ImageView ivPicture) {
        this.ivPicture = ivPicture;
    }

    private void setFileUriForCameraImage() {
        File root = new File(Environment.getExternalStorageDirectory()
                + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = "image.jpg";
        final File sdImageMainDirectory = new File(root, fname);
        absoluteCameraImagePath = sdImageMainDirectory.getAbsolutePath();
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
    }

    public String getCameraImagePath() {
        return cameraImageFilePath;
    }

    private void getActivities() {
        imageCaptureintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = ((Activity) context)
                .getPackageManager();
        this.cameraList = packageManager.queryIntentActivities(
                imageCaptureintent, 0);
        if (cameraList.size() > 0) {
            isActivityAvailable = true;
        } else {
            isActivityAvailable = false;
        }
    }

    private void fillCameraActivities() {
        getActivities();
        if (!isActivityAvailable) {
            return;
        }
        cameraIntents = new ArrayList<Intent>();
        for (ResolveInfo resolveInfo : cameraList) {
            Intent intent = new Intent(imageCaptureintent);
            intent.setPackage(resolveInfo.activityInfo.packageName);
            intent.setComponent(new ComponentName(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }
    }

    private void fillGallaryIntent() {
        galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);
    }

    public void showView() {
        fillCameraActivities();
        fillGallaryIntent();
        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent,
                "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                cameraIntents.toArray(new Parcelable[] {}));

        ((Activity) context).startActivityForResult(chooserIntent,
                IMAGE_PIC_CODE);
    }

    private Bitmap getBitmapFromURL(String src) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(src, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 192, 256);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(src, options);

    }

    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2
            // and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void onResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PIC_CODE) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    Log.v("", "ics");
                } else {
                    Log.v("", " not ics");
                }
                boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();

                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action
                                .equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH
                            && action != null) {
                        Log.v("", "action = null");
                        isCamera = true;
                    } else {
                        Log.v("", "action is not null");
                    }
                }
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                    onResultCameraOK();
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    onResultGalleryOK();
                }
            }
        }

//        if (requestCode == CROP_CAMERA_REQUEST) {
//            if (resultCode == Activity.RESULT_OK) {
//                resultOnCropOkOfCamera(data);
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                resultOnCroppingCancel();
//            }
//        }
//
//        if (requestCode == CROP_GALLARY_REQUEST) {
//            if (resultCode == Activity.RESULT_OK) {
//                resultOnCropOkOfGallary(data);
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                resultOnCroppingCancel();
//            }
//        }

    }

    private void doCropping(int code) {
        Log.v("", this.cameraImageFilePath);
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(selectedImageUri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        cropIntent.putExtra("return-data", true);
        try {
            ((Activity) context).startActivityForResult(cropIntent, code);
        } catch (Exception e) {

        }
    }

    private void onResultCameraOK() {
        this.cameraImageFilePath = absoluteCameraImagePath;
        this.bitmap = getBitmapFromURL(cameraImageFilePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float widthRatio = (float) height / width;
        if (height > width) {
            //portrait
            bitmap = Bitmap.createScaledBitmap(bitmap, width, (int)(widthRatio * width), true);
        } else {
            //landscape
            bitmap = Bitmap.createScaledBitmap(bitmap, (int)(widthRatio * height), height, true);
        }
        ivPicture.setImageBitmap(this.bitmap);
//        doCropping(CROP_CAMERA_REQUEST);
    }

    private void onResultGalleryOK() {
        this.cameraImageFilePath = selectedImageUri.toString();
        this.bitmap = getBitmapFromURL(getRealPathFromURI(context,
                selectedImageUri));
        if (bitmap == null){
            return;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float widthRatio = (float) height / width;
        if (height > width) {
            //portrait
            bitmap = Bitmap.createScaledBitmap(bitmap, width, (int)(widthRatio * width), true);
        } else {
            //landscape
            bitmap = Bitmap.createScaledBitmap(bitmap, (int)(widthRatio * height), height, true);
        }
        ivPicture.setImageBitmap(this.bitmap);
//        doCropping(CROP_GALLARY_REQUEST);
    }

    private void resultOnCropOkOfCamera(Intent data) {
        this.bitmap = data.getExtras().getParcelable("data");
        Log.v("", "cameraImageFilePath on crop camera ok => "
                + cameraImageFilePath);
        setImageProfile();
    }

    private void resultOnCropOkOfGallary(Intent data) {
        Bundle extras2 = data.getExtras();
        this.bitmap = extras2.getParcelable("data");
        setImageProfile();
    }

    private void resultOnCroppingCancel() {
        Log.v("", "do cropping cancel" + cameraImageFilePath);
        setImageProfile();
    }

    private void setImageProfile() {
        Log.v("", "cameraImagePath = > " + cameraImageFilePath);
        if (ivPicture != null) {
            if (bitmap != null) {
                ivPicture.setImageBitmap(bitmap);
            } else {
                Log.v("", "bitmap is null");
            }
        }
    }

    public Bitmap getImage(){
        return bitmap;
    }

    public Uri getImageUri(){
        return selectedImageUri;
    }
}




