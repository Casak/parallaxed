package casak.ru.parallaxed;

import android.support.v4.app.FragmentActivity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
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

public class ParallaxActivity extends FragmentActivity{

    private static final String TAG = "PARALLAX_ACTIVITY";

    private ListView list;
    private LazyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_parallax);

        new ProgressTask().execute("http://casak.ru/api.json");
    }


    class ProgressTask extends AsyncTask <String, Void, Frame[]> {
        private StringBuilder content = null;
        private BufferedReader reader = null;
        private String imageURL = null;
        private Bitmap myBitmap = null;
        private URL url = null;
        private HttpURLConnection connection = null;
        private Frame[] frames = null;

        @Override
        protected Frame[] doInBackground(String... path) {
            try {
                url = new URL(path[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                connection.connect();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                content = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    content.append(line + "\n");
                }

                try {
                    JSONObject dataJsonObj = new JSONObject(content.toString());
                    JSONObject data = dataJsonObj.getJSONObject("data");
                    JSONArray items = data.getJSONArray("items");

                    frames = new Frame[items.length()];
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);

                        StringBuilder artistName = new StringBuilder();
                        JSONArray artists = item.getJSONArray("artists");
                        for (int j = 0; j < artists.length(); j++) {
                            JSONObject artist = artists.getJSONObject(j);
                            if (artists.length() == 1) artistName.append(artist.getString("name"));
                            else artistName.append(artist.getString("name") + ", ");
                        }

                        frames[i] = new Frame(getImage(item.getString("picture")),
                                item.getString("title"), artistName.toString(),
                                item.getInt("view_count"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
            return frames;
        }

        @Override
        protected void onProgressUpdate(Void... items) {
        }

        @Override
        protected void onPostExecute(Frame... frames) {
            for(int i=0; i<frames.length; i++) Log.d(TAG, frames[i].toString());


            ParallaxListView parallaxListView = (ParallaxListView) findViewById(R.id.parallaxListView);
            parallaxListView.setDividerHeight(2);
            parallaxListView.setAdapter(new LazyAdapter(getApplicationContext(), frames));

            /*list = (ListView)findViewById(R.id.list);

            adapter = new LazyAdapter(getApplicationContext(), frames);
            list.setAdapter(adapter);*/
        }


        private Bitmap getImage(String imageURL){
            try {
                URL url = new URL(imageURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return myBitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                Log.d(TAG, "IOException on creating bitmap");
                return null;
            }
        }
    }
}