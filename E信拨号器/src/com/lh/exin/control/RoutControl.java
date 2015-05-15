package com.lh.exin.control;
import android.content.*;
import android.widget.*;
import com.lh.exin.account.*;
import com.lh.exin.message.*;
import com.lh.exin.routdata.*;

public class RoutControl
{
	private EditText edExAccount,edExPassword,edRoutAccount,edRoutPassword,edRoutIp;
	private MessageHandler handler;
	private AccountLogin login;
	private TextView tvStatus;
	private Context context;

	public RoutControl(Context context, EditText edExAccount, EditText edExPassword, EditText edRoutAccount, EditText edRoutPassword, EditText edRoutIp, TextView tvStatus)
	{
		this.context = context;
		this.edExAccount = edExAccount;
		this.edExPassword = edExPassword;
		this.edRoutAccount = edRoutAccount;
		this.edRoutPassword = edRoutPassword;
		this.edRoutIp = edRoutIp;
		this.tvStatus = tvStatus;
	}

	public void setRoutControl()
	{
		readEdittext();
		handler = new MessageHandler(context, tvStatus);
		login = new AccountLogin(RoutData.exRelAccount, RoutData.exPassword);
		login.setRoutInfo(RoutData.rAccount, RoutData.rPassword, RoutData.rIp);
		login.setHandler(handler);
		handler.setLogin(login);
	}
	public void login()
	{
		if (MessageStatus.isLogin == false)
		{
			setRoutControl();
			login.getWanHtml(MessageStatus.WANT_LOGIN);
		}
	}
	public void getWanInfo()
	{
		setRoutControl();
		login.getWanHtml(MessageStatus.WANT_GET_WANINFO);
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
