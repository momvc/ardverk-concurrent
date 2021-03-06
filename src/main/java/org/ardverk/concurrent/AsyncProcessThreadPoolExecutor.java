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
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of {@link AsyncThreadPoolExecutor} for {@link AsyncProcessFuture}s.
 * 
 * @see AsyncThreadPoolExecutor
 * @see AsyncProcessExecutorService
 */
public class AsyncProcessThreadPoolExecutor extends AsyncThreadPoolExecutor 
    implements AsyncProcessExecutorService {

  private volatile long timeoutInMillis = -1;
  
  public AsyncProcessThreadPoolExecutor(int corePoolSize,
      int maximumPoolSize, long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue, long purgeFrequency,
      TimeUnit purgeUnit) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
        purgeFrequency, purgeUnit);
  }

  public AsyncProcessThreadPoolExecutor(int corePoolSize,
      int maximumPoolSize, long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue,
      RejectedExecutionHandler handler, long purgeFrequency,
      TimeUnit purgeUnit) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler,
        purgeFrequency, purgeUnit);
  }

  public AsyncProcessThreadPoolExecutor(int corePoolSize,
      int maximumPoolSize, long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
      long purgeFrequency, TimeUnit purgeUnit) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
        threadFactory, purgeFrequency, purgeUnit);
  }

  public AsyncProcessThreadPoolExecutor(int corePoolSize,
      int maximumPoolSize, long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
      RejectedExecutionHandler handler, long purgeFrequency,
      TimeUnit purgeUnit) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
        threadFactory, handler, purgeFrequency, purgeUnit);
  }

  @Override
  public long getTimeout(TimeUnit unit) {
    return unit.convert(timeoutInMillis, TimeUnit.MILLISECONDS);
  }

  @Override
  public long getTimeoutInMillis() {
    return getTimeout(TimeUnit.MILLISECONDS);
  }

  @Override
  public void setTimeout(long timeout, TimeUnit unit) {
    timeoutInMillis = unit.toMillis(timeout);
  }
  
  /**
   * Creates and returns an {@link AsyncRunnableFuture} for 
   * the given {@link AsyncProcess}.
   */
  protected <T> AsyncProcessRunnableFuture<T> newTaskFor(
      AsyncProcess<T> process, long timeout, TimeUnit unit) {
    return new AsyncProcessFutureTask<T>(process, timeout, unit);
  }
  
  @Override
  public <T> AsyncProcessFuture<T> submit(AsyncProcess<T> process) {
    return submit(process, getTimeoutInMillis(), TimeUnit.MILLISECONDS);
  }
  
  @Override
  public <T> AsyncProcessFuture<T> submit(
      AsyncProcess<T> process, long timeout, TimeUnit unit) {
    AsyncProcessRunnableFuture<T> future 
      = newTaskFor(process, timeout, unit);
    execute(future);
    return future;
  }
}