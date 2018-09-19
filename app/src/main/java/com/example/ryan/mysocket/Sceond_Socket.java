package com.example.ryan.mysocket;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Sceond_Socket extends AppCompatActivity implements ClientConnector.ConnectLinstener {

    private EditText mAuthDevice;
    private EditText mReceiveDevice;
    private EditText mSendingMsg;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private ClientConnector mConnector;
    private ListView listView;

    //genymotion连本地网络是10.0.3.2，IDE自带的模拟器是10.0.0.2
    private String mDstName = "10.0.3.2";
    private int mDstPort = 9999;
    private ArrayAdapter mAdapter;
    private List<String> mData;
    private static final String TAG = "Sceond_Socket";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sceond__socket);

        initView();
        initData();
    }

    private void initData() {
        mHandlerThread = new HandlerThread("Sceond_Socket", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mConnector = new ClientConnector(mDstName, mDstPort);
        mConnector.setOnConnectLinstener(this);
        mData = new ArrayList<>();
        mAdapter = new ArrayAdapter(this, R.layout.item, R.id.tv_text, mData);
        listView.setAdapter(mAdapter);
    }

    private void initView() {
        mAuthDevice = findViewById(R.id.et_auth_device);
        mReceiveDevice = findViewById(R.id.et_receive_device);
        mSendingMsg = findViewById(R.id.et_send_message);
        listView = findViewById(R.id.listView);

        Button btn_auth_device  = findViewById(R.id.btn_auth_device);

        btn_auth_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String auth = mAuthDevice.getText().toString().trim();
                if (TextUtils.isEmpty(auth)) {
                    Toast.makeText(Sceond_Socket.this, "认证信息不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    mConnector.auth(auth);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void connectServer(View view) {

        mHandler.post(new ConnectRunnable());
    }

    @Override
    public void onReceiveData(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mData.add(data);
                mAdapter.notifyDataSetChanged();
//                Toast.makeText(MainActivity.this, "data=" + data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class ConnectRunnable implements Runnable {


        @Override
        public void run() {
            try {
                Log.d(TAG, "run: ");
                mConnector.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void authClient(View view) {


    }

    public void sendMessage(View view) {
        String receiver = mReceiveDevice.getText().toString().trim();
        String msg = mSendingMsg.getText().toString().trim();
        if (TextUtils.isEmpty(msg) || TextUtils.isEmpty(receiver)) {
            Toast.makeText(this, "信息不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
//            mData.add(msg);
            mConnector.send(receiver, msg);
//            mAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnctServer(View view) {
        try {
            mConnector.disConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
