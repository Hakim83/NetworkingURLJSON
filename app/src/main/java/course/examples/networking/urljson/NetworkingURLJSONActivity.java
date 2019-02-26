package course.examples.networking.urljson;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class NetworkingURLJSONActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get your own user name at http://www.geonames.org/login
        final String USER_NAME = "aporter";
        final String URL = "http://api.geonames.org/earthquakesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username="
                + USER_NAME;
        new GetResponseTask().execute(URL);
    }

    public class GetResponseTask extends AsyncTask<String, Void, List<String>> {

        private static final String LONGITUDE_TAG = "lng";
        private static final String LATITUDE_TAG = "lat";
        private static final String MAGNITUDE_TAG = "magnitude";
        private static final String EARTHQUAKE_TAG = "earthquakes";

        @Override
        protected List<String> doInBackground(String... strings) {
            //this part only return raw response from url!

            try {
                String raw = GetHttpResponse(strings[0]);
                if (raw != null) {
                    return ParseJSON(raw);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (result == null) {
                Toast.makeText(getApplicationContext(), "No Connection!", Toast.LENGTH_SHORT).show();
            } else {
                setListAdapter(new ArrayAdapter<String>(
                        NetworkingURLJSONActivity.this,
                        R.layout.list_item, result));
            }

        }

        // convert inputstream to String
        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }

        protected String GetHttpResponse(String urlText) {
            String raw = null;

            try {
                URL url = new URL(urlText);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                raw = convertInputStreamToString(inputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return raw;
        }

        // parse raw JSON data into strings list
        private List<String> ParseJSON(String raw) throws JSONException, ParseException {
            List<String> result = new ArrayList<>();
            JSONObject responseObject = new JSONObject(raw);
            // Extract value of "earthquakes" key -- a List
            JSONArray earthquakes = responseObject.getJSONArray(EARTHQUAKE_TAG);

            // Iterate over earthquakes list
            for (int idx = 0; idx < earthquakes.length(); idx++) {

                // Get single earthquake data - a Map
                JSONObject earthquake = (JSONObject) earthquakes.get(idx);

                // Summarize earthquake data as a string and add it to
                // result
                result.add(MAGNITUDE_TAG + ":"
                        + earthquake.get(MAGNITUDE_TAG) + ","
                        + LATITUDE_TAG + ":"
                        + earthquake.getString(LATITUDE_TAG) + ","
                        + LONGITUDE_TAG + ":"
                        + earthquake.get(LONGITUDE_TAG));

            }
            return result;
        }
    }
}