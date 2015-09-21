package example.com.streamjungle.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import example.com.streamjungle.rest.model.SoundCloudModel;

/**
 * I use Gson to serialize the song data list in my RecyclerViewAdapter. I serialize that data so it
 * can be sent via an intent. But the returning type of the deserialize object is not a valid
 * json, therefore i need to create a custom deserializer that deserialize that object
 * our api:
 * https://api.soundcloud.com/tracks?client_id=1233be8128198789963a226f93916311&created_at[from]=2015-09-06%2006:41:53
 * Created by alamatounkara on 9/14/15.
 */
public class CustomDeserializer implements JsonDeserializer<List<SoundCloudModel>> {


    @Override
    public List<SoundCloudModel> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        //creating a list of SoundCloudModel object that will contain the deserialize object
        List<SoundCloudModel> listOfSongs = new ArrayList<SoundCloudModel>();


        //convert this JsonElement 'json' into a jsonArray cuz our serialize file will be a list
        JsonArray jsonArray = json.getAsJsonArray();

        for (int i = 0; i < jsonArray.size(); i++) {
            //getting individual jsonObject from that jsonArray
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            //creating a SoundCloudModel object for each jsonObject
            SoundCloudModel soundCloudModel = new SoundCloudModel();
            //not all song have 'id', therefore we need to check if it does exist before
            //calling 'getAsString()' method in order to avoid nullPointer exception
            if (jsonObject.get("id") != null) {
                soundCloudModel.setID(jsonObject.get("id").getAsString());
            }
            //not all song have 'stream_url', therefore we need to check if it does exist before
            //calling 'getAsString()' method in order to avoid nullPointer exception
            if (jsonObject.get("stream_url") != null) {
                soundCloudModel.setStreamUrl(jsonObject.get("stream_url").getAsString());
            }
            //not all song have 'title', therefore we need to check if it does exist before
            //calling 'getAsString()' method in order to avoid nullPointer exception
            if (jsonObject.get("title") != null) {
                soundCloudModel.setTitle(jsonObject.get("title").getAsString());
            }
            //not all song have 'genre', therefore we need to check if it does exist before
            //calling 'getAsString()' method in order to avoid nullPointer exception
            if (jsonObject.get("genre") != null) {
                soundCloudModel.setGenre(jsonObject.get("genre").getAsString());
            }
            //not all song have 'artwork_url', therefore we need to check if it does exist before
            //calling 'getAsString()' method in order to avoid nullPointer exception
            if (jsonObject.get("artwork_url") != null) {
                soundCloudModel.setArtworkUrl(jsonObject.get("artwork_url").getAsString());
            }

            //adding the soundCloudModel object to the list
            listOfSongs.add(soundCloudModel);
        }

        return listOfSongs;
    }
}
