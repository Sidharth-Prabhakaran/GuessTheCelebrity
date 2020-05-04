package com.myappcompany.sid.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class MainActivity extends AppCompatActivity {

    ArrayList<String> characters = new ArrayList<String>();
     ArrayList<String> characterImages = new ArrayList<String>();
     int charChose = 0;
     ImageView imageView;
     String[] answers = new String[4];
     int locationOfCorrectAnswer = 0;
     Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void optionClicked (View view){
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(),"Correct",Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getApplicationContext(),"Wrong. It was " + characters.get(charChose),Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }

    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{

         @Override
         protected Bitmap doInBackground(String... urls) {
             try{
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                return BitmapFactory.decodeStream(in);
             }catch (Exception e){
                e.printStackTrace();
                return null;
             }
         }
     }

public void newQuestion(){
    ImageDownloader imageTask = new ImageDownloader();
    Random rand = new Random();
    charChose = rand.nextInt(characterImages.size());
    Bitmap myImage=null;
    try {
        myImage = imageTask.execute(characterImages.get(charChose)).get();

    }catch(Exception e){
        e.printStackTrace();
    }

    imageView.setImageBitmap(myImage);
    locationOfCorrectAnswer = rand.nextInt(4);
    int incorrectLocation = 0;

    for (int i=0;i<4;i++){
        if (i == locationOfCorrectAnswer){
            answers[i] = characters.get(charChose);
        }else{
            incorrectLocation = rand.nextInt(characterImages.size());
            while(incorrectLocation == charChose) {
                incorrectLocation = rand.nextInt(characterImages.size());
            }
            answers[i] = characters.get(incorrectLocation);
        }
    }

    button0.setText(answers[0]);
    button1.setText(answers[1]);
    button2.setText(answers[2]);
    button3.setText(answers[3]);
}
     public class DownloadTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = null;
            URL url;
            HttpURLConnection urlConnection;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return  result;

            } catch (Exception e) {
                e.printStackTrace();
                return " Failed" + e;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.questionImageView);

        String pageCode = null;
        String newPageCode = "";
        DownloadTask task = new DownloadTask();

        button0 = findViewById(R.id.optionButton1);
        button1 = findViewById(R.id.optionButton2);
        button2 = findViewById(R.id.optionButton3);
        button3 = findViewById(R.id.optionButton4);

        try {
            pageCode = task.execute("Https://besttoppers.com/top-10-cartoon-characters/#content").get();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.i("Failed to read page","check the logs");
        }

        String[] firsResult = pageCode.split("</div><!-- .entry-content");
        String[] finalResult = firsResult[0].split("entry-content\">");


        Pattern p = Pattern.compile("<h2>\\d{1,2}.(.*?)</h2>");
        Matcher m = p.matcher(finalResult[1]);
        while (m.find()){
            characters.add(m.group(1));
        }

        p = Pattern.compile("src=\"(.*?)\" alt");
        m = p.matcher(finalResult[1]);
        while (m.find()){
            String x = m.group(1);
            characterImages.add(x.substring(0,4)+"s"+x.substring(4,x.length()));
        }

       newQuestion();

    }

}

