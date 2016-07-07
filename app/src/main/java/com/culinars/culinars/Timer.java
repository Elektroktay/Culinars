package com.culinars.culinars;

import java.util.ArrayList;

public class Timer {

    private boolean countDown;
    private int maxSecs;
    private int currentSecs;

    private long lastTime;
    private boolean isPaused;

    private Thread timerThread;

    private ArrayList<OnTimeChangedListener> listeners;

    public Timer(boolean countDown) {
        this(0, countDown);
    }

    public Timer(int maxSecs, boolean countDown) {
        listeners = new ArrayList<>();
        this.maxSecs = maxSecs;
        isPaused = true;
        setCountDown(countDown);
        this.currentSecs = maxSecs;
        init();
    }

    private void init() {
        if (timerThread != null)
            timerThread.interrupt();
        timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!isPaused && (System.currentTimeMillis() - lastTime) > 999) {
                        lastTime = System.currentTimeMillis();
                        if (countDown)
                            currentSecs--;
                        else
                            currentSecs++;
                        for (OnTimeChangedListener listener : listeners) {
                            listener.onTimeChanged(getCurrentTime());
                        }
                    }
                }
            }
        });
    }

    public void setMaxSecs(int maxSecs) {
        this.maxSecs = maxSecs;
    }

    public void setCountDown(boolean countDown) {
        this.countDown = countDown;
    }

    public void startTimer() {
        if (timerThread == null)
            init();
        lastTime = System.currentTimeMillis();
        isPaused = false;
        if (!timerThread.isAlive())
            timerThread.start();

    }

    public void pauseTimer() {
        isPaused = true;
    }

    public void stopTimer() {
        isPaused = true;
        timerThread.interrupt();
    }

    public void resetTimer() {
        currentSecs = maxSecs;
        init();
    }

    public void addOnTimeChangedListener(OnTimeChangedListener listener) {
        listeners.add(listener);
    }

    public void removeOnTimeChangedListener(OnTimeChangedListener listener) {
        listeners.remove(listener);
    }


    public int getCurrentTime() {
        return currentSecs;
    }

    public static int StringToSecs(String text) {
        String[] parts = text.split(":");
        if (parts.length == 1) {
            int result = 0;
            result += Integer.parseInt(parts[1]);
            return result;
        }
        if (parts.length == 2) {
            int result = 0;
            result += Integer.parseInt(parts[0])*60;
            result += Integer.parseInt(parts[1]);
            return result;
        }
        if (parts.length == 3) {
            int result = 0;
            result += Integer.parseInt(parts[0])*3600;
            result += Integer.parseInt(parts[1])*60;
            result += Integer.parseInt(parts[2]);
            return result;
        }
        return -1;
    }

    public static String secsToString(int num) {
        String result = "";
        int hrs=0;
        int mins=0;
        int secs=0;
        if (num > 3600) {
            hrs = num/3600;
            num = num%3600;
        }
        if (num > 60) {
            mins = num/60;
            num = num%60;
        }
        secs = num;

        if (hrs>0)
            result += hrs + ":";
        if (mins>0) {
            if (mins < 10 && hrs > 0)
                result += 0;
            result += hrs + ":";
        }
        if (secs < 10)
            result += 0;
        result += secs;
        return result;
    }

    @Override
    public String toString() {
        return secsToString(currentSecs);
    }

    public void removeListeners() {
        listeners.clear();
    }

    public interface OnTimeChangedListener {
        void onTimeChanged(int currentSecs);
    }
}
