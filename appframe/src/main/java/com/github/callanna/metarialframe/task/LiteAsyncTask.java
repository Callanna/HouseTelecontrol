package com.github.callanna.metarialframe.task;

import android.content.Context;
import android.os.SystemClock;

import com.github.callanna.metarialframe.util.LogUtil;
import com.litesuits.android.async.AsyncTask;
import com.litesuits.android.async.SimpleCachedTask;
import com.litesuits.android.async.SimpleTask;
import com.litesuits.android.async.TaskExecutor;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Callanna on 2015/12/18.
 */
public class LiteAsyncTask {
    private static LiteAsyncTask instance;
    private Context mContext;

    private LiteAsyncTask(Context context){
        mContext = context;
    }

    public static LiteAsyncTask getInstance(Context context){
        if(instance == null){
           instance= new LiteAsyncTask(context);
        }
        return instance;
    }
    public static  void executeSync(Runnable runnable){
        AsyncTask.execute(runnable);
    }
    public static void executeBigSync(final Runnable runnable,int count){
        // 较大量并发 execute allowing loss
        for (int i = 0; i < count; i++) {
            final int j = i;
            AsyncTask.executeAllowingLoss(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d("duanyl========> sync count " + j);
                    runnable.run();
                }
            });
        }
    }
    public static void executeAsync(final Runnable runnable ){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                 runnable.run();
                return null;
            }
        };
        task.execute();
    }
    public static  void executeBigAsync(final Runnable runnable,int count){
        // 较大量并发 execute allowing loss
        for (int i = 0; i < count; i++) {
            final int j = i;
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                   runnable.run();
                    return null;
                }
            };
            task.executeAllowingLoss();
        }
    }
    //Cached Task 在不需要频繁刷新数据的场景，正确实用它，可极大减轻服务器压力
    public void executeCachedAsync(final Runnable runnable){
    new SimpleCachedTask<String>(mContext, "getUserInfo", 10, TimeUnit.SECONDS) {
        @Override
        protected String doConnectNetwork() {
            // execute...!
             runnable.run();
            return "";
        }
     }.execute();
    }

    public void executeAfterTimeCancel(final Runnable runnable,int aftertime){
        SimpleTask<Integer> st = new SimpleTask<Integer>() {

            @Override
            protected Integer doInBackground() {
                runnable.run();
                return 0;
            }

            @Override
            protected void onCancelled() {
            }

            @Override
            protected void onPostExecute(Integer result) {
            }
        };
        st.execute();
        SystemClock.sleep(aftertime);
        st.cancel(true);
    }

    public void executeOrdered(LinkedList<SimpleTask> simpleTasks,SimpleTask lastsimpleTask){
        // order: 2-1-last ，按task2-task1-lastTask的顺序执行
        TaskExecutor.OrderedTaskExecutor taskExecutor= TaskExecutor.newOrderedExecutor();
        for(int i = 0;i < simpleTasks.size(); i++) {
            taskExecutor.put(simpleTasks.get(1));
        }
        taskExecutor.put(lastsimpleTask).start();
    }

    public void executeCyclicBarrier(LinkedList<SimpleTask> simpleTasks,SimpleTask destsimpleTask){
       // 123并发执行，全部完成后，执行lastTask。
        TaskExecutor.CyclicBarrierExecutor taskExecutor= TaskExecutor.newCyclicBarrierExecutor();
        for(int i = 0;i < simpleTasks.size(); i++) {
            taskExecutor.put(simpleTasks.get(1));
        }
        taskExecutor.start(destsimpleTask);
    }


}
