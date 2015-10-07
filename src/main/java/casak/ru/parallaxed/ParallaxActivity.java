package casak.ru.parallaxed;

import      android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ParallaxActivity extends Activity{

    private static final String TAG = "PARALLAX_ACTIVITY";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parallax);

        imageView = (ImageView) this.findViewById(R.id.image);
        if(contentText==null)
           new ProgressTask().execute();
    }



    String contentText = null;
    class ProgressTask extends AsyncTask <Bitmap, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Bitmap... path) {

            StringBuilder content = null;
            BufferedReader reader = null;
            String imageURL = null;
            Bitmap myBitmap = null;
            try {
                URL url = new URL("http://ellotv.bigdig.com.ua/api/home/video");
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setReadTimeout(10000);
                c.connect();
                reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                content = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    content.append(line + "\n");
                }

                try {
                    JSONObject dataJsonObj = new JSONObject(content.toString());
                    JSONObject data = dataJsonObj.getJSONObject("data");
                    JSONArray items = data.getJSONArray("items");

                    JSONObject secondFriend = items.getJSONObject(0);
                    imageURL = secondFriend.getString("picture");
                    Log.d(TAG, "Picture URL : " + imageURL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    url = new URL(imageURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    myBitmap = BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    Log.d(TAG, "IOException on creating bitmap");
                }

            }
            catch (IOException ex) {
                content.append(ex.getMessage());
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException e) {
                        Log.d(TAG, "IOEXception on closing");
                    }
                }
            }
            return myBitmap;
        }
        @Override
        protected void onProgressUpdate(Void... items) {
        }
        @Override
        protected void onPostExecute(Bitmap image) {
            imageView.setImageBitmap(image);
        }


}

}
