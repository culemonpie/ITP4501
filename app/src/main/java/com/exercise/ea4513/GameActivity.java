package com.exercise.ea4513;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    long startTime; // The time when the user started to answer a question.
    final int NUMBER_OF_QUESTIONS = Constant.Values.NUMBER_OF_QUESTIONS;
    ;
    int currentQuestionIndex = -1; // Which question the user is currently at. Should be an integer between -1 to NUMBER_OF_QUESTIONS-1 (zero based index). -1 means that the questions have not been loaded yet.
    int correctAnswers = 0; //The number of correct answers the user got.
    Response[] responses = new Response[NUMBER_OF_QUESTIONS];


    // Background colors that are used in the view during different situations
    int NORMAL_COLOR = 0xFF726B6B;
    int CORRECT_COLOR = 0xFF479D32;
    int INCORRECT_COLOR = 0xFFA72525;

    DownloadTask task;

    Button btnReady;
    Button btnSkip;
    Button btnNext;

    Chronometer tvTimer;
    TextView tvQuestionNumber;
    TextView tvQuestionContent;
    ImageView ivLoading;
    View actGame;
    RadioGroup rgAnswers;
    long totalTime;

    SQLiteDatabase db;
    String DB_NAME = Constant.Values.DB_NAME;
    String qs;


    GameQuestion[] gameQuestions = new GameQuestion[NUMBER_OF_QUESTIONS];

    public class Response{
        long time_spent;
        boolean isCorrect;

        public Response(long t, boolean c){
            time_spent = t;
            isCorrect = c;
        }

    }

    public void setTvQuestionNumber() {
        String txt = String.format("%d of %d", currentQuestionIndex + 1, NUMBER_OF_QUESTIONS);
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
        btnSkip = findViewById(R.id.btnSkip);
        ivLoading = findViewById(R.id.ivLoading);
        rgAnswers = findViewById(R.id.rbAnswers);

        //Setup the UI elements
        btnReady.setVisibility(View.INVISIBLE);
        btnSkip.setVisibility(View.INVISIBLE);
        rgAnswers.setVisibility(View.INVISIBLE);

        //Setup logic
        setTvQuestionNumber();
        // Loads the json containing the 10 questions.
        String json_response;

        totalTime = 0;

        try{
            task = new DownloadTask();
            String url_string = "https://ajtdbwbzhh.execute-api.us-east-1.amazonaws.com/default/201920ITP4501Assignment";
            task.execute(url_string);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

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

        if (currentQuestionIndex < NUMBER_OF_QUESTIONS) {
            setTvQuestionNumber();
            tvQuestionContent.setText(gameQuestions[currentQuestionIndex].content);
            actGame.setBackgroundColor(NORMAL_COLOR);

            for (int i = 0; i < rgAnswers.getChildCount(); i++){
                RadioButton radioAnswer = (RadioButton) rgAnswers.getChildAt(i);
                radioAnswer.setText(gameQuestions[currentQuestionIndex].answers[i].value + "");
            }

            Log.d("time", tvTimer.getBase()+"" );
            tvTimer.start();
        } else {
            //Game ends when all questions are asked
            Intent data = new Intent(this, EndGameActivity.class);
            data.putExtra("correctAnswers", correctAnswers);
            data.putExtra("NUMBER_OF_QUESTIONS", NUMBER_OF_QUESTIONS);
            long elapsed =  SystemClock.elapsedRealtime() -  tvTimer.getBase();

            try{
                db = SQLiteDatabase.openDatabase(Constant.Values.DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                qs = "CREATE TABLE IF NOT EXISTS \"TestsLog\" (\n" +
                        "\t\"testNo\"\tINTEGER NOT NULL,\n" +
                        "\t\"testDate\"\tREAL,\n" +
                        "\t\"time\"\tINTEGER,\n" +
                        "\t\"duration\"\tINTEGER,\n" +
                        "\t\"correctCount\"\tINTEGER,\n" +
                        "\tPRIMARY KEY(\"testNo\")\n" +
                        ");\n";
                db.execSQL(qs);
                db.close();
            } catch(SQLiteException e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }


            data.putExtra("timeSpent", 30 );
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
        tvTimer.stop();

        btnSkip.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        for (int i = 0; i < rgAnswers.getChildCount(); i++) {
            rgAnswers.getChildAt(i).setEnabled(false);
        }

        //for now, we will use a random variable to determine if it's right
        RadioButton rbAnswer = (RadioButton)v;
        boolean isCorrect = rbAnswer.getText().toString().equals(gameQuestions[currentQuestionIndex].correctAnswer+"");

        tvTimer.getBase();
        long timeSpent = tvTimer.getBase();
        Log.d("Time spent", timeSpent + "" );
        responses[currentQuestionIndex] = new Response(timeSpent, isCorrect);

        try{

        }catch (SQLiteException e){

        }

        if (isCorrect){
            actGame.setBackgroundColor(CORRECT_COLOR);
            correctAnswers++;
        } else{
            actGame.setBackgroundColor(INCORRECT_COLOR);
        }
    }


    public void setTvQuestion(){

    }


    //@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
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
                // load json result
                JSONObject jsonObject = new JSONObject(result);
                JSONArray questions = jsonObject.getJSONArray("questions");

                //shuffle questions and pick 5 randomly
                int[] questions_index = new int[NUMBER_OF_QUESTIONS];
                int count = 0;

                Random random = new Random();

                while (count < NUMBER_OF_QUESTIONS){
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

                //start
                for (int i = 0; i < NUMBER_OF_QUESTIONS; i++){
                    //enter the questions into database
                    int index = questions_index[i]; //Questions are shuffled
                    JSONObject questionSet = questions.getJSONObject(index);
                    String content = questionSet.getString("question");
                    int correctAnswer = questionSet.getInt("answer");
                    GameQuestion gameQuestion =  new GameQuestion(content, correctAnswer);
                    gameQuestions[i] = gameQuestion;
                }

                for (int i = 0; i < NUMBER_OF_QUESTIONS; i++){
                    //print the questions
                    Log.d("JSON", String.format("%d: %s - %s", i, gameQuestions[i].content, gameQuestions[i].toString()));
                }

                Log.d("JSON", "COMPLETED");
                ivLoading.setVisibility(View.INVISIBLE);
                btnReady.setVisibility(View.VISIBLE);
                tvQuestionContent.setText("Please click the I\'m ready button to start");
            } catch (Exception e){
                Log.e("JSON", e.toString());
            }
        }
    }


}
