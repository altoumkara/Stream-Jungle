package example.com.streamjungle.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import example.com.streamjungle.R;
import example.com.streamjungle.rest.model.SoundCloudModel;
import example.com.streamjungle.rest.service.SoundClientService;
import example.com.streamjungle.services.TrackService;
import example.com.streamjungle.utility.SoundCloudSingleton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * we want the music to be played in the service. But the operation(the media controllers that user
 * interact with) should be in the main activity
 */

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    //the service that will play the track in the background
    private TrackService mTrackService;
    //boolean to check if our activity is bound to the service.
    private boolean mTrackBound = false;
    //intent used to send the current song to the service
    private Intent mServiceIntent;
    //Binder used to bind to service
    private TrackServiceConnection mTrackServiceConnection = new TrackServiceConnection();
    //List of all songs that we have
     private List<SoundCloudModel> mTrackList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.track_recylerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        initRestCall();
    }



    /**
     * use to initialize the Http call to the rest api
     */
    public void initRestCall() {

        SoundClientService soundClientService = SoundCloudSingleton.getSoundClientService();
        /**
         * on SoundCloud api documentation : https://developers.soundcloud.com/docs/api/reference#tracks
         * the query string( created_at[from]) for date look like:=	date (yyyy-mm-dd hh:mm:ss)
         * return tracks created at this date or later.
         *
         * note: in "yyyy-MM-dd hh:mm:ss", MM(month) must be uppercase to differentiate it from mm(minute)
         */
        soundClientService.getTrack(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), new Callback<List<SoundCloudModel>>() {

            @Override
            public void success(List<SoundCloudModel> soundCloudModels, Response response) {
                mRecyclerView.setAdapter(new RecylerViewAdapter(MainActivity.this, soundCloudModels, mTrackBound, mTrackService));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Error: " + error);
            }
        });
    }

    /**
     * we would like to start and bind to the service when this activity instance starts
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mServiceIntent == null) {
            mServiceIntent = new Intent(this, TrackService.class);
            /**
             * Context.BIND_AUTO_CREATE==>
             * Flag for {@link #bindService}: automatically create the service as long
             * as the binding exists.  Note that while this will create the service,
             * its {@link android.app.Service#onStartCommand}
             * method will still only be called due to an
             * explicit call to {@link #startService}.  Even without that, though,
             * this still provides you with access to the service object while the
             * service is created.
             */
            bindService(mServiceIntent, mTrackServiceConnection, Context.BIND_AUTO_CREATE);
            startService(mServiceIntent);//must call this for Service#onStartCommand to be called
        }

    }



    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        mTrackService = null;
        super.onDestroy();
    }


    /**
     * define a callback for service binding, passed to bindService().
     * This implementation of ServiceConnection, monitors the connection with the
     * service(TrackService class in this case)
     */
    private class TrackServiceConnection implements android.content.ServiceConnection {

        /**
         * called when client connect to a service.
         *
         * @param name
         * @param service is the Ibinder(mIBinder,In our case) returned from the onBind() method in
         *                our TrackService class.
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //TrackBinder is the custom binder class we define in TrackService class
            TrackService.TrackBinder binder = (TrackService.TrackBinder) service;
            //get and set our service that we are getting from the binder
            mTrackService = binder.getTrackService();
            //now we are bound to the service
            mTrackBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //now we are not bound to the service
            mTrackBound = false;
        }
    }

}