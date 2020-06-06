package iciclez.airpods;

import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

//draw overlay gui
//create notification
public class AirPodsService extends Service {

    private AirPodsNotification notification;
    private BluetoothReceiver bluetoothReceiver;
    private BluetoothLeScanner bluetoothLeScanner;

    private List<ScanFilter> getBluetoothLeScanFilter()
    {
        byte[] manufacturerData = new byte[27];
        byte[] manufacturerDataMask = new byte[27];

        manufacturerData[0] = 7;
        manufacturerData[1] = 25;

        manufacturerDataMask[0] = -1;
        manufacturerDataMask[1] = -1;

        ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setManufacturerData(76, manufacturerData, manufacturerDataMask);
        return Collections.singletonList(builder.build());
    }

    public void startAirPodsService()
    {
        //RemoteViews view =new RemoteViews(getPackageName(),R.layout.activity_main);
        //view.setTextViewText(R.id.mainTextView, "startService Called");

        BluetoothAdapter bluetoothAdapter = ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled())
        {
            this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            this.bluetoothLeScanner.startScan(getBluetoothLeScanFilter(),
                    new ScanSettings.Builder().setScanMode(2).setReportDelay(2).build(),
                    new ScanCallback()
                    {
                        @Override
                        public void onBatchScanResults(List<ScanResult> results)
                        {
                            for (ScanResult result : results)
                            {
                                onScanResult(-1, result);
                            }

                            super.onBatchScanResults(results);
                        }

                        @Override
                        public void onScanResult(int callbackType, ScanResult result)
                        {
                            byte[] buffer = result.getScanRecord().getManufacturerSpecificData(76);
                            if (buffer == null || buffer.length != 27 || result.getRssi() < -60)
                            {
                                return;
                            }

                            AirPodsResponse response = new AirPodsResponse(buffer);

                            Log.d("0xdeadbeef",
                                    response.isLeftAirPodCharging() + " " +
                                            response.isRightAirPodCharging() + " " +
                                            response.isCaseCharging() + " " +
                                            response.getLeftAirPodBattery() + " " +
                                            response.getRightAirPodBattery() + " " +
                                            response.getCaseBattery());
                        }
                    });
        }
    }

    public void stopAirPodsService()
    {
        if (this.bluetoothLeScanner != null)
        {
            this.bluetoothLeScanner.stopScan(new ScanCallback(){ });
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");

        bluetoothReceiver = new BluetoothReceiver(this,
                ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter(),
                getApplicationContext());

        try
        {
            registerReceiver(bluetoothReceiver, intentFilter);
        }
        catch(Throwable throwable)
        {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (bluetoothReceiver != null)
        {
            unregisterReceiver(bluetoothReceiver);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (notification == null || !notification.isAlive())
        {
            notification = new AirPodsNotification((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE));
            notification.start();
        }
        return START_STICKY;
    }
}
