/*
 * Copyright 2010-2012 Roger Kapsi
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.ardverk.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of {@link ThreadPoolExecutor} for {@link AsyncFuture}s.
 * 
 * @see ThreadPoolExecutor
 * @see AsyncExecutorService
 */
public class AsyncThreadPoolExecutor extends ManagedThreadPoolExecutor 
    implements AsyncExecutorService {
  
  public AsyncThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
      long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue, long purgeFrequency,
      TimeUnit purgeUnit) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
        purgeFrequency, purgeUnit);
  }

  public AsyncThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
      long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue,
      RejectedExecutionHandler handler, long purgeFrequency,
      TimeUnit purgeUnit) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler,
        purgeFrequency, purgeUnit);
  }

  public AsyncThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
      long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
      long purgeFrequency, TimeUnit purgeUnit) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
        threadFactory, purgeFrequency, purgeUnit);
  }

  public AsyncThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
      long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
      RejectedExecutionHandler handler, long purgeFrequency,
      TimeUnit purgeUnit) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
        threadFactory, handler, purgeFrequency, purgeUnit);
  }

  @Override
  protected <T> AsyncRunnableFuture<T> newTaskFor(Callable<T> callable) {
    return new AsyncFutureTask<T>(callable);
  }

  @Override
  protected <T> AsyncRunnableFuture<T> newTaskFor(Runnable runnable, T value) {
    return new AsyncFutureTask<T>(runnable, value);
  }
  
  @Override
  public <T> AsyncFuture<T> submit(Callable<T> task) {
    return (AsyncFuture<T>)super.submit(task);
  }

  @Override
  public <T> AsyncFuture<T> submit(Runnable task, T result) {
    return (AsyncFuture<T>)super.submit(task, result);
  }

  @Override
  public AsyncFuture<?> submit(Runnable task) {
    return (AsyncFuture<?>)super.submit(task);
  }
}