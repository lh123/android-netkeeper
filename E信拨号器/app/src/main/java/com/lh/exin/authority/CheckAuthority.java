package com.lh.exin.authority;
import android.content.*;
import android.telephony.*;
import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import com.lh.exin.message.*;
import android.app.*;
import java.util.*;

public class CheckAuthority
{
	private static final String path="https://git.oschina.net/lh123/update-service/raw/master/auth.text";
	private AuthorityHandler hander;
	private Context context;

	public CheckAuthority(Context context)
	{
		this.context = context;
		hander=new AuthorityHandler(this,context);
	}
	
	protected HttpsURLConnection getConnect(String path) throws MalformedURLException, IOException
	{
		URL url=new URL(path);
		HttpsURLConnection connect=(HttpsURLConnection) url.openConnection();
		return connect;
	}
	
	protected String[] getList(HttpURLConnection connect) throws IOException
	{
		InputStream is=connect.getInputStream();
		InputStreamReader isr=new InputStreamReader(is);
		BufferedReader br=new BufferedReader(isr);
		String temp="";
		StringBuffer data=new StringBuffer();
		while((temp=br.readLine())!=null)
		{
			data.append(temp);
		}
		temp=data.toString();
		return temp.split(",");
	}
	
	protected String getDeviceId()
	{
		return ((TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE)).getDeviceId();
	}
	
	protected boolean getAuthority(String[] list)
	{
		String deviceId=getDeviceId();
		for(int i=0;i<list.length;i++)
		{
			if(deviceId.equals(list[i]))
			{
				return true;
			}
		}
		return false;
	}
	
	public void startAuthority()
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					try
					{
						HttpURLConnection connect=getConnect(path);
						String[] list=getList(connect);
						if(getAuthority(list))
						{
							hander.sendEmptyMessage(MessageStatus.AUTH_PASS);
						}
					}
					catch (IOException e)
					{
						hander.sendEmptyMessage(MessageStatus.AUTH_CONNECT_FAILED);
					}
				}
			}).start();
	}
	
	public void setAuthStatusPass()
	{
		
		SharedPreferences sp=context.getSharedPreferences("auth_status",0);
		SharedPreferences.Editor editor=sp.edit();
		editor.putBoolean("auth_status",true);
		
		editor.commit();
	}
	public void setAuthStatusFail()
	{
		SharedPreferences sp=context.getSharedPreferences("auth_status",0);
		SharedPreferences.Editor editor=sp.edit();
		editor.putBoolean("auth_status",false);
		editor.commit();
	}
	
	public boolean checkAuthStatus()
	{
		SharedPreferences sp=context.getSharedPreferences("auth_status",0);
		if(sp.getBoolean("auth_status",false))
		{
			return true;
		}
		else
		{
			hander.sendEmptyMessage(MessageStatus.AUTH_FAILED);
			return false;
		}
	}
	public void showDialog(String title,String message)
	{
		new AlertDialog.Builder(context).
		setTitle(title).
		setMessage(message).
			setPositiveButton("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					startAuthority();
				}
			}).
		setNegativeButton("取消",null).show();
	}
}
