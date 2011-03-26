/*
 * Copyright 2010-2011 Roger Kapsi
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.ardverk.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A utility class to create {@link ScheduledThreadPoolExecutor}s and
 * {@link ThreadPoolExecutor}s.
 */
public class ExecutorUtils {
    
    private ExecutorUtils() {}
    
    /**
     * Creates and returns a {@link ScheduledThreadPoolExecutor}
     */
    public static ScheduledThreadPoolExecutor newSingleThreadScheduledExecutor(String name) {
        return newSingleThreadScheduledExecutor(name, purgeFrequency(), TimeUnit.MILLISECONDS);
    }
    
    /**
     * Creates and returns a {@link ScheduledThreadPoolExecutor}
     */
    public static ScheduledThreadPoolExecutor newSingleThreadScheduledExecutor(
            String name, long frequency, TimeUnit unit) {
        return newScheduledThreadPool(1, name, frequency, unit);
    }
    
    /**
     * Creates and returns a {@link ScheduledThreadPoolExecutor}
     */
    public static ScheduledThreadPoolExecutor newScheduledThreadPool(
            int corePoolSize, String name) {
        return newScheduledThreadPool(corePoolSize, name, purgeFrequency(), TimeUnit.MILLISECONDS);
    }
    
    /**
     * Creates and returns a {@link ScheduledThreadPoolExecutor}
     */
    public static ScheduledThreadPoolExecutor newScheduledThreadPool(
            int corePoolSize, String name, long frequency, TimeUnit unit) {
        
        return new ManagedScheduledThreadPoolExecutor(corePoolSize, 
                defaultThreadFactory(name), frequency, unit);
    }

    /**
     * Creates and returns a {@link ThreadPoolExecutor}
     */
    public static AsyncProcessThreadPoolExecutor newCachedThreadPool(String name) {
        return newCachedThreadPool(name, purgeFrequency(), TimeUnit.MILLISECONDS);
    }
    
    /**
     * Creates and returns a {@link ThreadPoolExecutor}
     */
    public static AsyncProcessThreadPoolExecutor newCachedThreadPool(String name, 
            long frequency, TimeUnit unit) {
        
        return new AsyncProcessThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), 
                defaultThreadFactory(name),
                frequency, unit);
    }
    
    /**
     * Creates and returns a {@link ThreadPoolExecutor}
     */
    public static AsyncProcessThreadPoolExecutor newSingleThreadExecutor(String name) {
        return newSingleThreadExecutor(name, purgeFrequency(), TimeUnit.MILLISECONDS);
    }
    
    /**
     * Creates and returns a {@link ThreadPoolExecutor}
     */
    public static AsyncProcessThreadPoolExecutor newSingleThreadExecutor(
            String name, long frequency, TimeUnit unit) {
        return newFixedThreadPool(1, name, frequency, unit);
    }
    
    /**
     * Creates and returns a {@link ThreadPoolExecutor}
     */
    public static AsyncProcessThreadPoolExecutor newFixedThreadPool(int nThreads, String name) {
        return newFixedThreadPool(nThreads, name, purgeFrequency(), TimeUnit.MILLISECONDS);
    }
    
    /**
     * Creates and returns a {@link ThreadPoolExecutor}
     */
    public static AsyncProcessThreadPoolExecutor newFixedThreadPool(int nThreads, 
            String name, long frequency, TimeUnit unit) {
        
        return new AsyncProcessThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), 
                defaultThreadFactory(name),
                frequency, unit);
    }

    /**
     * Creates and returns a {@link ThreadFactory} which creates 
     * {@link Thread}s that are pre-fixed with the given name.
     */
    public static ThreadFactory defaultThreadFactory(String name) {
        return new DefaultThreadFactory(name);
    }
    
    /**
     * Tries to shutdown the given {@link Executor}.
     */
    public static boolean shutdown(Executor executor) {
        if (executor != null) {
            if (executor instanceof ExecutorService) {
                ((ExecutorService)executor).shutdown();
            } else if (executor instanceof Shutdownable<?>) {
                ((Shutdownable<?>)executor).shutdown();
            }
            return true;
        }
        return false;
    }
    
    /**
     * Tries to shutdown the given {@link Executor}s.
     */
    public static boolean shutdownAll(Executor... executors) {
        boolean success = true;
        if (executors != null) {
            for (Executor executor : executors) {
                success &= shutdown(executor);
            }
        }
        return success;
    }
    
    /**
     * Tries to shutdown the given {@link Executor}s.
     */
    public static boolean shutdownAll(Iterable<? extends Executor> executors) {
        boolean success = true;
        if (executors != null) {
            for (Executor executor : executors) {
                success &= shutdown(executor);
            }
        }
        return success;
    }
    
    private static long purgeFrequency() {
        return purgeFrequency(30L, TimeUnit.SECONDS);
    }
    
    private static long purgeFrequency(long defaultValue, TimeUnit unit) {
        Class<?> clazz = ExecutorUtils.class;
        String key = clazz.getCanonicalName() + ".purgeFrequency";
        
        String value = System.getProperty(key);
        if (value != null) {
            return Long.parseLong(value);
        }
        
        return unit.toMillis(defaultValue);
    }
}