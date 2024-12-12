public class MainActivity extends AppCompatActivity {
    private static final int MSG_REGISTER_CLIENT = 1;
    private static final int MSG_SEND_TO_SERVICE = 2;
    private static final int MSG_SEND_TO_ACTIVITY = 3;

    private Messenger mService = null;
    private boolean mBound = false;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    private EditText messageEditText;
    private TextView responseTextView;

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEND_TO_ACTIVITY:
                    String response = msg.getData().getString("response");
                    // Update UI dengan response dari service
                    responseTextView.setText(response);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;

            // Daftarkan Activity sebagai client
            try {
                Message msg = Message.obtain(null, MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageEditText = findViewById(R.id.messageEditText);
        responseTextView = findViewById(R.id.responseTextView);

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToService();
            }
        });
    }

    private void sendMessageToService() {
        if (!mBound) return;

        String messageText = messageEditText.getText().toString();
        try {
            Message msg = Message.obtain(null, MSG_SEND_TO_SERVICE);
            Bundle bundle = new Bundle();
            bundle.putString("message", messageText);
            msg.setData(bundle);
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind ke MessengerService
        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind dari service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
}
