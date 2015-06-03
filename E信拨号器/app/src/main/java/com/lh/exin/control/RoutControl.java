package com.lh.exin.control;
import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import com.lh.exin.account.*;
import com.lh.exin.message.*;
import com.lh.exin.routdata.*;
import com.umeng.analytics.*;
import java.util.*;

public class RoutControl
{
	private EditText edExAccount,edExPassword,edRoutAccount,edRoutPassword,edRoutIp;
	private MessageHandler handler;
	private TrackHandler thandler;
	private Rout login;
	private TextView tvStatus;
	private Context context;
	private boolean pauseTrack=false;
	private ArrayList<DrivesInfo> array;

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
		RoutData.routControl = this;
	}

	public void setRoutControl()
	{
		readEdittext();
		handler = new MessageHandler(context, tvStatus);
		thandler = new TrackHandler(context, tvStatus);
		login = new Rout(context);
		login.setHandler(handler, thandler);
		handler.setLogin(login);
	}
	public void login()
	{
		readEdittext();
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					System.out.println("检查lock");
					pauseTrack = true;
					handler.sendMessage(handler.obtainMessage(MessageStatus.SHOW_WAITING_DIALIG, "正在连接路由器"));
					synchronized (login)
					{
						System.out.println("获得lock");
						login.setTrackTime(1000);
						long current=System.currentTimeMillis();
						login.login();
						MobclickAgent.onEventDuration(context, "登录", System.currentTimeMillis() - current);
						pauseTrack = false;
						System.out.println("唤醒track");
						login.setTrackTime(2500);
						login.notify();
					}
				}
			}).start();
	}
	public void getWanInfo()
	{
		readEdittext();
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					pauseTrack = true;
					long current=System.currentTimeMillis();
					handler.sendMessage(handler.obtainMessage(MessageStatus.SHOW_WAITING_DIALIG, "正在连接路由器"));
					synchronized (login)
					{
						String[] backInfo= login.getWanInfo();
						MobclickAgent.onEventDuration(context, "获取WAN信息", System.currentTimeMillis() - current);
						if (backInfo != null)
						{
							handler.sendMessage(handler.obtainMessage(MessageStatus.CREAT_WAN_DIALOG, backInfo));
						}
						pauseTrack = false;
						login.notifyAll();
						System.out.println("唤醒");
					}
				}
			}).start();
	}

	public void getStatus()
	{
		readEdittext();
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

	public void restartRout()
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					pauseTrack = true;
					long current=System.currentTimeMillis();
					handler.sendMessage(handler.obtainMessage(MessageStatus.SHOW_WAITING_DIALIG, "正在尝试重启路由器"));
					synchronized (login)
					{
						login.restartRout();
						pauseTrack = false;
						login.notifyAll();
						System.out.println("唤醒");
						MobclickAgent.onEventDuration(context, "重启路由器", System.currentTimeMillis() - current);
					}
				}
			}).start();
	}

	public ArrayList<DrivesInfo> getDrivesList()
	{
		Thread thread=new Thread(new Runnable(){

				@Override
				public void run()
				{
					pauseTrack = true;
					long current=System.currentTimeMillis();
					synchronized (login)
					{
						array = login.getDrivesList();
						pauseTrack = false;
						login.notifyAll();
						System.out.println("唤醒");
						MobclickAgent.onEventDuration(context, "获取设备列表", System.currentTimeMillis() - current);
					}
				}
			});

		thread.start();
		try
		{
			thread.join();
		}
		catch (InterruptedException e)
		{
			return null;
		}
		return array;
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
