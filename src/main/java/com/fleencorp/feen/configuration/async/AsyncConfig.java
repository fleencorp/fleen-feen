package com.fleencorp.feen.configuration.async;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration class to enable asynchronous method execution.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 *
 * @see <a href="https://velog.io/@hyewon0218/%EB%B9%84%EB%8F%99%EA%B8%B0-%EC%B2%98%EB%A6%AC">
 *   Asynchronous processing</a>
 * @see <a href="">https://velog.io/@hyewon0218/%EB%A9%80%ED%8B%B0-%EC%8A%A4%EB%A0%88%EB%93%9C%EB%9E%80
 *   [Operating System] What is multi-threading?</a>
 * @see <a href="https://velog.io/@mk020/Spring-%EC%9D%B4%EB%A9%94%EC%9D%BC-%EC%9D%B8%EC%A6%9D-%EA%B8%B0%EB%8A%A5-%EB%8F%99%EC%8B%9C%EC%97%90-%EC%97%AC%EB%9F%AC-%EC%9C%A0%EC%A0%80%EC%97%90%EA%B2%8C-%EC%9D%B4%EB%A9%94%EC%9D%BC-%EC%9D%B8%EC%A6%9D-%EC%9A%94%EC%B2%AD%EC%9D%B4-%EB%93%A4%EC%96%B4%EC%98%A8%EB%8B%A4%EB%A9%B4-Async-%EB%B9%84%EB%8F%99%EA%B8%B0-%EC%B2%98%EB%A6%AC">
 *   [Spring] Email authentication feature: What if email authentication requests come in from multiple users at the same time? +@Async Asynchronous processing</a>
 */
@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncConfig implements AsyncConfigurer {

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return AsyncConfigurer.super.getAsyncUncaughtExceptionHandler();
  }

  /**
   * Configures and provides an {@link Executor} bean named "mailExecutor" for asynchronous processing.
   *
   * <p>This method sets up a {@link ThreadPoolTaskExecutor} with specific parameters to manage
   * background tasks efficiently. The executor is particularly useful for tasks like sending
   * email verifications, where the main application should not be blocked while waiting
   * for these tasks to complete.</p>
   *
   * <p>Scenario Explanation:</p>
   * <p>Imagine you are running a bakery and while baking a cake, you also need to send
   * out invitations for a party. If you, the baker, stop baking every time you send
   * an invitation, it would slow things down. Instead, you hire a few assistants (workers)
   * to send out the invitations. Now, you can keep baking cakes while your assistants
   * handle the invitations.</p>
   *
   * <p>In the same way, this method sets up a team of background workers to handle email
   * verification tasks, allowing your main program to continue running smoothly without
   * waiting for each email to be sent.</p>
   *
   * <p><b>Configuration Details:</b></p>
   * <ul>
   * <li><b>Core Pool Size:</b> 2 - Always have at least 2 workers ready to handle tasks.</li>
   * <li><b>Max Pool Size:</b> 5 - Can scale up to 5 workers if needed.</li>
   * <li><b>Queue Capacity:</b> 10 - Can hold up to 10 tasks waiting to be handled if all workers are busy.</li>
   * <li><b>Thread Name Prefix:</b> "Async Executor-" - Each worker thread will have a name starting with this prefix for easier identification.</li>
   * </ul>
   *
   * <p><b>Behavior When Queue is Full:</b></p>
   * <p>If all workers are busy and the queue of 10 tasks is full, new tasks will not be
   * accepted until a space is freed up in the queue. This ensures that the system does
   * not get overwhelmed and maintains a manageable load.</p>
   *
   * @return the configured {@link Executor} instance for managing asynchronous tasks.
   */
  @Override
  @Bean()
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(50);
    executor.setThreadNamePrefix("async-thread-");
    executor.initialize();
    return executor;
  }

}
