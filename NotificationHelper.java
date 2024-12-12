public class NotificationHelper {
    private static final String CHANNEL_ID = "high_priority_channel";
    private static final String CHANNEL_NAME = "High Priority Notifications";
    private static final int NOTIFICATION_ID = 1;

    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel untuk notifikasi prioritas tinggi");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void showNotification(String title, String message) {
        // Buat pending intent untuk tap notifikasi
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Buat action button intent
        Intent actionIntent = new Intent(context, NotificationActionReceiver.class);
        actionIntent.setAction("ACTION_BUTTON_CLICKED");
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build notifikasi
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_action, "Action", actionPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        // Show notifikasi
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, 
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}

// Broadcast Receiver untuk handle action button
public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("ACTION_BUTTON_CLICKED".equals(intent.getAction())) {
            Toast.makeText(context, "Action button clicked", Toast.LENGTH_SHORT).show();
        }
    }
}
