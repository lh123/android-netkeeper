package com.lh.exin.message;
import android.content.*;
import android.os.*;
import android.widget.*;
import com.lh.exin.account.*;

public class MessageHandler extends Handler
{
	private Context context;
	private TextView tvStatus;
	private Rout login;

	public MessageHandler(Context context, TextView tvStatus)
	{
		this.context = context;
		this.tvStatus = tvStatus;
	}
	public void setLogin(Rout login)
	{
		this.login=login;
	}
	@Override
	public void handleMessage(Message msg)
	{
		switch (msg.what)
		{
			case MessageStatus.CREAT_WAN_DIALOG:
				login.closeWaitingDialog(1);
				login.showWanInfo((String[])msg.obj);
				break;
			case MessageStatus.CANNOT_CONNECT_ROUT:
				login.closeWaitingDialog(2);
				tvStatus.setText("无法连接路由器");
				break;
			case MessageStatus.SHOW_WAITING_DIALIG:
				login.showWaitingDialog((String)msg.obj);
				break;
			case MessageStatus.CLOSE_WAITING_DIALOG:
				login.closeWaitingDialog(1);
				break;
			case MessageStatus.RESTART_ROUT_SUCCESS:
				login.closeWaitingDialog(3);
				break;
			default:
				tvStatus.setText("未知错误");
		}
		super.handleMessage(msg);
	}
}
