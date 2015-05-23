package com.lh.exin.message;
import android.content.*;
import android.os.*;
import android.widget.*;
import com.lh.exin.account.*;

public class MessageHandler extends Handler
{
	private Context context;
	private TextView tvStatus;
	private AccountLogin login;

	public MessageHandler(Context context, TextView tvStatus)
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
		switch (msg.what)
		{
			case MessageStatus.CONNECT_TIMEOUT:
				tvStatus.setText("连接超时");
				break;
			case MessageStatus.CONNECT_FAILED:
				tvStatus.setText("连接失败(网络断开)");
				break;
			case MessageStatus.CREAT_WAN_DIALOG:
				login.showWanInfo((String[])msg.obj);
				break;
			case MessageStatus.CANNOT_CONNECT_ROUT:
				tvStatus.setText("未知");
				break;
			default:
				tvStatus.setText("未知错误");
		}
		super.handleMessage(msg);
	}
}
