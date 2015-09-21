package example.com.streamjungle.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import example.com.streamjungle.R;
import example.com.streamjungle.rest.model.SoundCloudModel;
import example.com.streamjungle.services.TrackService;
import example.com.streamjungle.utility.Contants;

/**
 * Created by alamatounkara on 9/7/15.
 */
public class RecylerViewAdapter extends RecyclerView.Adapter<RecylerViewAdapter.MyViewHolder> {
    private String TAG = "RecylerViewAdapter";

    //list containing all the songs
   private List<SoundCloudModel> mDataModel;
    //the service that will play the track in the background
    private TrackService mTrackService;
    //boolean to check if our activity is bound to the service.
    private boolean mTrackBound = false;
    //our context
    private Context mContext;
    //because we want to send the whole song list data(mDataModel) to the PlayerActivity when the
    //user click on a song from the list, we dont want to repeadlyoolean verify

    public RecylerViewAdapter(Context context, List<SoundCloudModel> data, boolean isServiceBound,
                                                                        TrackService trackService){
        this.mDataModel = data;
        this.mContext = context;
        this.mTrackBound=isServiceBound;
        this.mTrackService = trackService;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.single_track_row,viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return this.mDataModel.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        String arworkImg = mDataModel.get(i).getArtworkUrl();
        /**
         * Some traks dont have an album image. We need to display the default image in those cases
         */
        if(arworkImg ==null){
            Picasso.with(mContext).load(R.drawable.default_artwork_img).into( myViewHolder.mAlbumArtwork);
        }else {
            Picasso.with(mContext).load(mDataModel.get(i).getArtworkUrl()).into(myViewHolder.mAlbumArtwork);
        }
        myViewHolder.mTitle.setText(mDataModel.get(i).getTitle());
        myViewHolder.mGenre.setText(mDataModel.get(i).getGenre());
    }

    /**
     * use to set a new song
     */
    public void songPicked(int currentSong) {
        if (mTrackBound == true) {
            //now setting the song list(mSongList) in our mTrackService
            mTrackService.setSongList(mDataModel);
            mTrackService.playCurSong(currentSong);
        }
    }


    public class MyViewHolder extends ViewHolder implements View.OnClickListener {
        private TextView mTitle, mGenre;
        private ImageView mAlbumArtwork;
        private ViewGroup singleTrackRow;
        public MyViewHolder(View itemView) {
            super(itemView);
            mAlbumArtwork = (ImageView) itemView.findViewById(R.id.artworkIV);
            mGenre= (TextView) itemView.findViewById(R.id.genreTV);
            mTitle= (TextView) itemView.findViewById(R.id.titleTV);
            singleTrackRow= (ViewGroup) itemView.findViewById(R.id.track_row);
            singleTrackRow.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int currentSong = getAdapterPosition();

            Intent intent = new Intent(mContext, PlayerActivity.class);
            Gson gson = new Gson();

            intent.putExtra(Contants.CURRENT_SONG, currentSong);
            intent.putExtra(Contants.IS_SONG_PLAYING, true);//true mean the song is playing
            intent.putExtra(Contants.SONG_WHOLE_DATA, gson.toJson(mDataModel));

            songPicked(currentSong); //start playing the song from the service
            mContext.startActivity(intent); //start the new activity that will display the media player
        }
    }

}
