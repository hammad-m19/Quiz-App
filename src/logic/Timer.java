package logic;

import javax.swing.SwingUtilities;

/**
 * Handles countdown timer for each quiz question.
 * Runs on a separate thread and notifies the UI via TimerCallback.
 */
public class Timer {

    private int totalSeconds;
    private int secondsLeft;
    private boolean running;
    private TimerCallback callback;
    private Thread timerThread;

    /**
     * Creates a new Timer with the given duration.
     *
     * @param totalSeconds the countdown duration in seconds
     * @param callback     the listener to notify on tick / timeout
     */
    public Timer(int totalSeconds, TimerCallback callback) {
        this.totalSeconds = totalSeconds;
        this.secondsLeft = totalSeconds;
        this.running = false;
        this.callback = callback;
    }

    /**
     * Starts (or resumes) the countdown.
     */
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

    /**
     * Stops the timer.
     */
    public void stopTimer() {
        running = false;
        if (timerThread != null) {
            timerThread.interrupt();
        }
    }

    /**
     * Resets the timer back to its original duration and stops it.
     */
    public void resetTimer() {
        stopTimer();
        secondsLeft = totalSeconds;
    }

    /**
     * Returns the remaining seconds.
     */
    public int getSecondsLeft() {
        return secondsLeft;
    }
}
