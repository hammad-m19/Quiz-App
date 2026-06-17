package logic;

/**
 * Callback interface for timer events.
 * The QuizFrame implements this to react to tick and timeout events.
 */
public interface TimerCallback {
    /** Called every second with the remaining seconds. */
    void onTick(int secondsLeft);

    /** Called when the timer reaches zero. */
    void onTimeout();
}
