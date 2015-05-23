package com.lh.exin.message;
import android.content.*;
import android.os.*;
import android.widget.*;
import com.lh.exin.account.*;

public class TrackHandler extends Handler
{

	private Context context;
	private TextView tvStatus;
	private AccountLogin login;
	public TrackHandler(Context context, TextView tvStatus)
	{
		this.context = context;
		this.tvStatus = tvStatus;
	}
	public void setLogin(AccountLogin login)
	{
		this.login=login;
	}
	@Override
	public void handleMessage(Message msg)
	{
		switch(msg.what)
		{
			case MessageStatus.SET_VIEW_STATUS:
				tvStatus.setText((String)msg.obj);
				break;
		}
		super.handleMessage(msg);
	}
	
}
