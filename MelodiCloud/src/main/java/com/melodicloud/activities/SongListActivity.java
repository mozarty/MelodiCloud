package com.melodicloud.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.melodicloud.R;
import com.melodicloud.adapters.SongsAdapter;
import com.melodicloud.services.DownloadService;
import com.melodicloud.services.requests.LoadSongsRequest;
import com.melodicloud.util.FileNameUtil;
import com.melodicloud.util.LogUtil;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.percolate.caffeine.ToastUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.voidplus.soundcloud.Track;

/**
 * Created by Salem on 02-Apr-15.
 */
@EActivity(R.layout.activity_song_list)
public class SongListActivity extends BaseActivity implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private LoadSongsRequest loadSongsRequest = null;


    private SongsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @ViewById(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    @ViewById(R.id.recycleView)
    RecyclerView recyclerView;

    @ViewById(R.id.songSum)
    TextView songSum;

    @Override
    public void onViewsLoaded() {
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(SongListActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        refreshLayout.setOnRefreshListener(this);

        /**
         * Now we seets the colors to be used in the refresh animation.
         **/
        refreshLayout.setColorSchemeResources(R.color.blue_bright,
                R.color.green_light,
                R.color.orange_light,
                R.color.red_light);
        loadSongs(false);
    }

    private void loadSongs(boolean forceRefresh) {

        startRefreshing();
        loadSongsRequest = new LoadSongsRequest(spiceManager,forceRefresh);

        spiceManager.execute(loadSongsRequest, lastRequestCacheKey,
                DurationInMillis.ALWAYS_RETURNED, new SongsLoadListener());
        ToastUtils.quickToast(this, "LoadingSongs");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        SearchManager SManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchViewAction = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchViewAction.setSearchableInfo(SManager.getSearchableInfo(getComponentName()));
        searchViewAction.setIconifiedByDefault(true);
        searchViewAction.setOnQueryTextListener(this);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_download:
                File dir = new File(Environment.getExternalStorageDirectory(), "CloudLiberty");
                dir.mkdirs();
                for (int i = 0; i < mAdapter.getItemCount(); i++) {
                    Track track = mAdapter.getItem(i);
                    String filename = FileNameUtil.sanitizeFileName(track.getTitle());
                    LogUtil.d("FILES", filename);

                    downloadTrack(track);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Background
    protected void downloadTrack(Track track) {
        DownloadService.downloadFile(track);
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (mAdapter != null)
            mAdapter.getFilter().filter(s);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public void onRefresh() {
        loadSongs(true);


    }

    @UiThread
    protected void stopRefreshing() {
        refreshLayout.setRefreshing(false);
    }

    @UiThread
    protected void startRefreshing() {
        TypedValue typed_value = new TypedValue();
        getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
        refreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));

        refreshLayout.setRefreshing(true);
    }

    private class SongsLoadListener implements
            RequestListener<ArrayList<Track>> {

        @Override
        public void onRequestFailure(SpiceException e) {
            ToastUtils.quickToast(SongListActivity.this, "Failed to load songs");
            stopRefreshing();
        }

        @Override
        public void onRequestSuccess(ArrayList<Track> songs) {
            loadSongsRequest = null;

            stopRefreshing();
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            //recyclerView.setHasFixedSize(true);

            // specify an adapter (see also next example)
            mAdapter = new SongsAdapter(songs);
            recyclerView.setAdapter(mAdapter);

            songSum.setText(songs.size() + " songs");

            ToastUtils.quickToast(SongListActivity.this, "Successfully loaded " + songs.size() + " songs");
        }


    }

}
