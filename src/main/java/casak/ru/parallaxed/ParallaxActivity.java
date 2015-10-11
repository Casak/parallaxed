package casak.ru.parallaxed;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ParallaxActivity extends Activity{

    private static final String TAG = "PARALLAX_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);

        setContentView(R.layout.activity_parallax);

        new ProgressTask().execute("http://ellotv.bigdig.com.ua/api/home/video");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }


    class ProgressTask extends AsyncTask <String, Void, Frame[]> {
        private StringBuilder content;
        private BufferedReader reader;
        private URL url;
        private HttpURLConnection connection;
        private Frame[] frames;

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
                String line;
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
                        Log.d(TAG, "IOEXception on reader.close()");
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
            ParallaxListView parallaxListView = (ParallaxListView) findViewById(R.id.parallaxListView);
            parallaxListView.setDividerHeight(2);
            parallaxListView.setAdapter(new LazyAdapter(getApplicationContext(), frames));
        }


        private Bitmap getImage(String imageURL){
            try {
                URL url = new URL(imageURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                Log.d(TAG, "IOException on creating bitmap");
                return null;
            }
        }
    }
}