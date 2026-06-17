package logic;

import javax.swing.SwingUtilities;

public class Timer {

    private int totalSeconds;
    private int secondsLeft;
    private boolean running;
    private TimerCallback callback;
    private Thread timerThread;

    public Timer(int totalSeconds, TimerCallback callback) {
        this.totalSeconds = totalSeconds;
        this.secondsLeft = totalSeconds;
        this.running = false;
        this.callback = callback;
    }

    public void startTimer() {
        running = true;
        timerThread = new Thread(() -> {
            while (running && secondsLeft > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return; // thread was interrupted — stop gracefully
                }
                if (!running) break;
                secondsLeft--;

                // Notify UI on the Event Dispatch Thread
                final int s = secondsLeft;
                SwingUtilities.invokeLater(() -> {
                    if (callback != null) {
                        callback.onTick(s);
                    }
                });

                if (secondsLeft <= 0) {
                    SwingUtilities.invokeLater(() -> {
                        if (callback != null) {
                            callback.onTimeout();
                        }
                    });
                }
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }

    public void stopTimer() {
        running = false;
        if (timerThread != null) {
            timerThread.interrupt();
        }
    }

    public void resetTimer() {
        stopTimer();
        secondsLeft = totalSeconds;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }
}
