package com.lh.exin.routinfospace;
import android.content.*;
import android.widget.*;
import com.lh.exin.account.*;
import com.lh.exin.routdata.*;

public class RoutInfoSpace
{
	private EditText edExAccount,edExPassword,edRoutAccount,edRoutPassword,edRoutIp;
	private CheckBox cbSave;
	private Context context;
	private SharedPreferences sp;
	private String temp="";

	public RoutInfoSpace(CheckBox cbSave, EditText edExAccount, EditText edExPassword, EditText edRoutAccount, EditText edRoutPassword, EditText edRoutIp, Context context)
	{
		this.cbSave = cbSave;
		this.edExAccount = edExAccount;
		this.edExPassword = edExPassword;
		this.edRoutAccount = edRoutAccount;
		this.edRoutPassword = edRoutPassword;
		this.edRoutIp = edRoutIp;
		this.context = context;
	}


	protected void setSharedPreferences()
	{
		sp = context.getSharedPreferences("routinfo", 0);
	}
	public void saveInfo()
	{
		if (cbSave.isChecked())
		{
			readEdittext();
			setSharedPreferences();
			SharedPreferences.Editor editor=sp.edit();
			editor.putString("exist", "true");
			editor.putString("exaccount", RoutData.exAccount);
			editor.putString("expassword", RoutData.exPassword);
			editor.putString("routip", RoutData.rIp);
			editor.putString("routaccount", RoutData.rAccount);
			editor.putString("routpassword", RoutData.rPassword);
			editor.commit();
		}
		else
		{
			clearInfo();
		}
	}
	public void readInfo()
	{
		setSharedPreferences();
		if (sp.contains("exist"))
		{
			edExAccount.setText(sp.getString("exaccount", temp));
			edExPassword.setText(sp.getString("expassword", temp));
			edRoutAccount.setText(sp.getString("routaccount", temp));
			edRoutIp.setText(sp.getString("routip", temp));
			edRoutPassword.setText(sp.getString("routpassword", temp));
		}
	}
	protected void clearInfo()
	{
		sp.edit().clear().commit();
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
