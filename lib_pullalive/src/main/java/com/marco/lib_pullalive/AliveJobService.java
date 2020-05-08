package com.marco.lib_pullalive;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AliveJobService extends JobService {
    private static final String TAG = AliveJobService.class.getSimpleName();
    private static final int PULL_ALIVE = 0x01;
    private JobScheduler jobScheduler;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PULL_ALIVE:
                    Log.d(TAG, "pull alive");
                    jobFinished((JobParameters) msg.obj, true);
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        JobInfo job = initJobInf(startId);
        //提交job到system process中
        if (jobScheduler.schedule(job) <= 0) {
            Log.d(TAG, "Alive job service failed");
        } else {
            Log.d(TAG, "Alive job service success");
        }
        return Service.START_STICKY;
    }

    private JobInfo initJobInf(int startId) {
        JobInfo.Builder builder = new JobInfo.Builder(startId, new ComponentName(getPackageName(), AliveJobService.class.getName()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(true);//低电量模式执行
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //版本大于24
            builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)
                    .setOverrideDeadline(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)
                    .setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR);
        } else {
            builder.setPeriodic(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
        }
        builder.setPersisted(false);//是否持久化
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresCharging(false);//是否充电的时候执行
        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        mHandler.sendMessage(Message.obtain(mHandler, PULL_ALIVE, params));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeCallbacksAndMessages(null);
        return false;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AliveJobService.class);
        context.startService(intent);
    }
}
