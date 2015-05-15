package com.lh.exin;

import android.app.*;
import android.content.*;
import android.os.*;
import android.telephony.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.lh.exin.control.*;
import com.lh.exin.message.*;
import com.lh.exin.routdata.*;
import java.io.*;
import org.json.*;

public class MainActivity extends Activity
{
    private EditText edExAccount,edExPassword,edRoutAccount,edRoutPassword,edRoutIp;
	private Button btnSet,btnInfo,btnAdvance,btnUpdate;
	private TextView tvStatus;
	private CheckBox cbSave;
	private RoutControl routControl; 
	private UpdateControl updateControl;
	private MessageHandler handler;
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
		btnAdvance=(Button) findViewById(R.id.but_advance);
		btnUpdate=(Button) findViewById(R.id.btn_update);
		tvStatus = (TextView) findViewById(R.id.tv_status);
		cbSave = (CheckBox) findViewById(R.id.cb_save);
		handler=new MessageHandler(this,tvStatus);
		updateControl=new UpdateControl(this);
		routControl=new RoutControl(this,edExAccount, edExPassword, edRoutAccount, edRoutPassword, edRoutIp, tvStatus);
		updateControl.startUpdate();
		readSavedInfo();
		btnUpdate.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					updateControl.startUpdate();
				}
			});
			
		btnAdvance.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					routControl.readEdittext();
					Intent i=new Intent();
					i.setClass(MainActivity.this,AdvanceFunctionActivity.class);
					startActivity(i);
				}
			});
		btnSet.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (checkIMEI()==true)
					{
						routControl.login();
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
					routControl.getWanInfo();
				}
			});
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
				js.put("exaccount", RoutInfo.exAccount);
				js.put("expassword", RoutInfo.exPassword);
				js.put("routip", RoutInfo.rIp);
				js.put("routaccount", RoutInfo.rAccount);
				js.put("routpassword", RoutInfo.rPassword);
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
