package com.app2youth.hackaton.Workup1;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
/*
public class PushService extends IntentService {

	public static boolean pushRunning=false;
	private BasicClass bc;

	private AlarmManager alarmManager;
	public long count=0;
	private boolean started;
	private PendingIntent pendingIntent;
	private static final long delay = 1000*30;
	public PushService(BasicClass bc) {
		super(PushService.class.getSimpleName());
		this.bc=bc;
		alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (! started) {
			started = true;

			// Call the service periodically every 15 minutes
			pendingIntent = PendingIntent.getService(
					getApplicationContext(),
					0,
					intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			alarmManager.setRepeating(
					AlarmManager.ELAPSED_REALTIME,
					delay,
					delay,
					pendingIntent);
		}





	}
}
*/



public class PushService extends Service {
	private static final String TAG = PushService.class.getSimpleName();
	private static final long UPDATE_INTERVAL = 1 * 20 *1000;
	private static final long DELAY_INTERVAL = 0;

	private Timer timer;

	public PushService() {
	}

	public void onCreate() {
		Log.d(TAG, "STARTING SERVICE");

		super.onCreate();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		timer = new Timer();
		timer.scheduleAtFixedRate(
				new TimerTask() {
					public void run() {
						String[] notifications = new String[]{"", ""};
						try {
							if (SQL.pushStatemenet == null) {
								Log.d("SHIT", "App closed, restarting service");
								restartSQLConnection();

								//SQL.start();
							}

							Log.d("SHIT", "Reading. id: " + getInt("id") + ", species: " + getString("species"));
							notifications = Controller.checkForNotifications(getInt("id"), getString("species").equals("t"));

						} catch (SQLException e) {
							Log.d("SHIT", "Error: Restarting connection");
							restartSQLConnection();

							//SQL.close();
							//SQL.start();

						} finally {

						}

						if (!notifications[0].equals("") && !notifications[1].equals("")) {
							pushNotification(notifications[0], notifications[1], SplashActivity.class, SplashActivity.class, 0);
						}
					}
				},
				DELAY_INTERVAL,
				UPDATE_INTERVAL
		);

		super.onStartCommand(intent, flags, startId);

		return 0;
	}

	@Override
	public void onDestroy() {
		timer.cancel();

		super.onDestroy();
	}

	private void restartSQLConnection(){
		if (isInternetAvailable()){
			SQL.start();
		}
		else{
			Log.d("SHIT", "Could not connect to SQL, no internet connection.");
		}
	}

	private boolean isInternetAvailable() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	public Boolean isInternetAvailableAlternative() {
		try {
			Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1    www.google.com");
			int returnVal = p1.waitFor();
			boolean reachable = (returnVal==0);
			if(reachable){
				System.out.println("Internet access");
				return reachable;
			}
			else{
				System.out.println("No Internet access");
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return false;
	}

	public int getInt(String name) {
		SharedPreferences mPreferences = getSharedPreferences("WorkUp", MODE_PRIVATE);
		return mPreferences.getInt(name, -1);
	}

	public String getString(String name) {
		SharedPreferences mPreferences = getSharedPreferences("WorkUp", MODE_PRIVATE);
		return mPreferences.getString(name, "");
	}

	public void pushNotification(String title, String text, Class whereToBeTransferred, Class stackClass, int idOfNotification) {
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle(title)
						.setContentText(text);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, whereToBeTransferred);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(stackClass);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
				);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// id allows you to update the notification later on.
		mNotificationManager.notify(idOfNotification, mBuilder.build());

	}

}


