package com.lh.exin.account;

import android.app.*;
import android.content.*;
import android.util.*;
import com.lh.exin.message.*;
import com.lh.exin.routdata.*;
import java.io.*;
import java.net.*;
import java.util.*;
import android.os.*;
import android.widget.*;

public class AccountLogin
{
	private String realAccount,account,password,rAccount,rPassword,rIp,startHour,startMin,endHour,endMin;
	private MessageHandler handle;
	private TrackHandler thandle;
	private Context context;
	private String HTML;
	private ProgressDialog pd;
	private long trackTime=2000;

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

	public AccountLogin(String account, String password, Context context)
	{
		this.account = account;
		this.password = password;
		this.context = context;
	}

	public void setTrackTime(long trackTime)
	{
		this.trackTime = trackTime;
	}

	public void setRoutInfo(String rAccount, String rPassword, String rIp)
	{
		this.rAccount = rAccount;
		this.rPassword = rPassword;
		this.rIp = rIp;
	}
	public void setLoginTime(String startHour, String startMin, String endHour, String endMin)
	{
		this.startHour = startHour;
		this.startMin = startMin;
		this.endHour = endHour;
		this.endMin = endMin;
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
	public String loginPath(String account, String password)
	{
		String encodeAccount = null,encodePassWord = null;
		try
		{
			encodeAccount = URLEncoder.encode(realAccount, "UTF-8").replace("+", "%20");
			encodePassWord = URLEncoder.encode(password, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{

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
		if (checkWanStatus() == false)
		{
			try
			{
				realAccount = AccountController.getRealAccount(account);
				HTML = getRespond(getConnect("http://" + rIp + loginPath(realAccount, password)));
				startTrack();
			}
			catch (IOException e)
			{
				handle.sendEmptyMessage(MessageStatus.CANNOT_CONNECT_ROUT);
			}
		}
	}

	public void loginAtTime()
	{

	}

	public String getBase64Acc()
	{
		return "Basic " + Base64.encodeToString((rAccount + ":" + rPassword).getBytes(), Base64.DEFAULT);
	}


	public String[] getWanInfo()
	{
		try
		{
			HTML = getRespond(getConnect("http://" + rIp + "/userRpm/StatusRpm.htm"));
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
				System.out.println("当前速度" + trackTime);
				String[] pppoeInf;
				pppoeInf = getWanInfo();
				if (pppoeInf == null)
				{
					Thread.sleep(trackTime);
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
				Thread.sleep(trackTime);
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

	public void showWaitingDialog(int what)
	{
		switch (what)
		{
			case 0:
				if (pd == null)
				{
					pd = new ProgressDialog(context);
					pd.setTitle("正在处理");
					pd.setMessage("正在连接路由器");
					pd.setCancelable(false);
					pd.show();
				}
				break;
			case 1:
				if(pd!=null)
				{
					pd.cancel();
					pd=null;
				}
				break;
			case 2:
				if(pd!=null)
				{
					pd.cancel();
					pd=null;
					Toast.makeText(context,"连接失败,请检查网络连接",Toast.LENGTH_SHORT).show();
				}
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
