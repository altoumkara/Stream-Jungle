package example.com.streamjungle.rest.service;

import java.util.List;

import example.com.streamjungle.rest.model.SoundCloudModel;
import example.com.streamjungle.utility.Contants;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by alamatounkara on 9/6/15.
 */
public interface SoundClientService {
    /**
     * we are using this url: https://api.soundcloud.com/tracks?client_id=1233be8128198789963a226f93916311
     * refer to this link for query parameter allowed in the soundClound API
     */
    @GET("/tracks?client_id="+ Contants.CLIENT_ID)
    /**
     * this @Query might give us something like this:
     * https://api.soundcloud.com/tracks?client_id=1233be8128198789963a226f93916311&created_at[from]="10/11/2014"
     * the string "10/11/2014" will be @param date
     */
    public void getTrack(@Query("created_at[from]") String date, Callback<List<SoundCloudModel>> response);
}
