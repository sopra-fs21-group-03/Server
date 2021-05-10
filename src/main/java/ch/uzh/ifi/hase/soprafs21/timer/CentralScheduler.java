package ch.uzh.ifi.hase.soprafs21.timer;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

/**
 * Implements the Singleton pattern for a central scheduler
 */
@Configuration
@EnableScheduling
@ComponentScan
@Component
public class CentralScheduler{

    private static AnnotationConfigApplicationContext CONTEXT = null;
    private ScheduledFuture<?> scheduledFuture;

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    /**
     * Get/Create instance of the Scheduler
     * Basically a Singleton pattern, since you really don't want multiple CentralSchedulers running concurrently
     * on your server
     */
    public static CentralScheduler getInstance() {
        // Create new scheduler if it does not exist yet
        if (!isValidBean()) {
            CONTEXT = new AnnotationConfigApplicationContext(CentralScheduler.class);
        }

        // Get Scheduler if it already exists
        return CONTEXT.getBean(CentralScheduler.class);
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    /**
     * Used to start a background thread
     *
     * @param task  task that should be started
     * @param delay delay in which task should be performed
     */
    public void start(Runnable task, Long delay) {
        scheduledFuture = scheduler.schedule(task, Instant.now().plus(Duration.ofMillis(delay)));
    }

    /**
     * Stops all currently running tasks
     */
    public void reset(Runnable task, Long delay) {
        try {
            scheduledFuture.cancel(false);

            scheduledFuture = scheduler.schedule(task, Instant.now().plus(Duration.ofMillis(delay)));
        } catch (NullPointerException ignored){
        }
    }

    /**
     * Checks if it is a valid bean (Scheduler already exists)
     *
     * @return true if CentralScheduler already exists
     * false if CentralScheduler still has to be created
     */
    private static boolean isValidBean() {
        if (CONTEXT == null || !CONTEXT.isActive()) {
            return false;
        }

        try {
            CONTEXT.getBean(CentralScheduler.class);
        }
        catch (NoSuchBeanDefinitionException ex) {
            return false;
        }

        return true;
    }
}
