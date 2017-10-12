package com.ntsoft.ihhq.utility.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;


import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.utility.BitmapUtility;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 3/16/2016.
 */
public class TakePictureFromCameraManager {

    private static final String JPEG_FILE_PREFIX = "AllyTour_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private Context mContext;
    private ImageView destinationImageView;
    private String photoPath;

    public TakePictureFromCameraManager(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        mContext = context;
        photoPath = "";
    }
    public void setDestinationImageView(ImageView imageView) {
        this.destinationImageView = imageView;
    }
    public void setPhotoPath(String path) {
        this.photoPath = path;
    }
    public File setUpPhotoFile() throws IOException {

        File f = createImageFile();
//        arrPhotoPathes.add(f.getAbsolutePath());

        return f;
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }
    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir("AllyTours");

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(mContext.getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    ///process result of captured photo
    public void handleBigCameraPhoto() {

        if (photoPath.length() > 0) {

            setPicture();
//            galleryAddPic();
        }

    }
    public void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        Bitmap bitmap = (Bitmap) extras.get("data");
        destinationImageView.setImageBitmap(bitmap);

    }
    private void setPicture() {
        ////////process image to lower quality
//        String processedImage = BitmapUtility.saveProgressimageToSDCARD(bitmap, photoPath.substring(photoPath.lastIndexOf("/") + 1), "Allytours");
//        Bitmap finalbitmap = BitmapFactory.decodeFile(processedImage);
        Bitmap adjustBitmap = BitmapUtility.adjustBitmap(photoPath);
        Bitmap croppedBitmap = BitmapUtility.cropBitmapAnySize(adjustBitmap, 200, 200);
        BitmapUtility.saveBitmapToLocal(croppedBitmap, photoPath, photoPath);
        destinationImageView.setImageBitmap(croppedBitmap);
    }

//    private void setPic() {
//
//		/* There isn't enough memory to open up more than a couple camera photos */
//		/* So pre-scale the target bitmap into which the file is decoded */
//
//		/* Get the size of the ImageView */
//        int targetW = destinationImageView.getWidth();
//        int targetH = destinationImageView.getHeight();
//
//		/* Get the size of the image */
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(photoPath, bmOptions);
//
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//		/* Figure out which way needs to be reduced less */
//        int scaleFactor = 1;
//        if ((targetW > 0) || (targetH > 0)) {
//            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//        }
//
//		/* Set bitmap options to scale the image decode target */
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = null;
//
//		/* Associate the Bitmap to the ImageView */
//         /* Decode the JPEG file into a Bitmap */
//        bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
////                bitmap = BitmapUtility.rotateImage(bitmap, 90);
//
//        destinationImageView.setImageBitmap(bitmap);
//
//    }
}
