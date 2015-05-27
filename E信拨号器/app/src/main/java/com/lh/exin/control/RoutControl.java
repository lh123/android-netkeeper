package com.lh.exin.control;
import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import com.lh.exin.account.*;
import com.lh.exin.message.*;
import com.lh.exin.routdata.*;

public class RoutControl
{
	private EditText edExAccount,edExPassword,edRoutAccount,edRoutPassword,edRoutIp;
	private MessageHandler handler;
	private TrackHandler thandler;
	private ProgressDialog pd;
	private Handler dialogHandler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					pd = new ProgressDialog(context);
					
					pd.setTitle("正在获取");
					pd.setMessage("正在连接到路由器");
					pd.setCancelable(false);
					pd.show();
					break;
				case 1:
					if (pd != null)
					{
						pd.cancel();
					}
			}
		}
	};
	private AccountLogin login;
	private TextView tvStatus;
	private Context context;
	private boolean pauseTrack=false;

	public RoutControl(Context context, EditText edExAccount, EditText edExPassword, EditText edRoutAccount, EditText edRoutPassword, EditText edRoutIp, TextView tvStatus)
	{
		this.context = context;
		this.edExAccount = edExAccount;
		this.edExPassword = edExPassword;
		this.edRoutAccount = edRoutAccount;
		this.edRoutPassword = edRoutPassword;
		this.edRoutIp = edRoutIp;
		this.tvStatus = tvStatus;
		setRoutControl();
	}

	public void setRoutControl()
	{
		readEdittext();
		handler = new MessageHandler(context, tvStatus);
		thandler = new TrackHandler(context, tvStatus);
		login = new AccountLogin(RoutData.exAccount, RoutData.exPassword, context);
		login.setRoutInfo(RoutData.rAccount, RoutData.rPassword, RoutData.rIp);
		login.setHandler(handler, thandler);
		handler.setLogin(login);
	}
	public void login()
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					System.out.println("检查lock");
					pauseTrack = true;
					synchronized (login)
					{
						System.out.println("获得lock");
						login.setTrackTime(1000);
						login.login();
						pauseTrack = false;
						System.out.println("唤醒track");
						login.setTrackTime(2000);
						login.notify();
					}
				}
			}).start();
	}
	public void getWanInfo()
	{		
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					dialogHandler.sendEmptyMessage(0);
					pauseTrack = true;
					synchronized (login)
					{
						String[] backInfo= login.getWanInfo();
						if (backInfo != null)
						{
							handler.sendMessage(handler.obtainMessage(MessageStatus.CREAT_WAN_DIALOG, backInfo));
						}
						dialogHandler.sendEmptyMessage(1);
						pauseTrack = false;
						login.notifyAll();
						System.out.println("唤醒");
					}
				}
			}).start();
	}

	public void getStatus()
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					try
					{
						while (true)
						{
							synchronized (login)
							{
								while (pauseTrack == true)
								{
									System.out.println("停止track");
									login.wait();
								}
								login.startTrack();
								System.out.println("track_____");
							}
						}
					}
					catch (InterruptedException e)
					{}
				}
			}).start();
	}

	public void readEdittext()
	{
		RoutData.exAccount = edExAccount.getText().toString();
		RoutData.exPassword = edExPassword.getText().toString();
		RoutData.rAccount = edRoutAccount.getText().toString();
		RoutData.rIp = edRoutIp.getText().toString();
		RoutData.rPassword = edRoutPassword.getText().toString();
	}
}
