package com.cart.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientManager {
    private static ClientManager instance;
    private ExecutorService executors;
    private List<ClientTask> clientTasks;
    public static final int DEFAULT_THREAD_POOL_SIZE = 5;
    public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 2;
    //private final AtomicBoolean paused = new AtomicBoolean(false);

    private ClientManager() {
        executors = createExecutor(DEFAULT_THREAD_POOL_SIZE, DEFAULT_THREAD_PRIORITY);
        clientTasks = new ArrayList<>();
    }

    private ExecutorService createExecutor(int threadPoolSize, int threadPriority) {
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingDeque<>();
        return new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, taskQueue, createThreadFactory(threadPriority, "cart-pool-"));
    }

    private ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
        return new DefaultThreadFactory(threadPriority, threadNamePrefix);
    }

    public static ClientManager getInstance() {
        if (instance == null) {
            synchronized (ClientManager.class) {
                if (instance == null) {
                    instance = new ClientManager();
                }
            }
        }
        return instance;
    }
//which movement mode
    public void startClient(String host, int port) {
        ClientTask task = new ClientTask(host, port);
        clientTasks.add(task);
        executors.submit(task);
    }

    public void sendData(String data) {
        System.out.println("client task size is "+clientTasks.size());
        for (int index = 0; index < clientTasks.size(); index++) {
            ClientTask clientTask = clientTasks.get(index);
            clientTask.sendData(data);
        }
    }

    public void stopClient() {
        try {
            for (int index = 0; index < clientTasks.size(); index++) {
                ClientTask clientTask = clientTasks.get(index);
                clientTask.stop();
            }
            clientTasks.clear();
            executors.shutdown();
            executors.awaitTermination(1, TimeUnit.MINUTES);
            instance = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            t.setPriority(threadPriority);
            return t;
        }
    }
}



//public class TrackMovmentActivity extends BaseMovementActivity  {
//
//
//    @Override
//    public void initHeadTitle() {
//        title.setText("Track Movement");
//    }
//
//    @Override
//    public void sendStartCommand() {
//        ClientManager.getInstance().sendData("1#");
//    }
//    //if user click on "back" button on left-top, go back to the homeActivity.
//    @Override
//    public void sendStopCommand() {
//        ClientManager.getInstance().sendData("end1#");
//    }
//}
