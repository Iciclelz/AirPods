package iciclez.airpods;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;

public class BluetoothReceiver extends BroadcastReceiver {

    private boolean connected;
    private AirPodsService service;

    public BluetoothReceiver(AirPodsService service, BluetoothAdapter bluetoothAdapter, Context context)
    {
        this.service = service;

        bluetoothAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener()
        {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.HEADSET)
                {
                    for (BluetoothDevice device : proxy.getConnectedDevices())
                    {
                        if (isAirPodsUuid(device))
                        {
                            connected = true;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onServiceDisconnected(int profile)
            {
                if (profile == BluetoothProfile.HEADSET)
                {
                    connected = false;
                }

            }
        }, BluetoothProfile.HEADSET);

        if (bluetoothAdapter.isEnabled())
        {
            service.startAirPodsService();
        }
    }


    private boolean isAirPodsUuid(BluetoothDevice bluetoothDevice){
        ParcelUuid[] AIRPODS_UUIDS= {
                ParcelUuid.fromString("74ec2172-0bad-4d01-8f77-997b2be0722a"),
                ParcelUuid.fromString("2a72e02b-7b99-778f-014d-ad0b7221ec74")
        };

        ParcelUuid[] uuids = bluetoothDevice.getUuids();
        if (uuids == null || uuids.length == 0)
        {
            return false;
        }

        for (int i = 0; i < uuids.length; ++i)
        {
            if (uuids[i].equals(AIRPODS_UUIDS[0]) || uuids[i].equals(AIRPODS_UUIDS[1]))
            {
                return true;
            }
        }

        return false;
    }

    public boolean isConnected()
    {
        return connected;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        BluetoothDevice bluetoothDevice = intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
        {
            switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR))
            {
                case BluetoothAdapter.STATE_OFF:
                case BluetoothAdapter.STATE_TURNING_OFF:
                    service.stopAirPodsService();
                    //recentBeacons = clear
                    break;

                case BluetoothAdapter.STATE_ON:
                    service.startAirPodsService();
                    break;
            }
        }

        if (bluetoothDevice != null && action != null && !action.isEmpty() && isAirPodsUuid(bluetoothDevice))
        {
            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED))
            {
                connected = true;
            }
            if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                    || action.equals(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED))
            {
                connected = false;
                //clear beacon
            }
        }
    }
}
