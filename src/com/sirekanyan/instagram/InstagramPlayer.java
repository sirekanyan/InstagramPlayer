package com.sirekanyan.instagram;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;

import java.util.concurrent.ExecutionException;

/**
 * User: sirekanyan, date: 10/17/15
 */
public class InstagramPlayer extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getIntent().getData() != null) {
            String pageUrl = getIntent().getData().toString();
            try {
                openMediaFile(new InstagramAsyncTask().execute(pageUrl).get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void openMediaFile(MetaProperties media) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        boolean hasVideo = media.getVideo() != null;
        String directLink = hasVideo ? media.getVideo() : media.getImage();

        if (hasVideo) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(directLink);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            intent.setDataAndType(Uri.parse(directLink), mimeType);
            intent.putExtra(Intent.EXTRA_TITLE, media.getTitle());
            intent.putExtra("title", media.getTitle());
        } else {
            intent.setData(Uri.parse(directLink));
        }

        try {
            startActivity(intent);
            finish();
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            if (hasVideo) {
                downloadVideoPlayer();
            }
        }
    }

    private void downloadVideoPlayer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.player_not_found)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton(R.string.install, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://f-droid.org/repository/browse/?fdid=org.videolan.vlc"));
                        startActivity(intent);
                        finish();
                    }
                });
        builder.create().show();
    }
}
