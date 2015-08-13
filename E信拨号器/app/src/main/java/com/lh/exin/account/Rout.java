
package com.lh.exin.account;

import android.app.*;
import android.content.*;
import android.util.*;
import android.widget.*;
import com.lh.exin.message.*;
import com.lh.exin.routdata.*;
import java.io.*;
import java.net.*;
import java.util.*;
import com.umeng.analytics.*;
import android.net.*;

public class Rout
{
	private String realAccount;//记录真实账户
	private MessageHandler handle;
	private TrackHandler thandle;
	private Context context;
	private String HTML;
	private ProgressDialog pd;

	public static final int CONNECTION_NOT_CONNECTED=0;
	public static final int CONNECTION_SUCCESS=1;
	public static final int CONNECTION_NO_RESPONSE=4;
	public static final int CONNECTION_AUTHENTICATION_FAILED=3;
	public static final int CONNECTION_UNKNOWN=5;
	public static final int CONNECTION_NOT_CONNECTED_WAN=6;
	public static final int CONNECTION_OPERATION_NO_MODE=10;
	public static final int CONNECTION_OPERATION_EXCEPTION=11;
	public static final int CONNECTION_CONNECTING=2;
	public static final int CONNECTION_CHANGE_MODE = 7;

	public static final String[] PPPoELinkStat={
		"未连接",
		"已连接",
		"正在连接",
		"用户名或密码错误",
		"服务器无响应",
		"未知原因失败",
		"WAN口未连接"
	};

	public Rout(Context context)
	{
		this.context = context;
	}


	public void setHandler(MessageHandler handle, TrackHandler thandle)
	{
		this.handle = handle;
		this.thandle = thandle;
	}
	public HttpURLConnection getConnect(String path) throws MalformedURLException, IOException 
	{
		URL url=new URL(path);
		HttpURLConnection connect=(HttpURLConnection) url.openConnection();
		connect.setRequestProperty(
			"Authorization", getBase64Acc());
		connect.setConnectTimeout(1500);
		connect.connect();
		return connect;
	}
	public String loginPath(String account, String password)//获取post路由器的路径
	{
		String encodeAccount = null,encodePassWord = null;
		try
		{
			encodeAccount = URLEncoder.encode(realAccount, "UTF-8").replace("+", "%20");
			encodePassWord = URLEncoder.encode(password, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			MobclickAgent.reportError(context,e);
		}
		String path="/userRpm/PPPoECfgRpm.htm?wan=0&wantype=2&acc="
			+ encodeAccount
			+ "&psw="
			+ encodePassWord
			+ "&confirm="
			+ encodePassWord
			+ "sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=4&waittime2=0&Connect=%C1%AC+%BD%D3";
		return path;
	}
	public String loginPathAtTime(String account, String password, String startHour, String startMin, String endHour, String endMin)
	{
		String encodeAccount = null,encodePassWord = null;
		try
		{
			encodeAccount = URLEncoder.encode(realAccount, "UTF-8").replace("+", "%20");
			encodePassWord = URLEncoder.encode(password, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{}
		String path ="/userRpm/PPPoECfgRpm.htm?wan=0&wantype=2&acc=" +
			encodeAccount +
			"&psw=" +
			encodePassWord +
			"&confirm=" +
			encodePassWord +
			"&specialDial=0&SecType=0&sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=3&hour1=" +
			startHour +
			"&minute1=" +
			startMin +
			"&hour2=" +
			endHour +
			"&minute2=" +
			endMin +
			"&Save=%B1%A3+%B4%E6";
		return path;
	}

	public void login()
	{
		if (checkWanStatus() == true)
		{
			handle.sendEmptyMessage(MessageStatus.CLOSE_WAITING_DIALOG);
			return;
		}
		try
		{
			if (RoutData.exAccount != null && RoutData.exPassword != null)
			{
				realAccount = AccountController.getRealAccount(RoutData.exAccount);
				HTML = getRespond(getConnect("http://" + RoutData.rIp + loginPath(realAccount, RoutData.exPassword)));
				closeWaitingDialog(1);
				startTrack();
			}
		}
		catch (IOException e)
		{
			handle.sendEmptyMessage(MessageStatus.CANNOT_CONNECT_ROUT);
		}
	}

	public void loginAtTime()
	{

	}

	public String getBase64Acc()
	{
		return "Basic " + Base64.encodeToString((RoutData.rAccount + ":" + RoutData.rPassword).getBytes(), Base64.DEFAULT);
	}


	public String[] getWanInfo()
	{
		try
		{
			HTML = getRespond(getConnect("http://" + RoutData.rIp + "/userRpm/StatusRpm.htm"));
			if (HTML != null)
			{
				String KeywordWAN="var wanPara = new Array(";
				int tIndex=HTML.indexOf(KeywordWAN) + KeywordWAN.length();
				if (tIndex > 0)
				{
					int tEnd=HTML.indexOf(");", tIndex);
					if (tEnd > tIndex)
					{
						String[] wanInfo;
						String tArray=HTML.substring(tIndex, tEnd);
						tArray = tArray.replace("\"", "");
						wanInfo = tArray.split(",");
						return wanInfo;
					}
				}
			}
		}
		catch (IOException e)
		{
			handle.sendEmptyMessage(MessageStatus.CANNOT_CONNECT_ROUT);
		}
		return null;
	}

	public String getRespond(HttpURLConnection connect) throws IOException
	{
		InputStream is=connect.getInputStream();
		InputStreamReader isr=new InputStreamReader(is);
		BufferedReader br=new BufferedReader(isr);
		StringBuffer temp=new StringBuffer();
		HTML = null;
		while ((HTML = br.readLine()) != null)
		{
			temp.append(HTML);
		}
		return temp.toString();
	}

	public void startTrack()
	{
		try
		{
			boolean getData = true;
			int count=0;
			while (getData)
			{
				String[] pppoeInf;
				pppoeInf = getWanInfo();
				if (pppoeInf == null || pppoeInf.length<14)
				{
					Thread.sleep(1000);
					return;
				}
				switch (Integer.parseInt(pppoeInf[14]))
				{
					case CONNECTION_NOT_CONNECTED:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[0]));getData = false;break;
					case CONNECTION_SUCCESS:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[1]));getData = false;RoutData.isLogin = true;break;
					case CONNECTION_CONNECTING:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[2] + " " + count + "次"));count++;System.out.println(count);break;
					case CONNECTION_AUTHENTICATION_FAILED:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[3]));getData = false;break;
					case CONNECTION_NO_RESPONSE:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[4]));getData = false;break;
					case CONNECTION_UNKNOWN:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[5]));getData = false;break;
					case CONNECTION_NOT_CONNECTED_WAN:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[6]));getData = false;break;
					default:break;
				}
				if(count>10)
				{
					thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS,"连接超时"));
					return;
				}
				Thread.sleep(1000);
			}
		}
		catch (InterruptedException e)
		{
			Log.e("Thread", "Sleep Fail");
		}
	}

	public boolean checkWanStatus()
	{
		String[] pppoeInf;
		pppoeInf = getWanInfo();
		if (pppoeInf == null)
			return false;
		switch (Integer.parseInt(pppoeInf[14]))
		{
			case CONNECTION_NOT_CONNECTED:RoutData.isLogin = false;break;
			case CONNECTION_SUCCESS:RoutData.isLogin = true;break;
			case CONNECTION_CONNECTING:RoutData.isLogin = false;break;
			case CONNECTION_AUTHENTICATION_FAILED:RoutData.isLogin = false;break;
			case CONNECTION_NO_RESPONSE:RoutData.isLogin = false;break;
			case CONNECTION_UNKNOWN:RoutData.isLogin = false;break;
			case CONNECTION_NOT_CONNECTED_WAN:RoutData.isLogin = false;break;
			default:RoutData.isLogin = false;break;
		}
		return RoutData.isLogin;
	}
	
	public Object[] getDrivesList()
	{
		ArrayList<DrivesInfo> drivesArray = null;
		Object[] o=new Object[2];
		o[0]=true;
		try
		{
			HTML = getRespond(getConnect("http://" + RoutData.rIp + "/userRpm/AssignedIpAddrListRpm.htm"));
			String keyWords="var DHCPDynList = new Array(";
			int index=HTML.indexOf(keyWords)+keyWords.length();
			if(index>0)
			{
				int indexEnd=HTML.indexOf(");",index);
				if(indexEnd>index)
				{
					String[] drivesList;
					String[] single=new String[4];
					DrivesInfo drivesInfo;
					String info=HTML.substring(index,indexEnd);
					info=info.replaceAll("\"","");
					drivesList=info.split(",");
					drivesArray=new ArrayList<DrivesInfo>();
					for(int i=0;i<drivesList.length/4;i++)
					{
						drivesInfo=new DrivesInfo();
						for(int j=0;j<4;j++)
						{
							single[j]=drivesList[i*4+j];
						}
						drivesInfo.setDrivesInfo(single[0],single[1],single[2],single[3]);
						drivesArray.add(drivesInfo);
						o[1]=drivesArray;
					}
				}
			}
		}
		catch (IOException e)
		{
			o[0]=false;
		}
		return o;
	}

	public void showWaitingDialog(String message)
	{
		if (pd == null)
		{
			pd = new ProgressDialog(context);
			pd.setTitle("正在处理");
			pd.setMessage(message);
			pd.setCancelable(false);
			pd.show();
		}
	}

	public void closeWaitingDialog(int what)
	{
		switch (what)
		{
			case 1:
				if (pd != null)
				{
					pd.cancel();
					pd = null;
				}
				break;
			case 2:
				if (pd != null)
				{
					pd.cancel();
					pd = null;
					Toast.makeText(context, "连接失败,请检查网络连接", Toast.LENGTH_SHORT).show();
				}
				break;
			case 3:
				if (pd != null)
				{
					pd.cancel();
					pd = null;
					Toast.makeText(context, "重启指令发送成功", Toast.LENGTH_SHORT).show();
				}
		}
	}
	public void restartRout()
	{
		try
		{
			HTML = getRespond(getConnect("http://" + RoutData.rIp + "/userRpm/SysRebootRpm.htm?Reboot=%D6%D8%C6%F4%C2%B7%D3%C9%C6%F7"));
		}
		catch (IOException e)
		{
			handle.sendEmptyMessage(MessageStatus.CANNOT_CONNECT_ROUT);
		}
		handle.sendEmptyMessage(MessageStatus.RESTART_ROUT_SUCCESS);
	}
	
	public void checkProblem()
	{
		ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager.getActiveNetworkInfo().getType()==ConnectivityManager.TYPE_WIFI)
		{
			
		}
		else
		{
			
		}
	}

	public void showWanInfo(String[] backInfo)
	{
		StringBuffer temp=new StringBuffer();
		temp.append("WAN口连接信息\n---------------------\n");
		temp.append("外网连接状态： " + (backInfo[14].equals("1") ? "已经连接" : "未连接") + "\n");
		temp.append("MAC地址: " + backInfo[1] + "\n");
		temp.append("IP地址: " + backInfo[2] + "\n");
		temp.append("子网掩码： " + backInfo[4] + "\n");
		temp.append("网关地址： " + backInfo[7] + "\n");
		temp.append("主DNS服务器： " + backInfo[11] + "\n"); 
		temp.append("次DNS服务器： " + backInfo[12] + "\n");
		temp.append("在线时间： " + backInfo[13] + "\n");
		temp.append("---------------------");
		new AlertDialog.Builder(context).setTitle("Wan信息").setMessage(temp.toString()).setPositiveButton("确定", null).show();
	}
}
