package com.example.spider007.tiktaktoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class MainActivity extends Activity implements View.OnTouchListener {

    SpiderSurfaceView Ssv;
    int Chanse = 1;

    //// Game Variable

    float[] XCoordi = {0, 0, 0, 0};
    float[] YCoordi = {0, 0, 0, 0};
    float[][] State = {{0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ssv = new SpiderSurfaceView(MainActivity.this);
        Ssv.setOnTouchListener(this);
        setContentView(Ssv);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Ssv.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Ssv.resume();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x, y;
        x = event.getX();
        y = event.getY();
        Ssv.MakeMove(x, y);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_restart) {
            for (int i=0;i<3;i++)
                for (int j=0;j<3;j++)
                    State[i][j]=0;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SpiderSurfaceView extends SurfaceView implements Runnable {

        SurfaceHolder ourHolder;
        Thread ourThread = null;
        float Cw;
        float Ch;
        float lineW = 20;
        Bitmap p1, p2;
        private boolean isRunning = true;

        public SpiderSurfaceView(Context context) {
            super(context);
            ourHolder = getHolder();
            p1 = BitmapFactory.decodeResource(getResources(), R.drawable.button_blank_blue_01);
            p2 = BitmapFactory.decodeResource(getResources(), R.drawable.button_blank_gray_01);
        }

        private void initiateCoordi(Canvas canvas) {

            Cw = canvas.getWidth();
            Ch = canvas.getHeight();
            XCoordi[0] = 0.0f;
            XCoordi[1] = Cw / 3;
            XCoordi[2] = Cw * 2 / 3;
            XCoordi[3] = Cw;

            YCoordi[0] = 0.0f;
            YCoordi[1] = Ch / 3;
            YCoordi[2] = Ch * 2 / 3;
            YCoordi[3] = Ch;
        }

        public void pause() {
            isRunning = false;
            while (true) {
                try {
                    ourThread.join();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                ////
                drawBoard(canvas);
                printState(canvas);
                ////
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        private void Checkwin() {
            boolean win;
            // Checking vertical

            for (int i = 0; i < 3; i++) {

                win = true;
                for (int j = 0; j < 3; j++) {
                    if (State[i][j] != Chanse){
                        win =false;
                    }
                }
                if(win){
                    PlayerWon();
                    return;
                }

            }
            // Checking Horizontal

            for (int i = 0; i < 3; i++) {

                win = true;
                for (int j = 0; j < 3; j++) {
                    if (State[j][i] != Chanse){
                        win =false;
                    }
                }
                if(win){
                    PlayerWon();
                    return;
                }

            }

            // Checking cross
            if(State[0][0] == Chanse && State[1][1] == Chanse && State[2][2] == Chanse) {
                PlayerWon();
                return;
            }

            if(State[0][2] == Chanse && State[1][1] == Chanse && State[2][0] == Chanse) {
                PlayerWon();
                return;


            }

            checkTie();
        }

        private void checkTie() {
            for (int i=0;i<3;i++)
                for (int j=0;j<3;j++)
                    if(State[i][j]==0){
                        return;
                    }
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Game Result")
                    .setMessage("Game Tie")
                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            for (int i=0;i<3;i++)
                                for (int j=0;j<3;j++)
                                    State[i][j]=0;
                        }
                    })
                    .setNegativeButton(R.string.Exit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        private void PlayerWon() {
            if(Chanse == 1){
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Bingo")
                        .setMessage("Player 1 Won!")
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                for (int i=0;i<3;i++)
                                    for (int j=0;j<3;j++)
                                        State[i][j]=0;
                            }
                        })
                        .setNegativeButton(R.string.Exit, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else{
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Bingo")
                        .setMessage("Player 2 Won!")
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                for (int i=0;i<3;i++)
                                    for (int j=0;j<3;j++)
                                        State[i][j]=0;
                            }
                        })
                        .setNegativeButton(R.string.Exit, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        }

        private void printState(Canvas canvas) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (State[i][j] == 0)
                        continue;
                    if (State[i][j] == 1.0f) {
                        drawMove(canvas, i, j, p1);
                    } else if (State[i][j] == 2.0f) {
                        drawMove(canvas, i, j, p2);
                    }
                }
            }

        }

        private void drawMove(Canvas canvas, int i, int j, Bitmap p) {
            float Cx, Cy, BmCx, BmCy;
            Cx = (XCoordi[i + 1] - XCoordi[i]) / 2 + XCoordi[i];
            Cy = (YCoordi[j + 1] - YCoordi[j]) / 2 + YCoordi[j];
            BmCx = p.getWidth() / 2;
            BmCy = p.getHeight() / 2;

            canvas.drawBitmap(p, Cx - BmCx, Cy - BmCy, null);
        }

        private void drawBoard(Canvas canvas) {
            initiateCoordi(canvas);

            canvas.drawColor(Color.rgb(200, 200, 200));
            Paint brush = new Paint();
            brush.setColor(Color.BLACK);

            // Vertical Lines
            canvas.drawRect(XCoordi[0], YCoordi[0], XCoordi[0] + lineW, YCoordi[3], brush);
            canvas.drawRect(XCoordi[1] - lineW, YCoordi[0], XCoordi[1] + lineW, YCoordi[3], brush);
            canvas.drawRect(XCoordi[2] - lineW, YCoordi[0], XCoordi[2] + lineW, YCoordi[3], brush);
            canvas.drawRect(XCoordi[3] - lineW, YCoordi[0], XCoordi[3], YCoordi[3], brush);

            // Horizontal Lines
            canvas.drawRect(XCoordi[0], YCoordi[0], XCoordi[3], YCoordi[0] + lineW, brush);
            canvas.drawRect(XCoordi[0], YCoordi[1] - lineW, XCoordi[3], YCoordi[1] + lineW, brush);
            canvas.drawRect(XCoordi[0], YCoordi[2] - lineW, XCoordi[3], YCoordi[2] + lineW, brush);
            canvas.drawRect(XCoordi[0], YCoordi[3] - lineW, XCoordi[3], YCoordi[3], brush);
        }


        private void MakeMove(float x, float y) {
            int i, j;
            if (x < Cw / 3) {
                if (y < Ch / 3) {
                    i = 0;
                    j = 0;
                } else if (y < Ch * 2 / 3) {
                    i = 0;
                    j = 1;
                } else {
                    i = 0;
                    j = 2;
                }
            } else if (x < Cw * 2 / 3) {
                if (y < Ch / 3) {
                    i = 1;
                    j = 0;
                } else if (y < Ch * 2 / 3) {
                    i = 1;
                    j = 1;
                } else {
                    i = 1;
                    j = 2;
                }
            } else {
                if (y < Ch / 3) {
                    i = 2;
                    j = 0;
                } else if (y < Ch * 2 / 3) {
                    i = 2;
                    j = 1;
                } else {
                    i = 2;
                    j = 2;
                }
            }
            if (CheckFree(i, j)) {
                State[i][j] = Chanse;
                Checkwin();
                if (Chanse == 1)
                    Chanse = 2;
                else
                    Chanse = 1;
            }
        }

        private boolean CheckFree(int i, int i1) {
            if (State[i][i1] == 0)
                return true;
            else
                return false;
        }

    }
}