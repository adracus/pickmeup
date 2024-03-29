package com.pickmeupscotty.android.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.pickmeupscotty.android.R;
import com.pickmeupscotty.android.activities.ResponseActivity;
import com.pickmeupscotty.android.amqp.RabbitService;
import com.pickmeupscotty.android.amqp.Subscriber;
import com.pickmeupscotty.android.login.FBWrapper;
import com.pickmeupscotty.android.messages.PickUpRequest;

/**
 * Created by jannis on 07/03/15.
 */
public class NotificationService extends Service {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        RabbitService.subscribe(PickUpRequest.class, new Subscriber<PickUpRequest>() {
            @Override
            public void on(final PickUpRequest request) {
                final PickUpRequest req = request;

                final Context context = getApplicationContext();

                FBWrapper.INSTANCE.getUserId(new FBWrapper.UserIdCallback() {
                    @Override
                    public void onCompleted(String fbid) {
                        //Do not show request send by oneself
                        if (!req.getFacebookId().equals(fbid)) {
                            Intent notificationIntent = new Intent(context, ResponseActivity.class);
                            notificationIntent.putExtra(PickUpRequest.PICK_UP_REQUEST, req);
                            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                                    .setContentTitle("Pick Up Request")
                                    .setContentText("by " + req.getFacebookName())
                                    .setSmallIcon(R.drawable.mister_mustache);
                            notificationBuilder = notificationBuilder.setContentIntent(contentIntent);
                            Notification notification = notificationBuilder.build();
                            notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

                            NotificationManager mNotificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(12, notification);
                        }
                    }
                });
            }
        });
        return Service.START_NOT_STICKY;
    }
}
