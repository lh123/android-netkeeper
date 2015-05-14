package com.lh.exin;

import android.app.*;
import android.os.*;
import android.telephony.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;
import org.json.*;

public class MainActivity extends Activity
{
    private EditText edExAccount,edExPassword,edRoutAccount,edRoutPassword,edRoutIp;
	private Button btnSet,btnInfo;
	private TextView tvStatus;
	private CheckBox cbSave;
	private MessageHandler handler;
	private AccountLogin login;
	private String exAccount,exRelAccount,exPassword,rAccount,rPassword,rIp;
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
		getActionBar().hide();
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		edExAccount = (EditText) findViewById(R.id.ex_account);
		edExPassword = (EditText) findViewById(R.id.ex_password);
		edRoutAccount = (EditText) findViewById(R.id.rout_account);
		edRoutPassword = (EditText) findViewById(R.id.rout_password);
		edRoutIp = (EditText) findViewById(R.id.rout_ip);
		btnSet = (Button) findViewById(R.id.but_set);
		btnInfo = (Button) findViewById(R.id.but_info);
		tvStatus = (TextView) findViewById(R.id.tv_status);
		cbSave = (CheckBox) findViewById(R.id.cb_save);
		handler = new MessageHandler();
		readSavedInfo();
		//checkIMEI();
		btnSet.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (checkIMEI()==true)
					{
						if(MessageStatus.isLogin==false)
						{
						readEdittext();
						login = new AccountLogin(exRelAccount, exPassword, handler);
						login.setRoutInfo(rAccount, rPassword, rIp);
						//login.login();
						login.getWanHtml();
						checkIsSaved();
						}
					}
					else
					{
						new AlertDialog.Builder(MainActivity.this).setTitle("警告").setMessage("该设备未被授权运行").setPositiveButton("确定",null).show();
					}
				}
			});
		btnInfo.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
						if(login==null)
						{
							readEdittext();
							login = new AccountLogin(exRelAccount, exPassword, handler);
							login.setRoutInfo(rAccount, rPassword, rIp);
						}
						login.getWanInfo();
					}
			});
    }
	public class MessageHandler extends Handler
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case MessageStatus.LOGIN_ING:
					tvStatus.setText("登陆中");
					break;
				case MessageStatus.LOGIN_SUCCESS:
					tvStatus.setText("已连接");
					MessageStatus.isLogin=true;
					break;
				case MessageStatus.LOGIN_FAIL:
					tvStatus.setText("连接失败");
					login.login();
					break;
				case MessageStatus.GET_WIFI_INFO_SUCCESS:
					showWanInfo((String[])msg.obj);
					break;
				case MessageStatus.WAN_SUCCESS:
					tvStatus.setText("已连接");
					MessageStatus.isLogin=true;
					break;
				case MessageStatus.GET_WAN_HTML_SUCCESS:
					login.checkLoginStatus((String)msg.obj);
					break;
				default:
					tvStatus.setText("未知错误");
			}
			super.handleMessage(msg);
		}

	}
	public void checkIsSaved()
	{
		File file =new File("/mnt/sdcard/EXinData.txt");
		if (cbSave.isChecked())
		{
			JSONObject js=new JSONObject();
			String jsString = null;
			try
			{
				js.put("exaccount", exAccount);
				js.put("expassword", exPassword);
				js.put("routip", rIp);
				js.put("routaccount", rAccount);
				js.put("routpassword", rPassword);
				jsString = js.toString();
			}
			catch (JSONException e)
			{}
			try
			{
				FileWriter fw=new FileWriter(file);
				fw.write(jsString);
				fw.close();
			}
			catch (IOException e)
			{}
		}
		else
		{
			if (file.exists())
			{
				file.delete();
			}
		}
	}
	public void readSavedInfo()
	{
		File file=new File("/mnt/sdcard/EXinData.txt");
		if (file.exists())
		{
			StringBuffer jsString=new StringBuffer();
			String temp=null;
			try
			{
				FileInputStream fi=new FileInputStream(file);
				InputStreamReader ir=new InputStreamReader(fi);
				BufferedReader br=new BufferedReader(ir);
				while ((temp = br.readLine()) != null)
				{
					jsString.append(temp);
				}
			}
			catch (IOException e)
			{}
			try
			{
				JSONObject jsobj=new JSONObject(jsString.toString());
				edExAccount.setText((String)jsobj.get("exaccount"));
				edExPassword.setText((String)jsobj.get("expassword"));
				edRoutAccount.setText((String)jsobj.get("routaccount"));
				edRoutIp.setText((String)jsobj.get("routip"));
				edRoutPassword.setText((String)jsobj.get("routpassword"));
			}
			catch (JSONException e)
			{}
		}
	}
	public void readEdittext()
	{
		exAccount = edExAccount.getText().toString();
		exRelAccount = AccountController.getRealAccount(exAccount);
		exPassword = edExPassword.getText().toString();
		rAccount = edRoutAccount.getText().toString();
		rIp = edRoutIp.getText().toString();
		rPassword = edRoutPassword.getText().toString();
	}
	public void showWanInfo(String[] backInfo)
	{
		StringBuffer temp=new StringBuffer();
		temp.append("WAN口连接信息\n---------------------\n");
		temp.append("外网连接状态 ： " + (backInfo[14].equals("1") ? "已经连接" : "未连接") + "\n");
		temp.append("MAC 地址 ; " + backInfo[1] + "\n");
		temp.append("IP 地址 : " + backInfo[2] + "\n");
		temp.append("子网掩码 ： " + backInfo[4] + "\n");
		temp.append("网关地址 ： " + backInfo[7] + "\n");
		temp.append("主DNS服务器 ： " + backInfo[11] + "\n"); 
		temp.append("次DNS服务器 : " + backInfo[12] + "\n");
		temp.append("在线时间 ： " + backInfo[13] + "\n");
		temp.append("---------------------");
		new AlertDialog.Builder(this).setTitle("Wan信息").setMessage(temp.toString()).setPositiveButton("确定", null).show();
	}
	public boolean checkIMEI()
	{
		boolean canRun=false;
		String imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
		System.out.println(imei);
		for (int i=0;i < IMEI.imei.length;i++)
		{
			if (imei.equals(IMEI.imei[i]))
			{
				canRun = true;
				break;
			}
		}
		return canRun;
	}
}
