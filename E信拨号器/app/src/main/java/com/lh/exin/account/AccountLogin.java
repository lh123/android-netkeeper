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
	private String realAccount,password,rAccount,rPassword,rIp,startHour,startMin,endHour,endMin;
	private MessageHandler handle;
	private TrackHandler thandle;
	private TextView active_time;
	private Handler timerHandler;
	private Context context;

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
		"用户名或密码验证失败",
		"服务器无响应",
		"未知原因失败",
		"WAN口未连接"
	};

	public AccountLogin(String realAccount, String password, Context context)
	{
		this.realAccount = realAccount;
		this.password = password;
		this.context = context;
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

	public void setTimeView(TextView t)
	{
		this.active_time=t;
		timerHandler=new Handler(){

			@Override
			public void handleMessage(Message msg)
			{
				active_time.setText((String)msg.obj);
			}

		};
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
		{}
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
		{
			checkError(e);
		}
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
				HttpURLConnection connect=getConnect("http://" + rIp + loginPath(realAccount, password));
				connect.connect();
				connect.getInputStream();
			}
			catch (IOException e)
			{
				checkError(e);
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

	public void startTrack()
	{
		boolean getData = true;
		int count=0;
		while (getData)
		{
			try
			{
				String HTML=getrRespond(getConnect("http://" + rIp + "/userRpm/PPPoECfgRpm.htm"));
				if (++count > 20)
				{
					thandle.sendMessage(handle.obtainMessage(MessageStatus.SET_VIEW_STATUS, "路由器没有响应请求"));
					break;
				}
				String KeywordWAN="var pppoeInf = new Array(";
				int tIndex=HTML.indexOf(KeywordWAN) + KeywordWAN.length();
				System.out.println("开始" + tIndex);
				if (tIndex > 0)
				{
					int tEnd=HTML.indexOf(");", tIndex);
					if (tEnd > tIndex)
					{
						String tArray=HTML.substring(tIndex, tEnd);
						System.out.println("长度=" + tArray.length());
						String[] pppoeInf;
						pppoeInf = tArray.split(",");
						System.out.println(pppoeInf[26]);
						switch (Integer.parseInt(pppoeInf[26]))
						{
							case CONNECTION_NOT_CONNECTED:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[0]));getData = false;break;
							case CONNECTION_SUCCESS:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[1]));getData = false;RoutData.isLogin = true;break;
							case CONNECTION_CONNECTING:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[2] + " 已经跟踪" + count + "次"));System.out.println(count);break;
							case CONNECTION_AUTHENTICATION_FAILED:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[3]));getData = false;break;
							case CONNECTION_NO_RESPONSE:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[4]));getData = false;break;
							case CONNECTION_UNKNOWN:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[5]));getData = false;break;
							case CONNECTION_NOT_CONNECTED_WAN:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[6]));getData = false;break;
							default:break;
						}

					}
				}
				Thread.sleep(1500);
			}
			catch (InterruptedException e)
			{}
			catch (IOException e)
			{
				checkError(e);
			}
		}
	}
	public String[] getWanInfo()
	{
		try
		{
			String HTML=getrRespond(getConnect("http://" + rIp + "/userRpm/StatusRpm.htm"));
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
			checkError(e);
		}
		return null;
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
		temp.append("次DNS服务器: " + backInfo[12] + "\n");
		temp.append("在线时间： " + backInfo[13] + "\n");
		temp.append("---------------------");
		new AlertDialog.Builder(context).setTitle("Wan信息").setMessage(temp.toString()).setPositiveButton("确定", null).show();
	}

	public String getrRespond(HttpURLConnection connect) throws IOException
	{
		InputStream is=connect.getInputStream();
		InputStreamReader isr=new InputStreamReader(is);
		BufferedReader br=new BufferedReader(isr);
		StringBuffer temp=new StringBuffer();
		String HTML=null;
		while ((HTML = br.readLine()) != null)
		{
			temp.append(HTML);
		}
		return temp.toString();
	}

	public void checkError(IOException e)
	{
		if (e instanceof  java.net.SocketTimeoutException)
		{
			handle.sendEmptyMessage(MessageStatus.CONNECT_TIMEOUT);
		}
		else if (e instanceof java.net.ConnectException)
		{
			handle.sendEmptyMessage(MessageStatus.CHECK_UPDATE_FAILED);
		}
		else
		{
			handle.sendEmptyMessage(MessageStatus.UNKNOWN);
		}
	}
	
	public long backTime(String stime)
	{
		String temp=stime.replace(" day(s) ",":");
		String[] timearray=temp.split(":");
		int day=Integer.parseInt(timearray[0]);
		int hour=Integer.parseInt(timearray[1]);
		int minute=Integer.parseInt(timearray[2]);
		int second=Integer.parseInt(timearray[3]);
		long time=day*86400+hour*3600+minute*60+second;
		return time;
	}

	public String timeToString(long ltime)
	{
		int day=(int) ltime/86400;
		int hour=(int) (ltime-day*86400)/3600;
		int minute=(int)(ltime-day*86400-hour*3600)/60;
		int second=(int)(ltime-day*86400-hour*3600-minute*60);
		
		return String.format("%d天 %s:%s:%s",day,timeFormate(hour),timeFormate(minute),timeFormate(second));
	}
	
	public String timeFormate(int time)
	{
		if(time<10)
		{
			return "0"+time;
		}
		else
		{
			return ""+time;
		}
	}
	
	public void getTime()
	{
		String time=getWanInfo()[13];
		final long ltime=backTime(time);
		Timer timer=new Timer();
		TimerTask task=new TimerTask(){

			long t=ltime;
			@Override
			public void run()
			{
				t++;
				timerHandler.sendMessage(timerHandler.obtainMessage(1,timeToString(t)));
			}
		};
		timer.schedule(task,0,1000);
	}

	public boolean checkWanStatus()
	{
		try
		{
			String HTML=getrRespond(getConnect("http://" + rIp + "/userRpm/PPPoECfgRpm.htm"));
			String KeywordWAN="var pppoeInf = new Array(";
			int tIndex=HTML.indexOf(KeywordWAN) + KeywordWAN.length();
			if (tIndex > 0)
			{
				int tEnd=HTML.indexOf(");", tIndex);
				if (tEnd > tIndex)
				{
					String tArray=HTML.substring(tIndex, tEnd);
					System.out.println("长度=" + tArray.length());
					String[] pppoeInf;
					pppoeInf = tArray.split(",");
					System.out.println(pppoeInf[26]);
					switch (Integer.parseInt(pppoeInf[26]))
					{
						case CONNECTION_NOT_CONNECTED:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[0]));break;
						case CONNECTION_SUCCESS:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[1]));RoutData.isLogin = true;break;
						case CONNECTION_CONNECTING:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[2]));RoutData.isLogin = false;break;
						case CONNECTION_AUTHENTICATION_FAILED:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[3]));RoutData.isLogin = false;break;
						case CONNECTION_NO_RESPONSE:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[4]));RoutData.isLogin = false;break;
						case CONNECTION_UNKNOWN:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[5]));RoutData.isLogin = false;break;
						case CONNECTION_NOT_CONNECTED_WAN:thandle.sendMessage(thandle.obtainMessage(MessageStatus.SET_VIEW_STATUS, PPPoELinkStat[6]));RoutData.isLogin = false;break;
						default:RoutData.isLogin = false;break;
					}

				}
			}
		}
		catch (IOException e)
		{
			checkError(e);
		}
		return RoutData.isLogin;
	}
}
			
	
