// GameOverActivity.java
package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    private TextView scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        scoreText = findViewById(R.id.scoreText);

        // Lấy điểm số từ Intent
        int score = getIntent().getIntExtra("SCORE", 0);
        scoreText.setText("Your Score: " + score);
    }
}
