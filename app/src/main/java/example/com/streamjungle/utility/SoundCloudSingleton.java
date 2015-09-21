package example.com.streamjungle.utility;

import example.com.streamjungle.rest.service.SoundClientService;
import retrofit.RestAdapter;

/**
 * We are to be making several calls to the remote server. Each call to the Remote server will create
 * the RestAdapter and the SoundClientService, which is an expensive operation.
 * In order to avoid that, We use this singleton. So the RestAdapter and the SoundClientService are
 * only created once for our whole application(cuz they are static and final),and used whenever needed
 * Created by alamatounkara on 9/7/15.
 */
public class SoundCloudSingleton {

    //private static final SoundCloudSingleton mySingleton = new SoundCloudSingleton();
    /**
     * RestAdapter is responsible for transform an API Interface into an Object which actually
     * makes network request
     */
    private static final RestAdapter mRestAdapter = new RestAdapter.Builder()
            .setEndpoint(Contants.API_END_POINT)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();
    /**
     * use the RestAdapter to create a real implementation of our service interface(SoundClientService)
     */
    private static final SoundClientService mSoundClientService = mRestAdapter.create(SoundClientService.class);

//    private SoundCloudSingleton() {
//    }
//
//    public static SoundCloudSingleton getMySingleton() {
//        return mySingleton;
//    }

    public static SoundClientService getSoundClientService() {
        return mSoundClientService;
    }
}
