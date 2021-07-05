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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.speech.tts.TextToSpeech;

import java.util.*;

public class Main extends AppCompatActivity {
    //Static
    private final String TAG = "Main" ;
    private final String url = "https://fr.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&exchars=300&redirects=1&formatversion=2&origin=*&titles=";
    // Views
    public TextToSpeech t1,t2;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private ListView mListView;
    private FloatingActionButton mButtonSend;
    private EditText mEditTextMessage;
    private ChatMessageAdapter mAdapter;
    //Evaliation Mode
    private boolean evalmode = false ;
    private Evaluation eval = new Evaluation();
    private int evalQest = 0;

    // Permission
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String[] permissions = new String[] {
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO
    };
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
                mimicOtherMessage("Pour avoir de l'aide, merci de saisir le mot aide\nPour passer une évaluation, merci de saisir le mot eval ou bien test");

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
                    String response = "";
                    if(evalmode ==false ) {
                        if (message.toLowerCase().matches("aide")) {
                            response = "Pour avoir de l'aide, merci de saisir le mot aide\n" +
                                    "Pour passer une évaluation, merci de saisir le mot eval ou bien test\n"
                                    +"Cette application est développer par Berrich";
                        } else if (message.toLowerCase().matches("eval|test")) {
                            response = "bonjour utilisatuer\n";
                            evalmode = true;
                            eval.start(1);
                            response += eval.element.elementAt(0).getQest();
                            evalQest = 1;
                        } else if (!message.isEmpty()) {
                            try {
                                response = new Data().execute(url + message.replace(" ", "%20")).get();
                                response = response.compareTo("") == 0 ? message : response;
                                //Toast.makeText(getBaseContext(), response, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else{
                        if(evalQest != 3) {
                            switch (Integer.parseInt(message)) {
                                case 1:
                                    eval.score++;
                                case 2:
                                case 3:
                                    evalQest++;
                                    if(evalQest != 3)
                                        response = eval.element.elementAt(evalQest-1).getQest();
                                    break;
                                default:
                                    response = "choix entre 1 et 3 !!";
                                    response += eval.element.elementAt(evalQest-1).getQest();
                            }
                        }
                        else{
                            if(Integer.parseInt(message) == 1) eval.score++;
                            response = "Votre score est : "+ eval.score ;
                            eval.score = 0;
                            evalmode = false;
                        }
                    }
                    sendMessage(message);
                    mimicOtherMessage(response);
                    mEditTextMessage.setText("");
                    mListView.setSelection(mAdapter.getCount() - 1);

                    //Toast.makeText(getApplicationContext(),"Res : "+response,Toast.LENGTH_LONG).show();
                    if(!evalmode)
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
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //speech start
    public void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRANCE);
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
        if(t1.isSpeaking()){
            t1.stop();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(t1.isSpeaking())
            t1.stop();
        else
            super.onBackPressed();
    }
}
