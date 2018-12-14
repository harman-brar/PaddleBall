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

public class InversePlay extends AppCompatActivity implements View.OnTouchListener {

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
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
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

        private final String TAG = MyBringBackSurface.class.getSimpleName();

        SurfaceHolder ourHolder;
        Thread ourThread = null;
        boolean isRunning = false;

        int score;

        int cx = -1;
        int cy = -1;
        int ax = -1;
        int ay = -1;
        int bx = -1;
        int by = -1;
        int dx = -1;
        int dy = -1;

        private int xVelocity = -11;
        private int yVelocity = -14;
        private int axVelocity = -10;
        private int ayVelocity = -13;
        private int bxVelocity = -9;
        private int byVelocity = -12;
        private int dxVelocity = -12;
        private int dyVelocity = -15;

        Bitmap player, ball, ball2, ball3, ball4;
        RectF boundsPlayer, boundsCBall, boundsABall, boundsBBall, boundsDBall;

        float boundCX, boundCY;
        float boundAX, boundAY;
        float boundBX, boundBY;
        float boundDX, boundDY;

        boolean endGame = false;
        boolean collision = false;
        boolean collision2 = false;
        boolean collision3 = false;
        boolean collision4 = false;

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
                canvas.drawRGB(0, 0, 0);


                if (x != 0 && y != 0) {

                    Paint sc = new Paint();
                    sc.setColor(Color.LTGRAY);
                    sc.setTextSize(125);
                    sc.setTypeface(Typeface.MONOSPACE);
                    canvas.drawText(String.valueOf(score), canvas.getWidth() / 8 - 40, canvas.getHeight() / 8, sc);

                    Paint buildings = new Paint();
                    buildings.setColor(Color.rgb(30, 30, 30));

                    canvas.drawRect(0, (canvas.getHeight() /2) - 100 , canvas.getWidth()/4, canvas.getHeight(), buildings);
                    canvas.drawRect((canvas.getWidth()/4) + 2, (canvas.getHeight() / 2 - 200), canvas.getWidth()/2, canvas.getHeight(), buildings);
                    canvas.drawRect((canvas.getWidth()/2) + 2, (canvas.getHeight()/2) - 75, (canvas.getWidth() / 2) + (canvas.getWidth() / 4), canvas.getHeight(), buildings);
                    canvas.drawRect((canvas.getWidth() / 2) + (canvas.getWidth() / 4) + 2, (canvas.getHeight() / 2) - 125, canvas.getWidth(), canvas.getHeight(), buildings);

                    Paint ground = new Paint();
                    ground.setColor(Color.DKGRAY);

                    canvas.drawRect(0, canvas.getHeight() - 50, canvas.getWidth(), canvas.getHeight(), ground);


                    boundCX = (float) cx;
                    boundCY = (float) cy;

                    boundAX = (float) ax;
                    boundAY = (float) ay;

                    boundBX = (float) bx;
                    boundBY = (float) by;

                    boundDX = (float) dx;
                    boundDY = (float) dy;

                    if (score < 65) {
                        player = BitmapFactory.decodeResource(getResources(), R.drawable.nightplayer);
                    } else if (score >= 65) {
                        player = BitmapFactory.decodeResource(getResources(), R.drawable.mininightplayer);
                    }
                    canvas.drawBitmap(player, x - (player.getWidth() / 2), y - (player.getHeight() + 150), null);

                    ball = BitmapFactory.decodeResource(getResources(), R.drawable.dayball);
                    ball2 = BitmapFactory.decodeResource(getResources(), R.drawable.orangeball);
                    ball3 = BitmapFactory.decodeResource(getResources(), R.drawable.greenball);
                    ball4 = BitmapFactory.decodeResource(getResources(), R.drawable.redball);

                    boundsPlayer = new RectF((x - (player.getWidth() / 2)), (y - (player.getHeight() + 150)), ((x - (player.getWidth() / 2)) + player.getWidth()), ((y - (player.getHeight() + 150)) + player.getHeight()));
                    boundsCBall = new RectF(boundCX, boundCY, boundCX + ball.getWidth(), boundCY + ball.getHeight());
                    boundsABall = new RectF(boundAX, boundAY, boundAX + ball2.getWidth(), boundAY + ball2.getHeight());
                    boundsBBall = new RectF(boundBX, boundBY, boundBX + ball3.getWidth(), boundBY + ball3.getHeight());
                    boundsDBall = new RectF(boundDX, boundDY, boundDX + ball4.getWidth(), boundDY + ball4.getHeight());

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

                    //--------------------------- ball 2 c----------------------------------

                    if (ax < 0 && ay < 0) {
                        ax = 1;
                        ay = 1;
                    } else {
                        ax += axVelocity;
                        ay += ayVelocity;


                        if ((ax > canvas.getWidth() - ball.getWidth() / 2) || (ax < 0)) {
                            axVelocity = axVelocity * -1;
                        }

                        if (ay < 0 && !collision2) {
                            ayVelocity = ayVelocity * -1;
                        }

                        canvas.drawBitmap(ball2, ax, ay, null);

                    }

                    /*if(((ax < x) && (ax > (x - player.getWidth() + 70))) && (ay < y - player.getHeight() - 150) && (ay > (y - player.getHeight() - 185))) {
                        pop.start();
                        collision2 = true;
                    }*/

                    //--------------------------- ball 3 a ----------------------------------

                    if (bx < 0 && by < 0) {
                        bx = 1;
                        by = 1;
                    } else {
                        bx += bxVelocity;
                        by += byVelocity;


                        if ((bx > canvas.getWidth() - ball.getWidth() / 2) || (bx < 0)) {
                            bxVelocity = bxVelocity * -1;
                        }

                        if (by < 0 && !collision3) {
                            byVelocity = byVelocity * -1;
                        }

                        canvas.drawBitmap(ball3, bx, by, null);

                    }

                    /*if(((bx < x) && (bx > (x - player.getWidth() + 70))) && (by < y - player.getHeight() - 150) && (by > (y - player.getHeight() - 185))) {
                        pop.start();
                        collision3 = true;
                    }*/

                    //--------------------------- ball 4 b ----------------------------------

                    if (dx < 0 && dy < 0) {
                        dx = 1;
                        dy = 1;
                    } else {
                        dx += dxVelocity;
                        dy += dyVelocity;


                        if ((dx > canvas.getWidth() - ball.getWidth() / 2) || (dx < 0)) {
                            dxVelocity = dxVelocity * -1;
                        }

                        if (dy < 0 && !collision4) {
                            dyVelocity = dyVelocity * -1;
                        }

                        canvas.drawBitmap(ball4, dx, dy, null);

                    }

                    /*if(((dx < x) && (dx > (x - player.getWidth() + 70))) && (dy < y - player.getHeight() - 150) && (dy > (y - player.getHeight() - 185))) {
                        pop.start();
                        collision4 = true;
                    }*/

                    // =====================COLLISION===============================

                    if ((yVelocity > 0) && (RectF.intersects(boundsCBall, boundsPlayer))) {
                            collision = true;
                            collisionC();
                            pop.start();
                        }

                        if ((ayVelocity > 0) && (RectF.intersects(boundsABall, boundsPlayer))) {
                            collision2 = true;
                            collisionA();
                            pop.start();
                        }

                        if ((byVelocity > 0) && (RectF.intersects(boundsBBall, boundsPlayer))) {
                            collision3 = true;
                            collisionB();
                            pop.start();
                        }

                        if ((dyVelocity > 0) && (RectF.intersects(boundsDBall, boundsPlayer))) {
                            collision4 = true;
                            collisionD();
                            pop.start();
                        }

                    //--------------------------- end ----------------------------------

                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                    int highScore = prefs.getInt("highScore", 0);

                    if (score > highScore) {
                        highScore = score;
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("highScore", highScore);
                        editor.commit();
                    }

                    //Log.d(TAG, String.valueOf(highScore));


                    if (cy > canvas.getHeight() - ball.getHeight() / 2 && ay > canvas.getHeight() - ball.getHeight() / 2 && by > canvas.getHeight() - ball.getHeight() / 2 && dy > canvas.getHeight() - ball.getHeight() / 2) {
                        endGame = true;
                    }

                    if (endGame) {

                        Intent i = new Intent(InversePlay.this, Buffer.class);
                        i.putExtra("scores", String.valueOf(score));
                        i.putExtra("highScore", String.valueOf(highScore));

                        if (score == highScore) {
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

        private void collisionD() {
            dyVelocity = dyVelocity * -1;
            score = score + 1;
            //Log.d(TAG, String.valueOf(score));
            if (score < 35) {
                dyVelocity = dyVelocity - 2;
                if (dxVelocity < 0) {
                    dxVelocity = dxVelocity - 2;
                } else if (dxVelocity > 0) {
                    dxVelocity = dxVelocity + 2;
                }
            }
            collision4 = false;
        }

        private void collisionB() {
            byVelocity = byVelocity * -1;
            score = score + 1;
            //Log.d(TAG, String.valueOf(score));
            if (score < 60 || (score >= 85 && score < 90)) {
                byVelocity = byVelocity - 1;
                if (bxVelocity < 0) {
                    bxVelocity = bxVelocity - 3;
                } else if (axVelocity > 0) {
                    bxVelocity = bxVelocity + 3;
                }
            }
            collision3 = false;
        }

        private void collisionA() {
            ayVelocity = ayVelocity * -1;
            score = score + 1;
            //Log.d(TAG, String.valueOf(score));
            if (score < 35) {
                ayVelocity = ayVelocity - 2;
                if (axVelocity < 0) {
                    axVelocity = axVelocity - 3;
                } else if (axVelocity > 0) {
                    axVelocity = axVelocity + 3;
                }
            }
            collision2 = false;
        }

        private void collisionC() {
            yVelocity = yVelocity * -1;
            score = score + 1;
            //Log.d(TAG, String.valueOf(score));
            if (score < 65) {
                yVelocity = yVelocity - 1;
                if (xVelocity < 0) {
                    xVelocity = xVelocity - 2;
                } else if (xVelocity > 0) {
                    xVelocity = xVelocity + 2;
                }
            }
            collision = false;
        }

    }

}
