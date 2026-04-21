package com.ucm.server.logic;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Timer {
    
    private static final Logger log = LogManager.getLogger(Timer.class);

    private final ScheduledExecutorService _scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicInteger _seconds = new AtomicInteger(0);
    private final AtomicBoolean _isRunning = new AtomicBoolean(false);
    private ScheduledFuture<?> _task;

    private int _max;


    public Timer(int max) {
        _max = max;
    }

    public synchronized void start() {

        if (_isRunning.get())
            return;


        log.debug("Timer started!");
        _isRunning.set(true);
        _task = _scheduler.scheduleAtFixedRate(() -> {
            int current = _seconds.incrementAndGet();

            if(current >= _max) {
                _isRunning.set(false);

                ScheduledFuture<?> localTask = _task;
                if (localTask != null) {
                    localTask.cancel(false);
                }

                log.debug("Timer stopped at {} seconds", _max);
            }

        }, 0, 1, TimeUnit.SECONDS);
    }

    public synchronized void stop() {

        if (!_isRunning.get())
            return;

        _task.cancel(false);
        _isRunning.set(false);
    }

    public synchronized void reset() {
        stop();
        _seconds.set(0);
    }

    public synchronized void restart() {
        stop();
        _seconds.set(0);
        start();
    }

    public void shutdown() {
        _scheduler.shutdownNow();
    }

    public int getSecondsLeft() {
        int elapsed = _seconds.get();
        int left = _max - elapsed;
        return Math.max(left, 0);
    }

    
    public int getSegundos() { return _seconds.get(); }
    public boolean isRunning() { return _isRunning.get(); }

}