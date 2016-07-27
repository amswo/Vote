package com.example.t3ad20.vote;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.t3ad20.vote.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("명화 선호도 투표");

        Button btnGoBoard = (Button) findViewById(R.id.btnGoBoard);
        btnGoBoard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        com.example.t3ad20.vote.BoardActivity.class);
                startActivity(intent);
            }
        });

        // 9개의 이미지 버튼 객체배열
        ImageView image[] = new ImageView[9];
        // 9개의 이미지버튼 ID 배열
        Integer imageId[] = { R.id.iv1, R.id.iv2, R.id.iv3, R.id.iv4, R.id.iv5,
                R.id.iv6, R.id.iv7, R.id.iv8, R.id.iv9 };

        final String imgName[] = { "독서하는 소녀", "꽃장식 모자 소녀", "부채를 든 소녀",
                "이레느깡 단 베르양", "잠자는 소녀", "테라스의 두 자매", "피아노 레슨", "피아노 앞의 소녀들",
                "해변에서" };

        for (int i = 0; i < imageId.length; i++) {
            final int index; // 주의! 꼭 필요함..
            index = i;
            image[index] = (ImageView) findViewById(imageId[index]);
            image[index].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // 투표수 증가.
                    JSONObject obj = new JSONObject();

                    try {
                        obj.put("url", "http://52.78.63.16:3000/insertData");
                        obj.put("imageName", imgName[index]);
                    }
                    catch(Exception e) {
                        Log.d("###", e.toString());
                    }

                    new HTTPAsyncTask().execute(obj);
                    Toast.makeText(getApplicationContext(),
                            imgName[index] + "득표",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button btnFinish = (Button) findViewById(R.id.btnResult);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        ResultActivity.class);
                startActivity(intent);
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
//                JSONObject tmp = new JSONObject(obj.get("result"));
                Log.d("###", (String) obj.get("result"));
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
