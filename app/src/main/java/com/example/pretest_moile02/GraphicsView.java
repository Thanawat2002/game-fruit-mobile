package com.example.pretest_moile02;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GraphicsView extends View implements View.OnTouchListener {
    private float width, height;
    private List<Bitmap> fruitBitmaps;
    private List<Fruit> fruitsList;
    private Handler handler;
    private Random random;
    private CountDownTimer countDownTimer;
    private Paint showText;
    private boolean finish = false;
    private int score = 0;
    private long time = 30;
    private MediaPlayer mPlayer;
    private MediaPlayer backgroundMusicPlayer;
    private int imgFruits[] = {
            R.drawable.blueberries, R.drawable.grapes, R.drawable.hazelnut, R.drawable.orange,
            R.drawable.pineapple, R.drawable.strawberry, R.drawable.tomato, R.drawable.watermelon,
            R.drawable.chili
    };

    public GraphicsView(Context context) {
        super(context);
        showText = new Paint();
        showText.setColor(Color.BLUE);
        showText.setTextSize(60);
        showText.setTextAlign(Paint.Align.LEFT);
        setBackgroundColor(Color.argb(49,229,57,53));
        setOnTouchListener(this);

        fruitBitmaps = new ArrayList<>();
        for (int imgResId : imgFruits) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgResId);
            fruitBitmaps.add(bitmap);
        }
        fruitsList = new ArrayList<>();
        handler = new Handler();
        random = new Random();
        backgroundMusicPlayer = MediaPlayer.create(this.getContext(), R.raw.sound_game);
        backgroundMusicPlayer.setLooping(true);

        startGame();
    }

    private void startGame() {
        fruitsList.clear();
        score = 0;
        time = 30;
        finish = false;

        handler.removeCallbacksAndMessages(null);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        startFruitAnimation();
        startCountdownTimer();
        playBackgroundMusic();
        invalidate();
    }

    private void playBackgroundMusic() {
        if (backgroundMusicPlayer != null && !backgroundMusicPlayer.isPlaying()) {
            backgroundMusicPlayer.start();
        }
    }

    private void startFruitAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (finish) {
                    return;
                }

                float fruitX = random.nextFloat() * (width - fruitBitmaps.get(0).getWidth());
                float fruitY = 0;
                float speed = random.nextFloat() * 20 + 5;

                int fruitIndex = random.nextInt(fruitBitmaps.size());
                fruitsList.add(new Fruit(fruitX, fruitY, speed, fruitBitmaps.get(fruitIndex)));

                handler.postDelayed(this, 500);
            }
        }, 500);
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (Fruit fruit : fruitsList) {
                    fruit.updatePosition();
                }
                invalidate();
                handler.postDelayed(this, 30);
            }
        });
    }

    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time = millisUntilFinished / 1000;
            }

            @Override
            public void onFinish() {
                finish = true;
            }
        }.start();
    }

    private void playSound(int soundResourceId) {
        if (mPlayer != null) {
            mPlayer.release();
        }

        mPlayer = MediaPlayer.create(getContext(), soundResourceId);

        if (mPlayer != null) {
            mPlayer.start();

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });
        }
    }

    private void resetGame() {
        startGame();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (finish) {
            resetGame();
        } else {
            float x = event.getX();
            float y = event.getY();

            Iterator<Fruit> iterator = fruitsList.iterator();
            while (iterator.hasNext()) {
                Fruit fruit = iterator.next();
                if (x >= fruit.x && x < fruit.x + fruit.bitmap.getWidth() &&
                    y >= fruit.y && y < fruit.y + fruit.bitmap.getHeight()) {
                    iterator.remove();
                    int fruitIndex = -1;
                    for (int i = 0; i < imgFruits.length; i++) {
                        if (fruit.bitmap == fruitBitmaps.get(i)) {
                            fruitIndex = i;
                            break;
                        }
                    }
                    if (fruitIndex != -1 && imgFruits[fruitIndex] == R.drawable.chili) {
                        score--;
                        playSound(R.raw.sound_fail);
                    } else {
                        score++;
                        playSound(R.raw.sound_click);
                    }
                    invalidate();
                    break;
                }
            }
        }
        return true;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
        if (finish) {
            backgroundMusicPlayer.pause();
            showText.setColor(Color.RED);
            showText.setTextSize(60);
            showText.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("E N D G A M E", width/2, height/2-350, showText);
            canvas.drawText("Your Score : " + score, width/2, height/2-80, showText);
            canvas.drawText("Touch for Play Game", width/2, height/2 + 100, showText);
        } else {
            showText.setColor(Color.BLUE);
            showText.setTextSize(60);
            showText.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Score : " + score, (width-width)+10, 80, showText);
            canvas.drawText("Time : " + time, width-300, 80, showText);

            Iterator<Fruit> iterator = fruitsList.iterator();
            while (iterator.hasNext()) {
                Fruit fruit = iterator.next();
                canvas.drawBitmap(fruit.bitmap, fruit.x, fruit.y, null);
                if (fruit.y > height) {
                    iterator.remove();
//                score--; // Decrement score when fruit falls
                }
            }
        }

    }

    private class Fruit {
        private float x, y;
        private float speed;
        private Bitmap bitmap;

        public Fruit(float x, float y, float speed, Bitmap bitmap) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.bitmap = bitmap;
        }

        public void updatePosition() {
            y += speed;
        }
    }
}
