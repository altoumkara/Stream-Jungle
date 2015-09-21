package example.com.streamjungle.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.List;

import example.com.streamjungle.rest.model.SoundCloudModel;
import example.com.streamjungle.ui.activities.SongSingleton;
import example.com.streamjungle.utility.Contants;
import example.com.streamjungle.utility.CurrentSongEvent;
import example.com.streamjungle.utility.MediaPlayerEvent;

/**
 * Created by alamatounkara on 9/8/15.
 */
public class TrackService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, Runnable {
    //IBinder that the client will use to connect to this service
    private final IBinder mIBinder = new TrackBinder();
    private String TAG = "TrackService";
    //single song object
    private SoundCloudModel mDataModel;
    //List of all songs that we have
    private List<SoundCloudModel> mSongList;
    //total number of song that we have in our mTrackList
    private int mTotalNumberOfSong;
    //media player
    private MediaPlayer mMediaPlayer;
    //this will keep track of the position of the current song
    private int mCurrentPositon;
    //Boolean specifying whether or not the PlayerActivity is running
    private boolean mIsPlayerActivityOn = false;
    //duration total of the song
    int mCurrentSongDuration=0;

    //this the current place of the song when its playing, this value will be sent to the seekbar on
    //the PlayerActivity so it can update the seekbar
    int mCurrentPositionInASong=0;


    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * registering all the receiver in this class
         */
        SongSingleton.getInstance().registerMyBus(this);

        mMediaPlayer = new MediaPlayer();
        mCurrentPositon = 0;
        setMediaPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder; //client can get this form onServiceConnected callback method
    }

    /**
     * destroy the media player the activity is destroying and we are realease resources here
     */
    @Override
    public boolean onUnbind(Intent intent) {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;//garbage collector will take care of its memory
            /**
             * unregistering all the receiver in this class
             */
            SongSingleton.getInstance().unRegisterMyBus(this);
        }
        return false;

    }

    /**
     * This method initializes my media player when the service is started.
     * <p/>
     * don't forget to set "WAKE_LOCK" permission in the manifest
     */
    public void setMediaPlayer() {
        /**
         * public void setAudioStreamType(int streamtype)
         * Sets the audio stream type for this MediaPlayer. See {@link AudioManager}
         * for a list of stream types. Must call this method before prepare() or
         * prepareAsync() in order for the target stream type to become effective
         * thereafter.
         *
         * @param streamtype the audio stream type
         * @see android.media.AudioManager
         * AudioManager.STREAM_MUSIC is The audio stream for music playback(a term that refers
         * to when (during a performance) a musical group plays a prerecorded track and lip-synchs on stage )
         */
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        /**
         * Register a callback to be invoked when the media source is ready
         * for playback.
         *
         * "MediaPlayer.OnPreparedListener" the callback that will be run  when the media
         * source is ready for playback.
         */
        mMediaPlayer.setOnPreparedListener(this);
        /**
         * MediaPlayer.OnCompletionListener:
         * when a song finish playing, the mPlayerControl will still show the pause icon.
         * we want to avoid that. There we can use OnCompletionListener to display the play icon
         * when a song finish playing
         */
        mMediaPlayer.setOnCompletionListener(this);

        /**
         * Listener for when error is thrown
         */
        mMediaPlayer.setOnErrorListener(this);
    }

    /**
     * This method set my media player to play a specific song. It first checks to see
     * if the media is playing. if so,it stops the current song and reset the MediaPlayer. then we
     * load the new song from the list which is the full URL of where the streamed
     * audio file.Then we prepare the player for playback asynchronously
     */
    public void playSong() {
        if ((mMediaPlayer != null) && (mMediaPlayer.isPlaying())) {
            mMediaPlayer.stop();
        }
        /**
         * Resets the MediaPlayer to its uninitialized state. After calling
         * this method, you will have to initialize it again by setting the
         * data source and calling prepare().
         */
        mMediaPlayer.reset();

        //get the current song from the list
        mDataModel = mSongList.get(mCurrentPositon);
        try {
            Log.d(TAG, "mDataModel: " + mDataModel);

            //set the source
            Log.d(TAG, "mDataModel.getStreamUrl(): " + mDataModel.getStreamUrl() + "?client_id=" + Contants.CLIENT_ID);
            mMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(mDataModel.getStreamUrl() + "?client_id=" + Contants.CLIENT_ID));
            // mMediaPlayer.setDataSource(mDataModel.getStreamUrl()+"?client_id="+ Contants.CLIENT_ID);

            /**
             * public native void prepareAsync() throws IllegalStateException;
             * Prepares the player for playback(a term that refers to when you
             *  play a prerecorded track), asynchronously.
             *
             * After setting the datasource and the display surface, you need to either
             * call prepare() or prepareAsync(). For streams, you should call prepareAsync(),
             * which returns immediately, rather than blocking until enough data has been
             * buffered.
             *
             * @throws IllegalStateException if it is called in an invalid state
             */
            mMediaPlayer.prepareAsync();

        } catch (IOException e) {
            Log.e(TAG, "Error setting data source", e);
        }
    }

    /**
     * Setter to set the mSongList data. We will get the list of track from our activity
     *
     * @param songList passed from activity to the service
     */
    public void setSongList(List<SoundCloudModel> songList) {
        this.mSongList = songList;
        //getting the total number of songs
        this.mTotalNumberOfSong = mSongList.size();
    }

    /**
     * allow user to set song
     *
     * @param position is position of the song to be playing
     */
    public void setCurrentPositon(int position) {
        mCurrentPositon = position;
    }

    /**
     * Called when the media file is ready for playback(a term that refers to when you
     * play a prerecorded track) this will be called after prepareAsync() or prepare() is called.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        //  int currentSongDuration = mMediaPlayer.getDuration();
        //first check if the PlayerActivity is running before wasting time sending it event
        if (mIsPlayerActivityOn == true) {
            //duration total of the song
            mCurrentSongDuration = mMediaPlayer.getDuration();
            //send an event to the player activity this song, so it can update its UI
            SongSingleton.getInstance().postMsg(new CurrentSongEvent(
                            CurrentSongEvent.CurrentSong.SONG_NEXT, 2, mCurrentPositon,
                            mCurrentSongDuration
                    )
            );
        }

        mMediaPlayer.start();
        new Thread(this).start();

    }

    @Override
    public void run() {
        while((mMediaPlayer != null)&&(mCurrentPositionInASong<=mCurrentSongDuration)) {
            try {
                Thread.sleep(1000);
                mCurrentPositionInASong = mMediaPlayer.getCurrentPosition()/1000;

            } catch (InterruptedException e) {
                Log.e(TAG, "ERROR UPDATING SEEKBAR: ", e);
            }
            SongSingleton.getInstance().postMsg(new MediaPlayerEvent(
                            MediaPlayerEvent.MediaSong.SEEK_BAR,2, mCurrentPositionInASong
                    )
            );
        }
    }
    /**
     * Callback to e invoke when error is thrown
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    /**
     * MediaPlayer.OnCompletionListener:
     * when a song finish playing, the mPlayerControl will still show the pause icon.
     * we want to avoid that. There we can use OnCompletionListener to display the play icon
     * when a song finish playing
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        playNextSong();
        //send an notification to the player activity that there if no more song to play
        if (mIsPlayerActivityOn == true) {
            //first check if the PlayerActivity is running before wasting time sending it event
            if (mCurrentPositon == mTotalNumberOfSong-1) {
                //send an event to the PlayerActivity notifying it that the List of song finish
                //i use 'mTotalNumberOfSong-1' because the last element in an array is length-1
                SongSingleton.getInstance().postMsg(new CurrentSongEvent(
                                CurrentSongEvent.CurrentSong.SONG_STOP, 2, mCurrentPositon
                        )
                );
            }
        }
    }




    /**
     * This method will play the next song in the mSongList
     */
    public void playNextSong() {
        if ((mCurrentPositon+1) < mTotalNumberOfSong) { //need to check if we are not at the end of the list
            mCurrentPositon++;
            playSong();
//            //first check if the PlayerActivity is running before wasting time sending it event
//            if (mIsPlayerActivityOn == true) {
//                //send an event to the player activity this song, so it can update its UI
//                SongSingleton.getInstance().postMsg(new CurrentSongEvent(
//                                CurrentSongEvent.CurrentSong.SONG_NEXT, 2, mCurrentPositon
//                        )
//                );
//            }
        }
    }


    /**
     * This method will play the previous song in the mSongList
     */
    public void playPrevSong() {
       // Log.d(TAG, "before if playPrevSong()==>>mCurrentPositon: " + mCurrentPositon);
        if ((mCurrentPositon-1)>= 0) {
            mCurrentPositon--;
            playSong();
        }
    }

    /**
     * This method will play the current song in the mSongList
     */
    public void playCurSong(int curSong) {
        if ((curSong >= 0) && (curSong < mTotalNumberOfSong)) {
            this.mCurrentPositon = curSong;
            playSong();
        }
    }






    /**
     * This method will handle the 'next' and 'prev' button push events that will be sent from the
     * PlayerActivity. If the user pushes the 'nect' button, the service will start playing the next
     * song in the list , if any. otherwise, if the user pushes the 'prev' button, the service starts
     * playing the previous song, if it is not less than 0.
     *
     * @param currentSongEvent the current song event
     */
    @Subscribe
    public void playSongEvent(CurrentSongEvent currentSongEvent) {
        //I am comparing the result code to '1' here, because i put a code of '1' when i was sending
        //this event.
        if (currentSongEvent.getResulCode() == 1) {
            if (currentSongEvent.getType() == CurrentSongEvent.CurrentSong.SONG_NEXT) {
                playNextSong();
            } else if (currentSongEvent.getType() == CurrentSongEvent.CurrentSong.SONG_PREV) {
                playPrevSong();
            } else {
            }
        }
    }



    /**
     * This method toggle the play and pause of the song according to the "play" and "pause" icon.
     * It first checks to see if the event gotten is a sCurrentSong.SONG_STOP then stop the media
     * player. Otherwise play the song.
     */
    @Subscribe
    public void togglePlayPauseEvent(CurrentSongEvent currentSongEvent) {
        if (currentSongEvent.getType() == CurrentSongEvent.CurrentSong.SONG_STOP) {
            mMediaPlayer.pause();
        } else if (currentSongEvent.getType() == CurrentSongEvent.CurrentSong.SONG_PLAY) {
            mMediaPlayer.start();
        } else {
        }
    }

    /**
     * This method will handle the event that will say that the PlayerActivity is running.
     * Therefore the service can send the current song information to it so it can display the view
     * appropriate for that song
     *
     * @param currentSongEvent the current song event
     */
    @Subscribe
    public void activityRunningEvent(CurrentSongEvent currentSongEvent) {
        //first check if the PlayerActivity is running before wasting time sending it event
        if (currentSongEvent.getType() == CurrentSongEvent.CurrentSong.PLAYER_ACTIVITY_IS_ON) {
            mIsPlayerActivityOn = true;
            if (mCurrentPositon == mTotalNumberOfSong) {//we are at the end of the list
                //send an event to the PlayerActivity notifying it that the List of song finished
                //i use 'mCurrentPositon-1' because if mCurrentPositon==mTotalNumberOfSong, we might
                //have an ArrayIndexOutOfBound error
                SongSingleton.getInstance().postMsg(new CurrentSongEvent(
                                CurrentSongEvent.CurrentSong.SONG_STOP, 2, mCurrentPositon - 1
                        )
                );
            } else { //there is still track to play
               // send an event to the PlayerActivity notifying it about the current song
                SongSingleton.getInstance().postMsg(new CurrentSongEvent(
                                CurrentSongEvent.CurrentSong.CURRENT_TRACK, 2, mCurrentPositon
                        )
                );
            }
        }
    }


    /**
     * This method will handle all seekbar events that will be sent from the PlayerActivity when the
     * the user uses the seekbar
     */
    @Subscribe
    public void seekBarEvent(MediaPlayerEvent mediaPlayerEvent) {
        if(mediaPlayerEvent.getType() == MediaPlayerEvent.MediaSong.SEEK_BAR) {
            //I am comparing the result code to '1' here, because i put a code of '1' when i was sending
            //this event.
            if (mediaPlayerEvent.getResulCode() == 1) {
                mMediaPlayer.seekTo(mediaPlayerEvent.getCurrentPositionInASong()*1000);
            }
        }
    }


    /**
     * Binder used to communicate with the client(our Activity)
     */
    public class TrackBinder extends Binder {
        public TrackService getTrackService() {
            return TrackService.this;
        }

    }


}
