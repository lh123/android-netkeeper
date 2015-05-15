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
import com.lh.exin.routinfospace.*;

public class MainActivity extends Activity
{
    private EditText edExAccount,edExPassword,edRoutAccount,edRoutPassword,edRoutIp;
	private Button btnSet,btnInfo,btnAdvance,btnUpdate;
	private TextView tvStatus;
	private CheckBox cbSave;
	private RoutControl routControl; 
	private UpdateControl updateControl;
	private RoutInfoSpace routInfo;
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
		updateControl=new UpdateControl(this);
		routControl=new RoutControl(this,edExAccount, edExPassword, edRoutAccount, edRoutPassword, edRoutIp, tvStatus);
		routInfo=new RoutInfoSpace(cbSave, edExAccount, edExPassword, edRoutAccount, edRoutPassword, edRoutIp, this);
		updateControl.startUpdate();
		routInfo.readInfo();
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
					routInfo.saveInfo();
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
