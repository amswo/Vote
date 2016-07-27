package com.example.t3ad20.vote;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by T3AD20 on 2016-07-27.
 */
public class BoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        final EditText etWrite = (EditText) findViewById(R.id.etWrite);
        final EditText etNickName = (EditText) findViewById(R.id.etNickName);

        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("url", "http://52.78.63.16:3000/sendMessage");
                    if (etNickName.getText().length() <= 0) {
                        TextView tmpTv = new TextView(BoardActivity.this);
                        tmpTv.setText("you need a nickname!!");
                        tmpTv.setBackgroundColor(Color.RED);

                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(tmpTv);
                        toast.show();

                    }
                    else {
                        obj.put("nickname", etNickName.getText());
                        obj.put("message", etWrite.getText());
                    }
                }
                catch(Exception e) {
                    Log.d("###", e.toString());
                }

                new HTTPAsyncTask().execute(obj);

            }
        });
    }
    private class HTTPAsyncTask extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(JSONObject... obj) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return HttpPost(obj[0]);
            }
            catch(Exception e) {
                Log.d("###", e.toString());
            }
            return obj[0];
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject obj) {
            try {
                JSONObject tmpObj = new JSONObject((String) obj.get("result"));
                JSONArray tmpArr = tmpObj.getJSONArray("array");
                Log.d("### result", tmpArr.toString());

                LinearLayout llMessages = (LinearLayout) findViewById(R.id.llMessages);
                llMessages.removeAllViews();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView tvMessage = null;
                for (int i = 0; i < tmpArr.length(); i++) {
                    tvMessage = new TextView(BoardActivity.this);
                    tvMessage.setLayoutParams(params);
                    String tmpString = "[" + tmpArr.getJSONObject(i).get("nickname").toString() + "]: "
                            + tmpArr.getJSONObject(i).get("message").toString();
                    tvMessage.setText(tmpString);
//                    tvMessage.setText("haha");
                    llMessages.addView(tvMessage);
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

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
