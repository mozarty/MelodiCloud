package com.melodicloud.services;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.melodicloud.common.Global;
import com.melodicloud.util.FileNameUtil;
import com.melodicloud.util.LogUtil;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;

import java.io.File;
import java.util.HashMap;

import de.voidplus.soundcloud.Track;

/**
 * Created by Salem on 29-Apr-15.
 */
public class DownloadService {

    static Context context;

    static HashMap<Long, Track> downloadQueue = new HashMap<>();

    public static void init(Context context) {
        DownloadService.context = context;
        // when initialize
        context.registerReceiver(downloadCompleteReceiver, downloadCompleteIntentFilter);
    }

    private static String downloadCompleteIntentName = DownloadManager.ACTION_DOWNLOAD_COMPLETE;
    private static IntentFilter downloadCompleteIntentFilter = new IntentFilter(downloadCompleteIntentName);
    private static BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
            if (downloadQueue.get(id) != null) {
                Track track = downloadQueue.get(id);

                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(id);
                Cursor cursor = downloadManager.query(query);

                // it shouldn't be empty, but just in case
                if (!cursor.moveToFirst()) {
                    LogUtil.w("DOWNLOAD", "Download Failed -- 2");
                    return;
                }
                int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL != cursor.getInt(statusIndex)) {
                    LogUtil.w("DOWNLOAD", "Download Failed with status : " + statusIndex);

                    return;
                }

                int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                String downloadedPackageUriString = cursor.getString(uriIndex);

                LogUtil.w("DOWNLOAD", "downloading file metadata " + downloadedPackageUriString);

                afterDownloadAction(downloadedPackageUriString, track);

                return;
            } else {
                LogUtil.w("DOWNLOAD", "Download doesn't exist");
            }
        }
    };

    private static void afterDownloadAction(String downloadedPackageUriString, final Track track) {
        LogUtil.d("DOWNLOAD", "Downloaded file to " + downloadedPackageUriString);

        try {
            final AudioFile f = AudioFileIO.read(new File(downloadedPackageUriString));
            Tag tag = f.getTag();
            try {
                track.setSoundCloud(Global.soundcloud);
                tag.setField(FieldKey.LYRICS, track.getDescription());
                tag.setField(FieldKey.TITLE, track.getTitle());
                tag.setField(FieldKey.COMMENT, track.getStreamUrl());
            } catch (Exception e) {
            }
            f.commit();

            Glide.with(Global.appContext).load(track.getArtworkUrl()).asBitmap()
                    .toBytes().into(new SimpleTarget<byte[]>() {

                @Override
                public void onResourceReady(byte[] data, GlideAnimation anim) {
                    // Post your bytes to a background thread and upload them here.
                    Tag tag = f.getTag();
                    Artwork artwork = new Artwork();
                    artwork.setDescription(track.getDescription());
                    artwork.setBinaryData(data);
                    try {
                        tag.setField(artwork);
                        f.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (Exception e) {
        }
    }


    public static void downloadFile(Track track) {
        track.setSoundCloud(Global.soundcloud);

        String fileName = FileNameUtil.sanitizeFileName(track.getTitle()) + ".mp3";

        File dir = new File(Environment.getExternalStorageDirectory(), "CloudLiberty");
        File file = new File(dir, fileName);
        if (file.exists()) {
            LogUtil.i("DOWNLOAD", "file " + file.getAbsolutePath() + " already exists");
            if (file.length() == 0) {
                LogUtil.w("DOWNLOAD", "file " + file.getAbsolutePath() + " is 0 bytes");
                file.delete();
            }
        } else {
            try {
                //file.delete();
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);


                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(track.getStreamUrl()));

                LogUtil.i("DOWNLOAD", "downloading  " + track.getStreamUrl());
                // only download via WIFI
                //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                request.setTitle(track.getTitle());
                //request.setDescription("Downloading a very large zip");

                // we just want to download silently
                request.setVisibleInDownloadsUi(true);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                request.setDestinationInExternalPublicDir("/CloudLiberty", fileName);

                LogUtil.i("DOWNLOAD", "into  /CloudLiberty/" + fileName);
                long downloadID = downloadManager.enqueue(request);

                downloadQueue.put(downloadID, track);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
