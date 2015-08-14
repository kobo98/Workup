package com.app2youth.hackaton.Workup1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class IncomingSms extends BroadcastReceiver {

    static boolean received=false;

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);
                    if (message.equals(""+RegistrationDetailsActivity.code)) {
                        received = true;

                        // Show Alert
	                    /*
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,
                                "SMS Authorized with code: " + message, duration);
                        toast.show();
                        */
                    }
                    else{

                        // Show Alert
	                    /*
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,
                                "SMS NOT Authorized!", duration);
                        toast.show();
                        */
                    }


                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }
}
