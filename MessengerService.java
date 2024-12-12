public class MessengerService extends Service {
    private static final int MSG_REGISTER_CLIENT = 1;
    private static final int MSG_SEND_TO_SERVICE = 2;
    private static final int MSG_SEND_TO_ACTIVITY = 3;

    // Target untuk menerima pesan dari client
    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    
    // List untuk menyimpan client yang terhubung
    private ArrayList<Messenger> mClients = new ArrayList<>();

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    // Client mendaftar untuk menerima pesan
                    mClients.add(msg.replyTo);
                    break;
                    
                case MSG_SEND_TO_SERVICE:
                    // Terima pesan dari Activity
                    String messageFromActivity = msg.getData().getString("message");
                    // Proses pesan
                    String response = "Service menerima: " + messageFromActivity;
                    
                    // Kirim balasan ke semua client yang terdaftar
                    for (Messenger client : mClients) {
                        try {
                            Message replyMsg = Message.obtain(null, MSG_SEND_TO_ACTIVITY);
                            Bundle bundle = new Bundle();
                            bundle.putString("response", response);
                            replyMsg.setData(bundle);
                            client.send(replyMsg);
                        } catch (RemoteException e) {
                            mClients.remove(client);
                        }
                    }
                    break;
                    
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}