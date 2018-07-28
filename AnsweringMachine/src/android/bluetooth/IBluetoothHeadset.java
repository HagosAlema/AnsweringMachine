package android.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * System private API for Bluetooth Headset service
 *
 * {@hide}
 */
public interface IBluetoothHeadset {
    int getState();
    BluetoothDevice getCurrentHeadset();
    boolean connectHeadset(BluetoothDevice device);
    void disconnectHeadset();
    boolean isConnected(BluetoothDevice device);
    boolean startVoiceRecognition();
    boolean stopVoiceRecognition();
    boolean setPriority(BluetoothDevice device, int priority);
    int getPriority(BluetoothDevice device);
    int getBatteryUsageHint();
}