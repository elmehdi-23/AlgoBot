package ma.algobot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.speech.tts.TextToSpeech;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class Main extends AppCompatActivity {

    private Button start,about,exit;

    public TextToSpeech t1,t2;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ListView mListView;
    private FloatingActionButton mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;
    private ChatMessageAdapter mAdapter;

    String[] permissions = new String[] {
            Manifest.permission.INTERNET,

    };
    private final String TAG = "Main" ;
    private final String url = "https://fr.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&exchars=175&redirects=1&formatversion=2&origin=*&titles=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        new CountDownTimer((int) ((Math.random()* 2000)+1000),1000){
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG,"millisUntilFinished :" + millisUntilFinished);
            }
            @Override
            public void onFinish() {
                Log.d(TAG,"Main Activity" );
                setContentView(R.layout.activity_main);
                checkPermission();
                mListView = (ListView) findViewById(R.id.listView);
                mButtonSend = (FloatingActionButton) findViewById(R.id.btn_send);
                mEditTextMessage = (EditText) findViewById(R.id.et_message);

                //mImageView = (ImageView) findViewById(R.id.iv_image);
                mAdapter = new ChatMessageAdapter(getApplicationContext(), new ArrayList<ChatMessage>());
                mListView.setAdapter(mAdapter);

                txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
                btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

                t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.FRANCE);
                        }
                    }
                });


                btnSpeak.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        promptSpeechInput();
                    }
                });


                mButtonSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = mEditTextMessage.getText().toString();
                        //t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                        //bot
                        String response = "";
                        try {
                            response = new Data().execute(url +  message).get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sendMessage(message);
                        mimicOtherMessage(response);
                        mEditTextMessage.setText("");
                        mListView.setSelection(mAdapter.getCount() - 1);

                        //Toast.makeText(getApplicationContext(),"Res : "+response,Toast.LENGTH_LONG).show();
                        t1.speak(response, TextToSpeech.QUEUE_FLUSH, null);


                        //Toast.makeText(getApplicationContext(),"list : "+mListView.getCount(),Toast.LENGTH_LONG).show();
                    }
                });

            }
        }.start();
    }
    private boolean checkPermission() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }

    //speech start
    public void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try
        {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        }
        catch (ActivityNotFoundException a)
        {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }


    }
    public void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }
    public void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }
    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);

        mimicOtherMessage();
    }
    //Request and response of user and the bot
    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }
    public void onPause() {
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }
}
