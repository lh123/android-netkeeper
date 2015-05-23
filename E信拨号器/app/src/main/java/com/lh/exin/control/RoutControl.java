package com.lh.exin.control;
import android.app.*;
import android.content.*;
import android.widget.*;
import com.lh.exin.account.*;
import com.lh.exin.message.*;
import com.lh.exin.routdata.*;
import java.util.*;
import java.text.*;

public class RoutControl
{
	private EditText edExAccount,edExPassword,edRoutAccount,edRoutPassword,edRoutIp;
	private MessageHandler handler;
	private TrackHandler thandler;
	private AccountLogin login;
	private TextView tvStatus,tvTime;
	private Context context;

	public RoutControl(Context context, EditText edExAccount, EditText edExPassword, EditText edRoutAccount, EditText edRoutPassword, EditText edRoutIp, TextView tvStatus,TextView tvTime)
	{
		this.context = context;
		this.edExAccount = edExAccount;
		this.edExPassword = edExPassword;
		this.edRoutAccount = edRoutAccount;
		this.edRoutPassword = edRoutPassword;
		this.edRoutIp = edRoutIp;
		this.tvStatus = tvStatus;
		this.tvTime=tvTime;
	}

	public void setRoutControl()
	{
		readEdittext();
		handler = new MessageHandler(context, tvStatus);
		thandler = new TrackHandler(context, tvStatus);
		login = new AccountLogin(RoutData.exRelAccount, RoutData.exPassword,context);
		login.setRoutInfo(RoutData.rAccount, RoutData.rPassword, RoutData.rIp);
		login.setHandler(handler, thandler);
		login.setTimeView(tvTime);
		handler.setLogin(login);
	}
	public void login()
	{
		setRoutControl();
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					login.login();
					login.startTrack();
				}
			}).start();
	}
	public void getWanInfo()
	{		
		setRoutControl();
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					String[] backInfo= login.getWanInfo();
					if(backInfo!=null)
					{
						handler.sendMessage(handler.obtainMessage(MessageStatus.CREAT_WAN_DIALOG,backInfo));
					}
				}
			}).start();
		
	}
	
	public void getStatus()
	{
		setRoutControl();
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					login.startTrack();
					login.getTime();
				}
			}).start();
		
	}

	public void readEdittext()
	{
		RoutData.exAccount = edExAccount.getText().toString();
		RoutData.exRelAccount = AccountController.getRealAccount(RoutData.exAccount);
		RoutData.exPassword = edExPassword.getText().toString();
		RoutData.rAccount = edRoutAccount.getText().toString();
		RoutData.rIp = edRoutIp.getText().toString();
		RoutData.rPassword = edRoutPassword.getText().toString();
	}
}
