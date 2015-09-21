package example.com.streamjungle.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import example.com.streamjungle.R;
import example.com.streamjungle.rest.model.SoundCloudModel;
import example.com.streamjungle.utility.Contants;
import example.com.streamjungle.utility.CurrentSongEvent;
import example.com.streamjungle.utility.CustomDeserializer;
import example.com.streamjungle.utility.MediaPlayerEvent;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener ,
                                                SeekBar.OnSeekBarChangeListener, Runnable{
    private String TAG = "PlayerActivity";

    private SoundCloudModel mDataModel;
    //play-pause media control
    @InjectView(R.id.playPause_control)
    ImageView mPlayPauseControl;
    //prev media control
    @InjectView(R.id.prev_control)
    ImageView mPrevMediaControl;
    //next media control
    @InjectView(R.id.next_control)
    ImageView mNextMediaControl;
    //seekba that is synchronized with the song
    @InjectView(R.id.songSeekBar)
    SeekBar mSongSeekBar;
    //time elapse when music is playing
    @InjectView(R.id.timeElapseTxt)
    TextView mTimeElapse;
    //time left for a specific music to finish
    @InjectView(R.id.timeLeftTxt)
    TextView mTimeLeft;



    /**
     * this are the top views elements
     */
    @InjectView(R.id.titleTV)
    TextView mTitle;

    @InjectView(R.id.genreTV)
    TextView mGenre;

    @InjectView(R.id.artworkIV)
    ImageView mAlbumArtwork;

    @InjectView(R.id.selected_track_img)
    ImageView mSelectedTrackImg;

    //List of all songs that we have
    private List<SoundCloudModel> mTrackList;
    //current song
    private int mCurrentSong;
    //boolean to handle state of the song
    private boolean mSongIsPlaying = false;
    //total number of song that we have in our mTrackList
    private int mTotalNumberOfSong;
    //total duration of the current song
    int mCurrentSongDuration=0;
    // Boolean specifying whether or not the PlayerActivity is running
    // we want to skip the first time the activity is created, so we dont set up the activity twice
    private boolean mIsPlayerActivityOn = true;
    //durration total of song in a form  mm:ss
    private long mFormattedSongTotalDuration;
    //progress of the song when playing
  //  private int mSongSecondprogress=0;

//    Handler mRefresh ;
//   // Runnable mRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.inject(this);//inject views

        //set onclick listener to the player control image view that toggle the player pause/play state
        mPlayPauseControl.setOnClickListener(this);
        // set onclick listener to the player 'next' control image view play that play the next song
        mNextMediaControl.setOnClickListener(this);
        // set onclick listener to the player 'next' control image view play that play the next song
        mPrevMediaControl.setOnClickListener(this);
        //set listener to our seek bar
        mSongSeekBar.setOnSeekBarChangeListener(this);
        mSongSeekBar.setClickable(false);

       // mSongSeekBar.setMax(13445);

       // mRefresh = new Handler(Looper.getMainLooper());



        Intent intent = getIntent();
        if (intent.hasExtra(Contants.CURRENT_SONG)) {
            GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(List.class, new CustomDeserializer());
            Gson gson = gsonBuilder.create();
            //getting the whole song list, NOTE: we create a custom deserializer for this
            mTrackList = gson.fromJson(intent.getStringExtra(Contants.SONG_WHOLE_DATA), List.class);
            //getting the total number of songs
            mTotalNumberOfSong = mTrackList.size();
            //the current song that user clicked in order to start this activity
            mCurrentSong = intent.getIntExtra(Contants.CURRENT_SONG, 0);
            //getting the boolean to see whether the song is playing or pause or stop and so on..
            mSongIsPlaying = intent.getBooleanExtra(Contants.IS_SONG_PLAYING, false); //default true
            //playing the current song user selected
            mDataModel = mTrackList.get(mCurrentSong);
            //displayCurrentTrack(mDataModel);//method defined bellow
        }



    }


    /**
     * we would like to start and bind to the service when this activity instance starts
     */
    @Override
    protected void onStart() {
        super.onStart();
         //registering all the receiver in this class
        SongSingleton.getInstance().registerMyBus(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
       // mIsPlayerActivityOn = false; //this means that PlayerActivity is off
         // unregistering all the receiver in this class
        SongSingleton.getInstance().unRegisterMyBus(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mIsPlayerActivityOn = true; //this means that PlayerActivity is on
        //send an event to service(TrackService) notifying it that the this activity is running so it
        // can notify our PlayerActivity when a song events, such as going to the next song in the list
        SongSingleton.getInstance().postMsg(new CurrentSongEvent(CurrentSongEvent.CurrentSong.PLAYER_ACTIVITY_IS_ON));
    }

    /**
     * this will display the current track infos in their appropriate view element.
     *
     * @param dataModel is SoundCloudModel
     */
    public void displayCurrentTrack(SoundCloudModel dataModel) {
       // mSongSeekBar.setMax(mCurrentSongDuration);

        //mSongSeekBar.setMax(mCurrentSongDuration/1000);//set the seekbar to our song duration
        String arworkImg = dataModel.getArtworkUrl();
        Log.d(TAG, " displayCurrentTrack()-mCurrentSongDuration: " + mCurrentSongDuration);

        Log.d(TAG, "displayCurrentTrack()-arworkImg: " + arworkImg);

        /**
         * Some tracks dont have an album image. We need to display the default image in those cases
         */
        if (arworkImg == null) {
            Picasso.with(this).load(R.drawable.default_artwork_img).into(mAlbumArtwork);
            Picasso.with(this).load(R.drawable.default_artwork_img).into(mSelectedTrackImg);
        } else {
            arworkImg = arworkImg.replace("large", "t500x500");
            Picasso.with(this).load(dataModel.getArtworkUrl()).into(mAlbumArtwork);
            Picasso.with(this).load(arworkImg).into(mSelectedTrackImg);
        }
        mTitle.setText(dataModel.getTitle());
        mGenre.setText(dataModel.getGenre());
    }


    /**
     * This method toggle the play and pause button. It first checks to see if the media is playing.
     * if so, it pauses it, and display the play icon. Otherwise, if it is not playing, it will
     * just start and display the pause icon
     */
    public void togglePlayPause() {
        if (mSongIsPlaying == true) {//SONG IS PLAYING
            //now i am setting the event code to, to differenciate the events
            SongSingleton.getInstance().postMsg(new CurrentSongEvent(CurrentSongEvent.CurrentSong.SONG_STOP, 1));
            mPlayPauseControl.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
            mSongIsPlaying = false;
        } else {
            //now i am setting the event code to, to differentiate the events
            SongSingleton.getInstance().postMsg(new CurrentSongEvent(CurrentSongEvent.CurrentSong.SONG_PLAY, 1));
            mPlayPauseControl.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
            mSongIsPlaying = true;
        }
    }

    /**
     * This method will play the next song in the mSongList
     */
    public void playNextSong() {
        if ((mCurrentSong+1) < mTotalNumberOfSong) { //need to check if we are not at the end of the list
            //I am making this event code to 1, so the service can catch the event and handle it.
            SongSingleton.getInstance().postMsg(new CurrentSongEvent(CurrentSongEvent.CurrentSong.SONG_NEXT, 1));
            //displayCurrentTrack(mTrackList.get(mCurrentSong));//set the image to current song one
            Log.d(TAG, "playNextSong()==>>mCurrentSong: " + mCurrentSong);
        }
    }
    /**
     * This method will play the previous song in the mSongList
     */
    public void playPrevSong() {
        if ((mCurrentSong-1) >= 0) {
            //I am making this event code to 1, so the service can catch the event and handle it.
            SongSingleton.getInstance().postMsg(new CurrentSongEvent(CurrentSongEvent.CurrentSong.SONG_PREV, 1));
            //displayCurrentTrack(mTrackList.get(mCurrentSong-1));//set the image to current song one

            Log.d(TAG, " playPrevSong()==>>mCurrentSong: " + (mCurrentSong-1));
        }
    }

    /**
     * this method convert my song duration(which is given in millisecond) into minute and second,
     * in a form=> mm:ss
     * @param songDuration is the duration of a specific song in milliseconds
     * @return return a string format of the time in a form mm:ss
     */
    private String millisecondToMinuteAndSecond(int songDuration){
        // 1 second = 1000 millisecond
        //to get only 2 diget for second we use modulo
        int sec  = (int)(songDuration/1000)%60; //second value
        int min = (int)((songDuration/(1000*60))%60); //minute value
        //int hour = (int)((songDuration/(1000*60*60))%24); //hour value
        return String.format("%d:%02d", min, sec);
    }

    /**
     * this method will subtract the number of second from the total time until it is = 0;
     * then convert it in a form=> mm:ss
     * @param songTotalDuration total duration of the song in millisecond,
     * @param  timeInSecond is the current number of second the media is currently at.
     * @return return a string format of the time in a form mm:ss
     */
    private String doTimeLeft(int songTotalDuration, int timeInSecond){
        // 1 second = 1000 millisecond
        //songTotalDuration is in millisecond, that is why we are dividing it by 1000
        // timeInSecond is in second, that is why i did not divide it by 1000
        int timeLeft = (songTotalDuration/1000) - timeInSecond;
        //to get only 2 diget for second we use modulo
        int sec  = (int)(timeLeft)%60; //second value
        int min = (int)((timeLeft/60)%60); //minute value
        return String.format("-%d:%02d", min, sec);
    }


    /**
     * this method will increase the number of second until it is = total time;
     * then convert it in a form=> mm:ss
     * @param  timeInSecond is the current number of second the media is currently at.
     * @return return a string format of the time in a form mm:ss
     */
    private String doTimeElapsed(int timeInSecond){
        // timeInSecond is in second, that is why i did not divide it by 1000
        //to get only 2 diget for second we use modulo
        int sec  = (int)(timeInSecond)%60; //second value
        int min = (int)((timeInSecond/60)%60); //minute value
        return String.format("%d:%2d", min, sec);
    }



    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.playPause_control:
                togglePlayPause();
                break;
            case R.id.next_control:
                playNextSong();
                break;
            case R.id.prev_control:
                playPrevSong();
            break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            //tell the media to jump to that specific location where the seekbar is
            //NOTE: i made that request code '1'
            SongSingleton.getInstance().postMsg(new MediaPlayerEvent(
                            MediaPlayerEvent.MediaSong.SEEK_BAR, 1, progress
                    )
            );
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    /**
     * This method will handle all the events that will be sent from the service when the current
     * song is finished. It will first check to see if their is other songs to play, if yes, it will
     * display the appropriate image and icons base on the next song.
     */
    @Subscribe
    public void currentSongFinishEvent(CurrentSongEvent currentSongEvent) {
        if (currentSongEvent.getResulCode() == 2) {
//            mCurrentSong = currentSongEvent.getCurrentTrack();
//            mCurrentSongDuration = currentSongEvent.getSongDuration();//total song duration
//            displayCurrentTrack(mTrackList.get(mCurrentSong));
//            if(currentSongEvent.getType() == CurrentSongEvent.CurrentSong.SONG_STOP) {
//                mSongIsPlaying = false;
//                togglePlayPause();
//            }
//            //Log.d(TAG, " Event-CURRENT_TRACK;  mCurrentSong: " + mCurrentSong);

            if (currentSongEvent.getType() == CurrentSongEvent.CurrentSong.SONG_NEXT) {
                mCurrentSong = currentSongEvent.getCurrentTrack();
                mCurrentSongDuration = currentSongEvent.getSongDuration();//total song duration
                mSongSeekBar.setMax(mCurrentSongDuration / 1000);
              //  if ((mCurrentSong++) < mTotalNumberOfSong) { //need to check if we are not at the end of the list
                    displayCurrentTrack(mTrackList.get(mCurrentSong));
                    Log.d(TAG, " Event-SONG_NEXT;  mCurrentSongDuration: " + mCurrentSongDuration);
                //mTimeLeft.setText(millisecondToMinuteAndSecond(mCurrentSongDuration));
                //mTimeElapse.setText(millisecondToMinuteAndSecond(mCurrentSongDuration));
               // }
            } else if (currentSongEvent.getType() == CurrentSongEvent.CurrentSong.SONG_PREV) {
                mCurrentSong = currentSongEvent.getCurrentTrack();
                mCurrentSongDuration = currentSongEvent.getSongDuration();//total song duration
                mSongSeekBar.setMax(mCurrentSongDuration/1000);
                //Set time to integer reprsentation
//                Calendar calendar2 = Calendar.getInstance();
//                calendar2.setTimeInMillis(mCurrentSongDuration);
//                mTimeElapse.setText(mCurrentSongDuration);

                //  if ((mCurrentSong--) >= 0) {
                   displayCurrentTrack(mTrackList.get(mCurrentSong));
                Log.d(TAG, " Event-SONG_PREV;  mCurrentSong: " + mCurrentSong);

                //}
            } else if (currentSongEvent.getType() == CurrentSongEvent.CurrentSong.SONG_STOP) {
                mCurrentSong = currentSongEvent.getCurrentTrack();
               // displayCurrentTrack(mTrackList.get(mCurrentSong));
                mSongIsPlaying = false;
                togglePlayPause();
            } else if (currentSongEvent.getType() == CurrentSongEvent.CurrentSong.CURRENT_TRACK) {
                Log.d(TAG, "mIsPlayerActivityOn: "+mIsPlayerActivityOn);

                if(mIsPlayerActivityOn !=true) {//this will skip the first time the activity is creating
                    mCurrentSong = currentSongEvent.getCurrentTrack();
                    displayCurrentTrack(mTrackList.get(mCurrentSong));
               }
                mIsPlayerActivityOn  = false; //make true so the event dont try to set this activity's view again

            }
        }
    }
    /**
     * This method will handle all the events that will be sent from the service when the current
     * song is playing.
     */
    int a =0;

    @Subscribe
    public void mediaPlayerEvent(final MediaPlayerEvent mediaPlayerEvent) {
        if(mediaPlayerEvent.getType() == MediaPlayerEvent.MediaSong.SEEK_BAR) {
            //I am comparing the result code to '2' here, because i put a code of '2' when i was sending
            //this event.
            if (mediaPlayerEvent.getResulCode() == 2) {
               // mSongSecondprogress = mediaPlayerEvent.getCurrentPositionInASong();

                mSongSeekBar.setProgress(mediaPlayerEvent.getCurrentPositionInASong());
               // setCollpaseTime(mediaPlayerEvent.getCurrentPositionInASong());

                runOnUiThread(new Runnable() {
                    public void run(){

                        mTimeLeft.setText(String.valueOf(doTimeLeft(mCurrentSongDuration, mediaPlayerEvent.getCurrentPositionInASong())));

                        mTimeElapse.setText(String.valueOf(doTimeElapsed(mediaPlayerEvent.getCurrentPositionInASong())));
                    }
                });

            }

        }

    }
//
//    private void setCollpaseTime(int second){
//        mSongSecondprogress =second;
//        mRefresh.post(mRunnable);
//
//       // mTimeElapse.setText(second);
//
//    }
//
//
//    Runnable mRunnable = new Runnable() {
//        public void run() {
//            mTimeElapse.setText(String.valueOf(mSongSecondprogress));
//        }
//    };


    @Override
    public void run() {

    }
}