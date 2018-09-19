package com.example.ryan.mysocket;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by ryan on 18-9-18.
 */

public class ClientConnector {
    private Socket mClient;

    private static final String TAG = "ClientConnector";

        //服务端IP
    private String mDstName;

    //服务端口号
    private int mDesPort;

    private ConnectLinstener mListener;

    public ClientConnector(String mDstName, int mDesPort) {
        this.mDstName = mDstName;
        this.mDesPort = mDesPort;
    }

    /**
     * 与服务端进行连接
     *
     * @throws IOException
     */

    public void connect() throws IOException{

        Log.d(TAG, "connect: ");

        if (mClient == null){

            mClient = new Socket(mDstName,mDesPort);
            Log.d(TAG, "connect: 我创建了 mClient");

        }

        //获取娶她客户端发送来的数据
        InputStream in = mClient.getInputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = in.read(buffer)) != -1){
            String data = new String(buffer, 0 ,len);
            if (mListener != null){
                mListener.onReceiveData(data);
            }
        }
    }

    /**
     * 认证方法，这个方法是用来进行客户端一对一发送消息的
     * 在实际项目中进行即时通讯时都需要进行登录，这里就是
     * 模拟客户端的账号
     *
     * @param authName
     */

    public void auth(String authName) throws IOException{
        if (mClient !=null){
            //将客户端账号发送给服务端，让服务端保存
            Log.d(TAG, "auth: ");
            OutputStream outputStream = mClient.getOutputStream();
            //模拟认证格式，以#开头
            outputStream.write(("#" + authName).getBytes());
        }
    }

    /**
     * 将数据发送给指定的接收者
     *
     * @param receiver 信息接数者
     * @param data     需要发送的内容
     */

    public void send(String receiver , String data) throws IOException{
        OutputStream outputStream = mClient.getOutputStream();

        outputStream.write((receiver + "#" + data).getBytes());
    }






    /**
     * 断开连接
     *
     * @throws IOException
     */
    public void disConnect() throws IOException{
        if (mClient != null){
            mClient.close();
            mClient = null;
        }
    }

    public void setOnConnectLinstener(ConnectLinstener linstener){
        this.mListener = linstener;
    }

    /**
     * 数据接收回调接口
     */

    public interface ConnectLinstener{
        void onReceiveData(String data);
    }
}
