package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RaceActivity extends AppCompatActivity {

    private FrameLayout gameLayout;
    private ImageView playerCar, background1, background2, obstacle;
    private TextView gameOverText, scoreText;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private Handler handler = new Handler();

    private int backgroundY1 = 0;
    private int backgroundY2;
    private int scrollSpeed = 10;

    private int score = 0;
    private int obstacleSpeed = 10;
    private int obstacleY = -200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        gameLayout = findViewById(R.id.gameLayout);
        playerCar = findViewById(R.id.playerCar);
        background1 = findViewById(R.id.background1);
        background2 = findViewById(R.id.background2);
        obstacle = findViewById(R.id.obstacle);
        gameOverText = findViewById(R.id.gameOverText);
        scoreText = findViewById(R.id.scoreText);

        // Initialize background position
        backgroundY2 = -getResources().getDisplayMetrics().heightPixels;

        // Sensor setup
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);

        // Start the background scrolling
        handler.post(updateBackground);
        handler.post(updateObstacle);
    }

    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0]; // left/right

            float newX = playerCar.getX() - x * 5; // Sensitivity

            // Limit within screen bounds
            if (newX >= 0 && newX <= gameLayout.getWidth() - playerCar.getWidth()) {
                playerCar.setX(newX);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private Runnable updateBackground = new Runnable() {
        @Override
        public void run() {
            backgroundY1 += scrollSpeed;
            backgroundY2 += scrollSpeed;

            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            if (backgroundY1 >= screenHeight) {
                backgroundY1 = -screenHeight;
            }

            if (backgroundY2 >= screenHeight) {
                backgroundY2 = -screenHeight;
            }

            background1.setY(backgroundY1);
            background2.setY(backgroundY2);

            handler.postDelayed(this, 16); // 60fps
        }
    };

    private Runnable updateObstacle = new Runnable() {
        @Override
        public void run() {
            obstacleY += obstacleSpeed;
            if (obstacleY > getResources().getDisplayMetrics().heightPixels) {
                // Reset the obstacle position
                obstacleY = -200;
                // Randomly set new X position for the obstacle
                obstacle.setX((float) (Math.random() * (gameLayout.getWidth() - obstacle.getWidth())));
                score++; // Increase score
            }

            // Check for collision
            if (obstacleY + obstacle.getHeight() >= playerCar.getY() &&
                    obstacleY <= playerCar.getY() + playerCar.getHeight() &&
                    obstacle.getX() + obstacle.getWidth() > playerCar.getX() &&
                    obstacle.getX() < playerCar.getX() + playerCar.getWidth()) {
                gameOver();
            }

            obstacle.setY(obstacleY);
            scoreText.setText("Score: " + score);

            handler.postDelayed(this, 16); // 60fps
        }
    };

    private void gameOver() {
        // Gửi điểm số sang GameOverActivity
        Intent intent = new Intent(RaceActivity.this, GameOverActivity.class);
        intent.putExtra("SCORE", score);  // Truyền điểm số qua Intent
        startActivity(intent);

        // Dừng game
        handler.removeCallbacks(updateBackground);
        handler.removeCallbacks(updateObstacle);

        // Kết thúc hiện tại Activity (nếu cần)
        finish();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(sensorListener);
        handler.removeCallbacks(updateBackground);
        handler.removeCallbacks(updateObstacle);
    }
}
