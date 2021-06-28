package ma.algobot;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Data extends AsyncTask<String, Void, String> {

    protected String doInBackground(String... url) {
        try {
            // Create a new HTTP Client
            DefaultHttpClient defaultClient = new DefaultHttpClient();
            // Setup the get request
            HttpGet httpGetRequest = new HttpGet(url[0]);

            // Execute the request in the client
            HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
            // Grab the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String json = reader.readLine();

            // Instantiate a JSON object from the request response
            return new JSONObject(json).getJSONObject("query").getJSONArray("pages").getJSONObject(0).getString("extract").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}