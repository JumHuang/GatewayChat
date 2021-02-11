package com.jumhuang.gatewaychat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    private EditText input;
    private Button submit;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private RecyclerView recycler;
    private ArrayList<Message> list = new ArrayList<Message>();
    private MyAdapter adapter;

    private String clientId =null;

    private Socket socket = null;
    // 获取输出流与输入流
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private Timer timer = new Timer();
    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = (RecyclerView)findViewById(R.id.main_recycler);
        input = (EditText)findViewById(R.id.main_input);
        submit = (Button)findViewById(R.id.main_submit);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        setSupportActionBar(toolbar);

        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //recycler.addItemDecoration(new MyDividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));   //分割线
        recycler.setItemAnimator(new DefaultItemAnimator());

        adapter = new MyAdapter(getApplicationContext(), list, false);
        //adapter.setHasStableIds(true);
        recycler.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });

        submit.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    new Thread(new Runnable() {
                            @Override
                            public void run()
                            {
                                String app_text = "{\"Type\":\"sendToAll\",\"Message\":\"" + input.getText().toString().trim().replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
                                try
                                {
                                    if (app_text.isEmpty())
                                    {
                                        runOnUiThread(new Runnable() {
                                                @Override
                                                public void run()
                                                {
                                                    adapter.addData(new Message(null, null, "输入不能为空～～", false));
                                                }
                                            });
                                    }
                                    else
                                    {
                                        //注意charset.forName 字符编码，utf-8中文。。。。。
                                        if (outputStream != null)
                                        {
                                            byte[] sendData = app_text.getBytes(Charset.forName("utf-8"));
                                            outputStream.write(sendData, 0, sendData.length);
                                            outputStream.flush();
                                        }
                                    }
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                catch (StringIndexOutOfBoundsException e)
                                {
                                    runOnUiThread(new Runnable() {
                                            @Override
                                            public void run()
                                            {
                                                adapter.addData(new Message(null, null, "服务器断开～", false));
                                            }
                                        });
                                }
                            }
                        }).start();
                }
            });

        new Thread(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        socket = new Socket("你的ip", 8282); //ip及端口
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                        if (socket.isConnected())
                        {
                            runOnUiThread(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        adapter.addData(new Message(null, null, "连接成功～", false));
                                    }
                                });
                        }

                        //连接超时
                        //   socket.setSoTimeout(5000);
                        // 获取输入流接收信息
                        while (socket.isConnected() == true)
                        {
                            byte[] buf = new byte[2048];
                            int len = inputStream.read(buf);
                            //注意charset.forName 字符编码，utf-8中文。。。。。
                            final String receData = new String(buf, 0, len, Charset.forName("utf-8"));

                            runOnUiThread(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            JSONObject object=new JSONObject(receData);
                                            if (clientId == null)
                                                clientId = object.getString("ClientId");

                                            boolean type=false;
                                            if (clientId.equals(object.getString("ClientId")))
                                                type = true;
                                            adapter.addData(new Message(null, object.getString("ClientId"), object.getString("Message"), type));
                                            recycler.scrollToPosition(adapter.getItemCount()-1);
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    catch (StringIndexOutOfBoundsException e)
                    {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    adapter.addData(new Message(null, null, "服务器断开～", false));
                                }
                            });
                    }
                }
            }).start();

        sendBeatData();
    }

    /*定时发送数据*/
    private void sendBeatData()
    {
        if (timer == null)
        {
            timer = new Timer();
        }

        if (task == null)
        {
            task = new TimerTask() {
                @Override
                public void run()
                {
                    try
                    {
                        outputStream = socket.getOutputStream();

                        /*这里的编码方式根据你的需求去改*/
                        outputStream.write(("{\"Type\":\"ping\"}").getBytes("utf-8"));
                        outputStream.flush();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            };
        }
        timer.schedule(task, 0, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
