package com.android.randomchat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class RandomChat extends Activity implements View.OnClickListener {
    //    private final static String BR=System.getProperty("line.separator");
    private static final String TAG = "RandomChat";
    private static final String IP = "192.168.0.4";
    private static final String UTF8 = "UTF8";
    private static final int PORT = 10094;
    private int count = 0;

    private ArrayAdapter<String> listMsgAdapter;
    private ArrayList<String> al;
    private RandomChat current;
    private ListView lv;
    private EditText edtSend;
    private Button btnSend;

    private Socket socket;
    private InputStream in;
    private OutputStream out;

    private final Handler handler = new Handler();
    private String strReceive;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("OnCreate " + count++);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        current = this;

        lv = (ListView) findViewById(R.id.listView);
        // listView Line 그리기 x
        lv.setDivider(null);
        lv.setSelector(R.color.bgAlpha);
        lv.setDrawSelectorOnTop(false);
        setupListView();
        edtSend = (EditText) findViewById(R.id.edtSend);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

        (new Thread() {
            public void run() {
                try {
                    connect(IP, PORT);
                } catch (Exception e) {
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("OnDestroy");
        sendMsg("/quit");
        closeSocket();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("OnStop");
    }

    private void setupListView() {
        al = new ArrayList<String>();
        listMsgAdapter = new ArrayAdapter<String>(this, R.layout.msg, al);
        lv.setAdapter(listMsgAdapter);
    }

    private void connect(String ip, int port) {
        int size;
        byte[] w = new byte[1024];
        strReceive = "";
        try {
            socket = new Socket(ip, port);

            in = socket.getInputStream();
            out = socket.getOutputStream();

            while (socket != null && socket.isConnected()) {
                size = in.read(w);
                if (size <= 0) continue;
                strReceive = new String(w, 0, size, "UTF-8");
                // /quit 메시지를 만나면 종료
                if (strReceive.equals("/quit")) {
                    break;
                }
                handler.post(new Runnable() {
                    public void run() {
                        al.add(strReceive);
                        listMsgAdapter.notifyDataSetChanged();
                    }
                });
            }
        } catch (Exception e) {
            handler.post(new Runnable() {
                public void run() {
                    RandomChat.showDialog(current, "", "통신 에러입니다.");
                }
            });
        }
        finish();
    }

    protected static void showDialog(final Activity activity, String title, String text) {
        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setMessage(text);
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                activity.setResult(Activity.RESULT_OK);
            }
        });
        ad.create();
        ad.show();
    }

    private void showDialogQuit() {
        new AlertDialog.Builder(RandomChat.this)
                .setTitle("Quit Message")
                .setMessage("러브러브 랜덤 채팅을 종료하시겠습니까?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("finish");
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void sendMsg(String str) {
        try {
            final byte[] w = str.getBytes(UTF8);

            (new Thread() {
                public void run() {
                    try {
                        out.write(w);
                        out.flush();
                    } catch (Exception e) {
                    }
                }
            }).start();


        } catch (Exception e) {
            handler.post(new Runnable() {
                public void run() {
                    RandomChat.showDialog(current, "", "통신 에러입니다.");
                }
            });
        }
    }

    public void closeSocket() {
        if (socket != null && socket.isConnected())
            try {
                in.close();
                out.close();
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void onClick(View v) {
        if (v == btnSend) {
            if (socket != null && socket.isConnected()) {
                if (edtSend.getText().toString().equals("/quit")) {
                    handler.post(new Runnable() {
                        public void run() {
                            showDialogQuit();
                        }
                    });
                    return;
                }

                sendMsg(edtSend.getText().toString());
                edtSend.setText("", TextView.BufferType.NORMAL);
            }
        }
    }
}








