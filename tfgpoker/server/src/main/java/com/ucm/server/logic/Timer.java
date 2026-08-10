package com.ucm.server.logic;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * A simple timer implementation that counts down from a specified maximum value.
 * This timer can be started, stopped, reset, and restarted in background.
 */
public class Timer {
    

    /**
     * The scheduler used to run the timer task in a separate thread.
     */
    private final ScheduledExecutorService _scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * The number of seconds that have elapsed since the timer was started.
     */
    private final AtomicInteger _seconds = new AtomicInteger(0);

    /**
     * Indicates whether the timer is currently running or not.
     */
    private final AtomicBoolean _isRunning = new AtomicBoolean(false);

    /**
     * The scheduled task that runs the timer logic in the background.
     */
    private ScheduledFuture<?> _task;


    /**
     * The maximum number of seconds that the timer will count down from.
     */
    private int _max;


    /**
     * Creates a new Timer instance with the specified maximum value.
     * @param max the maximum number of seconds for the timer
     */
    public Timer(int max) {
        _max = max;
    }

    /**
     * Starts the timer if it is not already running.
     * If the timer is already running, this method does nothing.
     * If the timer reaches the maximum value, it will stop automatically.
     */
    public synchronized void start() {

        if (_isRunning.get())
            return;


        _isRunning.set(true);
        _task = _scheduler.scheduleAtFixedRate(() -> {
            int current = _seconds.incrementAndGet();

            if(current >= _max) {
                _isRunning.set(false);

                ScheduledFuture<?> localTask = _task;
                if (localTask != null) {
                    localTask.cancel(false);
                }
            }

        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Stops the timer if it is currently running.
     * If the timer is not running, this method does nothing.
     */
    public synchronized void stop() {

        if (!_isRunning.get())
            return;

        _task.cancel(false);
        _isRunning.set(false);
    }

    /**
     * Resets the timer to zero and stops it if it is currently running.
     * If the timer is not running, this method simply resets the elapsed time to zero.
     */
    public synchronized void reset() {
        stop();
        _seconds.set(0);
    }

    /**
     * Restarts the timer by stopping it, resetting the elapsed time to zero, and starting it again.
     * If the timer is not running, this method simply resets the elapsed time to zero and
     */
    public synchronized void restart() {
        stop();
        _seconds.set(0);
        start();
    }

    /**
     * Shuts down the timer's scheduler, stopping any running tasks and preventing new tasks from being scheduled.
     */
    public void shutdown() {
        _scheduler.shutdownNow();
    }

    /**
     * Returns the number of seconds left before the timer reaches its maximum value.
     * @return the number of seconds left, or 0 if the timer has already reached its maximum value
     */
    public int getSecondsLeft() {
        int elapsed = _seconds.get();
        int left = _max - elapsed;
        return Math.max(left, 0);
    }

    /**
     * Returns whether the timer is currently running or not.
     * @return true if the timer is running, false otherwise
     */
    public boolean isRunning() { return _isRunning.get(); }

}