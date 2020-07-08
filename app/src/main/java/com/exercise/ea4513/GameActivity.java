package com.exercise.ea4513;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    //    final int numberOfQuestions = Constant.Values.NUMBER_OF_QUESTIONS;
    int numberOfQuestions;
    final int NUMBER_OF_ANSWERS = Constant.Values.NUMBER_OF_ANSWERS;

    int currentQuestionIndex = -1; // Which question the user is currently at. Should be an integer between -1 to NUMBER_OF_QUESTIONS-1 (zero based index). -1 means that the questions have not been loaded yet.
    int correctAnswers = 0; //The number of correct answers the user got.
    Response[] responses;

    Boolean switchSound;

    // Background colors that are used in the view during different situations
    int NORMAL_COLOR = 0xFF726B6B;
    int CORRECT_COLOR = 0xFF479D32;
    int INCORRECT_COLOR = 0xFFA72525;

    DownloadTask task;

    Button btnReady;
    //    Button btnSkip;
    Button btnNext;
//    ImageButton btnPause;

    SoundPool soundPool;
    UITimer uiTimer;
    TextView tvTimer;
    TextView tvQuestionNumber;
    TextView tvQuestionContent;
    ImageView ivLoading;
    View actGame;
    RadioGroup rgAnswers;
    long totalTime;

    SQLiteDatabase db;
    String DB_NAME = Constant.Values.DB_NAME;
    String qs;
    Cursor cursor;

//    final MediaPlayer correctSound = MediaPlayer.create(GameActivity.this, R.raw.correct);
//    final MediaPlayer incorrectSound = MediaPlayer.create(GameActivity.this, R.raw.incorrect);

    int correctSound, incorrectSound;

    GameQuestion[] gameQuestions;

    public class Response {
        long time_spent;
        boolean isCorrect;

        public Response(long t, boolean c) {
            time_spent = t;
            isCorrect = c;
        }

    }

    public void setTvQuestionNumber() {
        String txt = String.format("%d of %d", currentQuestionIndex + 1, numberOfQuestions);
        tvQuestionNumber.setText(txt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Declare the UI elements
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestionContent = findViewById(R.id.tvQuestionContent);
        actGame = findViewById(R.id.actGame);


        btnReady = findViewById(R.id.btnReady);
        btnNext = findViewById(R.id.btnNext);
//        btnSkip = findViewById(R.id.btnSkip);
//        btnPause = findViewById(R.id.btnPause);
        ivLoading = findViewById(R.id.ivLoading);
        rgAnswers = findViewById(R.id.rbAnswers);

        //Setup the UI elements
        btnReady.setVisibility(View.INVISIBLE);
//        btnSkip.setVisibility(View.INVISIBLE);
        rgAnswers.setVisibility(View.INVISIBLE);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(audioAttributes)
                .build();

        correctSound = soundPool.load(this, R.raw.correct, 1);
        incorrectSound = soundPool.load(this, R.raw.incorrect, 1);


        //load preferences
        SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
        numberOfQuestions = settings.getInt("NUMBER_OF_QUESTIONS", 5);
        switchSound = settings.getBoolean("hasSound", false);
        responses = new Response[numberOfQuestions];
        gameQuestions = new GameQuestion[numberOfQuestions];

        //Setup logic
        setTvQuestionNumber();
        uiTimer = new UITimer();

        // generate radio buttons
        for (int i = 0; i < NUMBER_OF_ANSWERS; i++){
            RadioButton rbAnswer = new RadioButton(this);
            rbAnswer.setText(i+"" );
            rbAnswer.setId(View.generateViewId());
            rbAnswer.setTextSize(40);
            rbAnswer.setTextColor(Color.WHITE);
            rbAnswer.setOnClickListener(radioListener);
            rbAnswer.setWidth(300);
            rbAnswer.setPadding(0,10,0,0);
            rgAnswers.addView(rbAnswer);
        }

        try{
            task = new DownloadTask();
            String url_string = "https://ajtdbwbzhh.execute-api.us-east-1.amazonaws.com/default/201920ITP4501Assignment";
            task.execute(url_string);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask(){
            @Override
            public void run() {
                uiTimer.update();
                try {
                    tvTimer.setText(uiTimer.toString());
                } catch (Exception e){
                    Log.e("Error", e.getMessage());
                }
            };
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000);

        db = SQLiteDatabase.openDatabase(Constant.Values.DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        qs = "DELETE FROM QuestionsLog";
        cursor = db.rawQuery(qs, null);

    }

    View.OnClickListener radioListener = new View.OnClickListener(){
        public void onClick(View v) {
            onUserAnswerQuestion(v);
        }
    };




    public void setNextQuestion(View v){
        /*
        Reset the background color to #726B6B.
        Reset the timer.
        Enable the radio buttons.
        Increment the number for current question.
        If current question >= NUMBER_OF_QUESTIONS, the game ends.
        Otherwise, load the new question and shuffle the answers.
        Show btnNext, such that the user can move to the next question.
         */

//        btnSkip.setVisibility(View.VISIBLE);
        btnReady.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.INVISIBLE);
        rgAnswers.setVisibility(View.VISIBLE);
        rgAnswers.clearCheck();
        for (int i = 0; i < rgAnswers.getChildCount(); i++) {
            rgAnswers.getChildAt(i).setEnabled(true);
        }
//        rgAnswers.setClickable(true);

        currentQuestionIndex++;

        if (currentQuestionIndex < numberOfQuestions) {
            setTvQuestionNumber();
            tvQuestionContent.setText(gameQuestions[currentQuestionIndex].content);
            actGame.setBackgroundColor(NORMAL_COLOR);

            for (int i = 0; i < rgAnswers.getChildCount(); i++){
                RadioButton radioAnswer = (RadioButton) rgAnswers.getChildAt(i);
                radioAnswer.setText(gameQuestions[currentQuestionIndex].answers[i].value + "");
            }

            uiTimer.setPaused(false);
        } else {
            //Game ends when all questions are asked
            Intent data = new Intent(this, EndGameActivity.class);
            data.putExtra("correctAnswers", correctAnswers);
            data.putExtra("NUMBER_OF_QUESTIONS", numberOfQuestions);
            long elapsed =  uiTimer.getTime();

            try {

                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

                ContentValues contentValues = new ContentValues();
                contentValues.put("testDate", date);
                contentValues.put("time", time);
                contentValues.put("duration", uiTimer.getTime());
                contentValues.put("correctCount", correctAnswers);
                contentValues.put("numberOfQuestions", numberOfQuestions);
                contentValues.put("numberOfAnswers", NUMBER_OF_ANSWERS);

                long id = db.insert("TestsLog", null, contentValues);

                Log.d("MyDB", "Inserted into row " + id);
            } catch(SQLiteException e){
                Log.d("MyDB", e.getMessage() );
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            data.putExtra("timeSpent", uiTimer.time );
            finish();
            startActivity (data);
        }
    }

    public void onUserAnswerQuestion(View v){
        /*
        When user answers a question, pause the timer.
        Disable the radio buttons.
        Check if the answer is correct.
        If the answer is correct, set the background color of the view to green.
        If the answer is incorrect, set the background color of the view to red.
        Prompt user to click anywhere to proceed to the next question.
         */
//        tvTimer.stop();
        uiTimer.setPaused(true);

//        btnSkip.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        for (int i = 0; i < rgAnswers.getChildCount(); i++) {
            rgAnswers.getChildAt(i).setEnabled(false);
        }

        RadioButton rbAnswer = (RadioButton)v;
        boolean isCorrect = rbAnswer.getText().toString().equals(gameQuestions[currentQuestionIndex].correctAnswer+"");
        responses[currentQuestionIndex] = new Response(0, isCorrect); //todo: Remove time

        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put("Question", gameQuestions[currentQuestionIndex].content);
            contentValues.put("yourAnswer", rbAnswer.getText().toString());
            contentValues.put("isCorrect", isCorrect);
            long id = db.insert("QuestionsLog", null, contentValues);
            Log.d("MyDB", "Inserted into " + id);
        }catch (SQLiteException e){
            Log.d("MyDB", e.getMessage());
        }

        if (isCorrect) {
            actGame.setBackgroundColor(CORRECT_COLOR);
            correctAnswers++;
            if (switchSound) {
                soundPool.play(correctSound, 1, 1, 0, 0, 1);

            }
        } else {
            actGame.setBackgroundColor(INCORRECT_COLOR);
            if (switchSound) {
                soundPool.play(incorrectSound, 1, 1, 0, 0, 1);
            }
        }

        try{

        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... values){
            try {
                Log.d("JSON", "start");
                InputStream inputStream = null;
                URL url = null;
                String result = "";

                url = new URL(values[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5000);
                con.setRequestMethod("GET");
                con.connect();

                inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    result = line;
                }

                inputStream.close();

//                Thread.sleep(2000);
                return result;
            } catch (Exception e){
                // In case if the connection fails, use local fetch
                String json_response = "{\"questions\": [{\"question\": \"11, 13, 17, 19, 23, 29, 31, 37, 41, ? \", \"answer\": 43}, {\"question\": \"11, 10, ?, 100, 1001, 1000, 10001\", \"answer\": 101}, {\"question\": \"20, 19, 17, ?, 10, 5\", \"answer\": 14}, {\"question\": \"9, 12, 11, 14, 13, ?, 15\", \"answer\": 16}, {\"question\": \"4, 6, 12, 14, 28, 30, ?\", \"answer\": 60}, {\"question\": \"36, 34, 30, 28, 24, ?\", \"answer\": 22}, {\"question\": \"1, 4, 27, 16, ?, 36, 343\", \"answer\": 125}, {\"question\": \"6, 11, 21, 36, 56, ? \", \"answer\": 81}, {\"question\": \"2, 3, 5, 7, 11, ?, 17\", \"answer\": 13}, {\"question\": \"2, 7, 14, 23, ?, 47\", \"answer\": 34}]}";
                onPostExecute(json_response);
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result){
            /*
            AJAX loading is completed.
            Prompt user to click start for the first question.
            */
            try {
                // load json resultR
                JSONObject jsonObject = new JSONObject(result);
                JSONArray questions = jsonObject.getJSONArray("questions");

                //shuffle questions and pick 5 randomly
                int[] questions_index = new int[numberOfQuestions];
                int count = 0;

                Random random = new Random();

                while (count < numberOfQuestions){
                    int question_index = random.nextInt(questions.length());
                    boolean is_duplicate = false;
                    for (int i = 0; i < count; i++){
                        if (question_index == questions_index[i]){
                            is_duplicate = true;
                            break;
                        }
                    }
                    if (!is_duplicate){
                        questions_index[count++] = question_index;
                    }
                }

                //Deserialize and convert data into java
                for (int i = 0; i < numberOfQuestions; i++){
                    //enter the questions into database
                    int index = questions_index[i]; //Questions are shuffled
                    JSONObject questionSet = questions.getJSONObject(index);
                    String content = questionSet.getString("question");
                    int correctAnswer = questionSet.getInt("answer");
                    GameQuestion gameQuestion =  new GameQuestion(content, correctAnswer);
                    gameQuestions[i] = gameQuestion;
                }

                for (int i = 0; i < numberOfQuestions; i++){
                    //print the questions
                    Log.d("JSON", String.format("%d: %s - %s", i, gameQuestions[i].content, gameQuestions[i].toString()));
                }

                Log.d("JSON", "COMPLETED");
                ivLoading.setVisibility(View.INVISIBLE);
                btnReady.setVisibility(View.VISIBLE);
                tvQuestionContent.setText(R.string.tvReady);
            } catch (Exception e){
                Log.e("JSON", e.toString());
            }
        }
    }

    public void btnPause_click(View v){
        Log.d("Button", "Clicked");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void finish() {
        super.finish();
        db.close();
    }
}
