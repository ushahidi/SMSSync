package org.addhen.smssync.tasks;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public interface ThreadExecutor {
    /**
     * Executes a {@link Runnable}.
     *
     * @param runnable The class that implements {@link Runnable} interface.
     */
    void execute(final Runnable runnable);
}
