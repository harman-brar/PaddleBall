package com.echessbee.hsb.paddleball;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class NightPlay extends AppCompatActivity implements View.OnTouchListener  {

    MyBringBackSurface ourSurfaceView;
    float x, y;
    private int currentApiVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }


        ourSurfaceView = new MyBringBackSurface(this);
        setContentView(ourSurfaceView);

        ourSurfaceView.setOnTouchListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        ourSurfaceView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ourSurfaceView.pause();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();
        return true;
    }

    public class MyBringBackSurface extends SurfaceView implements Runnable {

        private final String TAG = MyBringBackSurface.class.getSimpleName() ;

        SurfaceHolder ourHolder;
        Thread ourThread = null;
        boolean isRunning = false;

        int cx;
        int cy;
        int score;

        private int xVelocity = -11;
        private int yVelocity = -14;

        Bitmap player, ball;
        RectF boundsPlayer, boundsBall;

        float boundCX, boundCY;

        boolean endGame = false;
        boolean collision = false;

        MediaPlayer pop;

        public MyBringBackSurface(Context context) {
            super(context);
            ourHolder = getHolder();

            pop = MediaPlayer.create(context, R.raw.pbpop);
        }


        public void pause() {
            isRunning = false;
            while (true) {
                try {
                    ourThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            ourThread = null;
        }

        public void resume() {
            isRunning = true;
            ourThread = new Thread(this);
            ourThread.start();
        }


        @Override
        public void run() {
            while (isRunning) {
                if (!ourHolder.getSurface().isValid())
                    continue;

                Canvas canvas = ourHolder.lockCanvas();
                canvas.drawRGB(40, 32, 90);

                if (x != 0 && y != 0) {

                    Paint sc = new Paint();
                    sc.setColor(Color.LTGRAY);
                    sc.setTextSize(125);
                    sc.setTypeface(Typeface.MONOSPACE);
                    canvas.drawText(String.valueOf(score), canvas.getWidth() / 8 - 20, canvas.getHeight() / 8, sc);

                    Paint buildings = new Paint();
                    buildings.setColor(Color.rgb(40, 32, 65));

                    canvas.drawRect(0, (canvas.getHeight() /2) - 100 , canvas.getWidth()/4, canvas.getHeight(), buildings);
                    canvas.drawRect((canvas.getWidth()/4) + 2, (canvas.getHeight() / 2 - 200), canvas.getWidth()/2, canvas.getHeight(), buildings);
                    canvas.drawRect((canvas.getWidth()/2) + 2, (canvas.getHeight()/2) - 75, (canvas.getWidth() / 2) + (canvas.getWidth() / 4), canvas.getHeight(), buildings);
                    canvas.drawRect((canvas.getWidth() / 2) + (canvas.getWidth() / 4) + 2, (canvas.getHeight() / 2) - 125, canvas.getWidth(), canvas.getHeight(), buildings);

                    Paint ground = new Paint();
                    ground.setColor(Color.BLACK);

                    canvas.drawRect(0, canvas.getHeight() - 50, canvas.getWidth(), canvas.getHeight(), ground);

                    boundCX = (float) cx;
                    boundCY = (float) cy;

                    if (score < 40 || score >= 60 && score < 75) {
                        player = BitmapFactory.decodeResource(getResources(), R.drawable.nightplayer);
                    } else if ((score >= 40 && score < 60) || score >= 80) {
                        player = BitmapFactory.decodeResource(getResources(), R.drawable.mininightplayer);
                    }

                    canvas.drawBitmap(player, x - (player.getWidth() / 2), y - (player.getHeight() + 150), null);

                    ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);

                    boundsPlayer = new RectF((x - (player.getWidth()/2)), (y - (player.getHeight() + 150)), ((x - (player.getWidth()/2)) + player.getWidth()), ((y - (player.getHeight() + 150)) + player.getHeight()));
                    boundsBall = new RectF(boundCX, boundCY, boundCX + ball.getWidth(), boundCY + ball.getHeight());

                    if (cx < 0 && cy < 0) {
                        cx = 1;
                        cy = 1;
                    } else {
                        cx += xVelocity;
                        cy += yVelocity;


                        if ((cx > canvas.getWidth() - ball.getWidth() / 2) || (cx < 0)) {
                            xVelocity = xVelocity * -1;
                        }

                        if (cy < 0 && !collision) {
                            yVelocity = yVelocity * -1;
                        }

                        canvas.drawBitmap(ball, cx, cy, null);

                    }

                    /*if(((cx < x) && (cx > (x - player.getWidth() + 70))) && (cy < y - player.getHeight() - 160) && (cy > (y - player.getHeight() - 195))) {
                        pop.start();
                        collision = true;
                    }*/

                    if ((yVelocity > 0) && (RectF.intersects(boundsBall, boundsPlayer))) {
                        collision = true;
                        if (score < 10) {
                            collision();
                        } else if ((score >= 10 && score < 25) || (score >= 60 && score < 75 )) {
                            collision2();
                        } else if ((score >= 25 && score < 60) || score >= 75) {
                            collision3();
                        }

                        pop.start();
                    }


                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                    int highScore = prefs.getInt("highScore", 0);

                    if(score > highScore) {
                        highScore = score;
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("highScore", highScore);
                        editor.commit();
                    }

                    //Log.d(TAG, String.valueOf(highScore));


                    if (cy > canvas.getHeight() - ball.getHeight() / 2) {
                        endGame = true;
                    }

                    if (endGame) {

                        Intent i = new Intent(NightPlay.this, Buffer.class);
                        i.putExtra("scores", String.valueOf(score));
                        i.putExtra("highScore", String.valueOf(highScore));

                        if(score == highScore) {
                            i.putExtra("newHighS", "NEW HIGHSCORE!");
                        }

                        startActivity(i);
                    }


                } else {

                    Paint start = new Paint();
                    start.setTextAlign(Paint.Align.CENTER);
                    start.setColor(Color.GRAY);
                    start.setTextSize(60);
                    start.setTypeface(Typeface.MONOSPACE);
                    canvas.drawText("TAP SCREEN TO START", canvas.getWidth() / 2, canvas.getHeight() / 2, start);

                }
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        private void collision3() {
            yVelocity = yVelocity * -1;
            score = score + 1;
            //Log.d(TAG, String.valueOf(score));
            yVelocity = yVelocity - (1/2);
            if(xVelocity < 0) {
                xVelocity = xVelocity - (1/2);
            } else if (xVelocity > 0) {
                xVelocity = xVelocity + (1/2);
            }
            collision = false;
        }

        private void collision2() {
            yVelocity = yVelocity * -1;
            score = score + 1;
            //Log.d(TAG, String.valueOf(score));
            yVelocity = yVelocity - 1;
            if(xVelocity < 0) {
                xVelocity = xVelocity - 1;
            } else if (xVelocity > 0) {
                xVelocity = xVelocity + 1;
            }
            collision = false;
        }

        private void collision() {
            yVelocity = yVelocity * -1;
            score = score + 1;
            //Log.d(TAG, String.valueOf(score));
            yVelocity = yVelocity - 2;
            if(xVelocity < 0) {
                xVelocity = xVelocity - 2;
            } else if (xVelocity > 0) {
                xVelocity = xVelocity + 2;
            }
            collision = false;
        }

    }
}
