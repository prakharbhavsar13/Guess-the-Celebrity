package com.example.guesscelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.in;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int choosenceleb=0;
    String[] answer = new String[4];
    int locationofanswer = 0;

    ImageView imageView;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    public void celebChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationofanswer))){
            Toast.makeText(getApplicationContext(),"correct",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"wrong! it was "+ celebNames.get(choosenceleb),Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }

    public class ImageDownloader extends AsyncTask <String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            }
             catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

        public class DownloadTask extends AsyncTask <String,Void,String>{

            @Override
            protected String doInBackground(String... urls) {
                String result = "";
                URL url;
                HttpURLConnection urlConnection = null;
                try{

                    url = new URL(urls[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);

                    int data = reader.read();
                    while (data != -1){
                        char current = (char)data;
                        result+= current;
                        data = reader.read();
                    }

                    return result;
                }
                catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
        }

        public void newQuestion() {
            try {
                Random rand = new Random();
                choosenceleb = rand.nextInt(celebURLs.size());

                ImageDownloader imageTask = new ImageDownloader();
                Bitmap celebImage = imageTask.execute(celebURLs.get(choosenceleb)).get();
                imageView.setImageBitmap(celebImage);

                locationofanswer = rand.nextInt(4);

                int incorrectlocations;

                for (int i = 0; i < 4; i++) {
                    if (i == locationofanswer) {
                        answer[i] = celebNames.get(choosenceleb);

                    } else {
                        incorrectlocations = rand.nextInt(celebURLs.size());
                        while (incorrectlocations == choosenceleb) {
                            incorrectlocations = rand.nextInt(celebURLs.size());
                        }
                        answer[i] = celebNames.get(incorrectlocations);

                    }
                }

                button1.setText(answer[0]);
                button2.setText(answer[1]);
                button3.setText(answer[2]);
                button4.setText(answer[3]);


        }catch(Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView =findViewById(R.id.imageView);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        DownloadTask task =  new DownloadTask();
        String result = null;
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"listedArticles\">");
            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()) {
               celebURLs.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while (m.find()) {
                celebNames.add((m.group(1)));
            }


        newQuestion();
        }
        catch (Exception e ){
            e.printStackTrace();
        }
    }
}
