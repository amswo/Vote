package com.example.t3ad20.vote;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.t3ad20.vote.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by T3AD20 on 2016-07-26.
 */
public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        setTitle("투표 결과");

        JSONObject obj = new JSONObject();
        try {
            obj.put("url", "http://52.78.63.16:3000/findData");
        }
        catch(Exception e) {
            Log.d("###", e.toString());
        }

        new HTTPAsyncTask().execute(obj);

        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();

            }
        });
    }

    private class HTTPAsyncTask extends AsyncTask<JSONObject, Void, JSONObject> {

        protected JSONObject doInBackground(JSONObject... obj) {
            Log.d("###", "RUN AsyncTask");
            // params comes from the execute() call: params[0] is the url.
            try {
                return HttpPost(obj[0]);
            }
            catch(Exception e) {
                Log.d("###", e.toString());
                return obj[0];
            }
        }
        // onPostExecute displays the results of the AsyncTask.

        protected void onPostExecute(JSONObject obj) {
            try {
                JSONObject tmpObj = new JSONObject((String) obj.get("result"));
                JSONArray tmpArr = tmpObj.getJSONArray("array");
                Log.d("### result", tmpArr.toString());

                //                // 9개의 TextView, RatingBar 객체배열
                TextView tv[] = new TextView[tmpArr.length()];
                TextView tv_1[] = new TextView[tmpArr.length()];
                //                // 9개의 TextView, RatingBar ID 배열
                Integer tvID[] = {R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5,
                        R.id.tv6, R.id.tv7, R.id.tv8, R.id.tv9};
                Integer tvID_1[] = {R.id.tv1_1, R.id.tv2_1, R.id.tv3_1, R.id.tv4_1, R.id.tv5_1,
                        R.id.tv6_1, R.id.tv7_1, R.id.tv8_1, R.id.tv9_1};

                for (int i = 0; i < tmpArr.length(); i++) {
                    System.out.println(tmpArr.getJSONObject(i).get("imageName").toString());
                    System.out.println(tmpArr.getJSONObject(i).get("count").toString());

                    tv[i] = (TextView) findViewById(tvID[i]);
                    tv[i].setText(tmpArr.getJSONObject(i).get("imageName").toString());
                    tv_1[i] = (TextView) findViewById(tvID_1[i]);
                    tv_1[i].setText(tmpArr.getJSONObject(i).get("count").toString());

                }
            }
            catch(Exception e) {
                Log.d("###", e.toString());
            }
        }
    }

    private JSONObject HttpPost(JSONObject obj) throws IOException {
        try {
            InputStream inputStream = null;
            URL url = new URL((String)obj.get("url"));

            // create HttpURLConnection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // make PUT request to the given URL
            conn.setRequestMethod("POST");
            conn.connect();

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(obj.toString());
            out.close();

            // receive response as inputStream
            inputStream = conn.getInputStream();

            // convert inputstream to string
            if(inputStream != null)
                obj.put("result", convertInputStreamToString(inputStream));
            else
                obj.put("result", "Did not work!");

            return obj;
        }
        catch(Exception e) {
            Log.d("###", e.toString());
        }
        return obj;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
