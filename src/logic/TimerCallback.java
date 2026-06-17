package logic;

public interface TimerCallback {
    void onTick(int secondsLeft);

    void onTimeout();
}
