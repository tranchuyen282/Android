package foodguide.md06.vtca.foodguide.activity;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import foodguide.md06.vtca.foodguide.R;

public class AlarmReceiver extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) return;
        if (intent.getAction().equals("FOO_ACTION")) {
            String foo = intent.getStringExtra("KEY_FOO_STRING");
            //Toast.makeText(context, foo, Toast.LENGTH_SHORT).show();
            createNotification(context, foo);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void createNotification(Context mContext, String title) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext);
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        Intent ii = new Intent(mContext.getApplicationContext(), CalendarActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.setBigContentTitle("Đã đến giờ nấu ăn !");
        //bigText.setSummaryText("Thông báo");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(mContext.getString(R.string.app_name));
        mBuilder.setContentText(title);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager
                = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


        if (mNotificationManager != null) {
            mNotificationManager.notify(0, mBuilder.build());
        }
    }
}
