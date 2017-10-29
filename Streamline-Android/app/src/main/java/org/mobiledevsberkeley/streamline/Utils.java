package org.mobiledevsberkeley.streamline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nzp on 10/26/17.
 */

public class Utils {

    private static final int SECONDS_IN_ONE_DAY = 86400;

    public static void fetchAlbumArt(Context context, String uri, final ImageView imageView, final Post post) {
        Picasso.with(context).load(uri).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImageBitmap(bitmap);
                post.setBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("PICASSO", "Could not load image");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}

        });
    }

    public static String formatTime(Double time) {
        return new SimpleDateFormat("hh:mm a", Locale.US).format(new Date((long) Math.floor(time) * 1000L));
    }

    public static long oneDayAgo() {
        long currentDate = (new Date().getTime()) / 1000L;
        return currentDate - SECONDS_IN_ONE_DAY;
    }

}
