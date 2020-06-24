package com.exercise.ea4513;

import android.util.Log;

import java.util.Random;

public class GameQuestion {
    /**
     A question that is used in the game.
     A question should contain NUMBER_OF_ANSWERS answers (currently set as 4), where 1 of them is correct.

     */
    String content;
    GameAnswer[] answers;
    int correctAnswer; //Deprecated. Use answers[correctIndex] instead
    int correctIndex;

    Random random;
    int count;
    final int NUMBER_OF_ANSWERS = Constant.Values.NUMBER_OF_ANSWERS; //number of answers per question
    final int DEVIATION = 10; // the deviation to the values of an incorrect answer
    public GameQuestion(String content, int correctAnswer){
        answers = new GameAnswer[NUMBER_OF_ANSWERS];
        this.content = content;
        this.correctAnswer = correctAnswer;
        count = 0;
        random = new Random();
        this.generateAnswers();
    }

    public boolean generateAnswers(){
        /**
         Generates 4 answers to the question, where 1 of them is correct. The answers shall be shuffled randomly.
         */

        int[] answers_int = new int[NUMBER_OF_ANSWERS];
        answers_int[count++] = correctAnswer;
        while (count < NUMBER_OF_ANSWERS){
            boolean is_duplicate = false;
            int tempAns = correctAnswer - DEVIATION / 2 +  random.nextInt(DEVIATION);
            for(int i = 0; i < count; i++){
                if (tempAns == answers_int[i]){
                    is_duplicate = true;
                    break;
                }
            }
            if (!is_duplicate){
                answers_int[count++] = tempAns;
            }
        }

        //Shuffle the answers. Prior to shuffling, the first answer is the correct answer
//        answers[0] = new GameAnswer(correctAnswer, true);
        int correctIndex = random.nextInt(NUMBER_OF_ANSWERS);
        answers[correctIndex] = new GameAnswer(answers_int[0], false);
        int j = 1;
        while (j < NUMBER_OF_ANSWERS){
            int index = random.nextInt(NUMBER_OF_ANSWERS);
            if (answers[index] == null){
                //Prior to shuffling, the first answer is the correct answer
                answers[index] = new GameAnswer(answers_int[j++], false);
            }
        }
        return true;
    }

    @Override
    public String toString(){
        String n = "";
        for (int i = 0; i < NUMBER_OF_ANSWERS; i++){
            if (answers[i].isCorrect){
                n += "[" + answers[i].value + "] ";
            } else{
                n += answers[i].value + " ";
            }
        }
        return n;
    }

}
