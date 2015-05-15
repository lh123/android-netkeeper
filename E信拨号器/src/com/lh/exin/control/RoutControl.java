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
		login = new AccountLogin(RoutInfo.exRelAccount, RoutInfo.exPassword);
		login.setRoutInfo(RoutInfo.rAccount, RoutInfo.rPassword, RoutInfo.rIp);
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
		RoutInfo.exAccount = edExAccount.getText().toString();
		RoutInfo.exRelAccount = AccountController.getRealAccount(RoutInfo.exAccount);
		RoutInfo.exPassword = edExPassword.getText().toString();
		RoutInfo.rAccount = edRoutAccount.getText().toString();
		RoutInfo.rIp = edRoutIp.getText().toString();
		RoutInfo.rPassword = edRoutPassword.getText().toString();
	}
}
