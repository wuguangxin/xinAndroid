package com.wuguangxin.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 实现手机摇晃监听器
 * <p>Created by wuguangxin on 15/6/24 </p>
 */
public class SensorManagerHelper implements SensorEventListener {
    // 速度阈值，当摇晃速度达到这值后产生作用
    private static final int SPEED_THRESHOLD = 5000;
    // 两次检测的时间间隔
    private static final int UPDATE_INTERVAL_TIME = 50;
    // 传感器管理器
    private SensorManager sensorManager;
    // 传感器
    private Sensor sensor;
    // 重力感应监听器
    private OnShakeListener onShakeListener;
    // 上下文对象context
    private Context context;
    // 手机上一个位置时重力感应坐标
    private float lastX;
    private float lastY;
    private float lastZ;
    // 上次检测时间
    private long lastUpdateTime;

    // 构造器
    public SensorManagerHelper(Context context) {
        // 获得监听对象
        this.context = context;
        start();
    }

    /**
     * 开始
     */
    public void start() {
        // 获得传感器管理器
        sensorManager = (SensorManager) context.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            // 获得重力传感器
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        // 注册
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    /**
     * 停止检测
     */
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    /**
     * 摇晃监听接口
     */
    public interface OnShakeListener {
        /**
         * 重力感应速度值，不要在该回调中做耗时操作。
         * speed≈10±：当手机放着不动时；
         * speed≈500±：当拿起桌面上的手机时；
         * speed≈5000±：当摇晃手机时；
         *
         * @param speed speed
         */
        void onSpeedChange(double speed);

        void onShake();
    }

    /**
     * 设置重力感应监听器
     *
     * @param listener
     */
    public void setOnShakeListener(OnShakeListener listener) {
        onShakeListener = listener;
    }

    /**
     * (non-Javadoc) android.hardware.SensorEventListener#onAccuracyChanged(android.hardware .Sensor, int)
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * 重力感应器感应获得变化数据 android.hardware.SensorEventListener#onSensorChanged(android.hardware .SensorEvent)
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // 现在检测时间
        long currentUpdateTime = System.currentTimeMillis();
        // 两次检测的时间间隔
        long timeInterval = currentUpdateTime - lastUpdateTime;
        // 判断是否达到了检测时间间隔
		if (timeInterval < UPDATE_INTERVAL_TIME) {
			return;
		}
        // 现在的时间变成last时间
        lastUpdateTime = currentUpdateTime;
        // 获得x,y,z坐标
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        // 获得x,y,z的变化值
        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;
        // 将现在的坐标变成last坐标
        lastX = x;
        lastY = y;
        lastZ = z;
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval * 10000;
        if (onShakeListener != null) {
            onShakeListener.onSpeedChange(speed);
            // 达到速度阀值，发出提示
            if (speed >= SPEED_THRESHOLD) {
                onShakeListener.onShake();
            }
        }
    }
}
