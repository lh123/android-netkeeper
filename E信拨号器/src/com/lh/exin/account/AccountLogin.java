package com.lh.exin.account;

import com.lh.exin.message.*;
import java.io.*;
import java.net.*;

public class AccountLogin
{
	private String realAccount,password,rAccount,rPassword,rIp,startHour,startMin,endHour,endMin;
	private MessageHandler handle;
	private String HTML=null;


	public AccountLogin(String realAccount, String password)
	{
		this.realAccount = realAccount;
		this.password = password;
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

	public void setHandler(MessageHandler handle)
	{
		this.handle=handle;
	}
	public HttpURLConnection getConnect(String path) throws MalformedURLException, IOException 
	{
		URL url=new URL(path);
		HttpURLConnection connect=(HttpURLConnection) url.openConnection();
		connect.setRequestProperty(
			"Authorization", getBase64Acc());
		connect.setConnectTimeout(3000);
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
		if (MessageStatus.isLogin == false)
		{
			new Thread(new Runnable(){

					@Override
					public void run()
					{
						try
						{
							HttpURLConnection connect=getConnect("http://" + rIp + loginPath(realAccount, password));
							connect.connect();
							//handle.sendEmptyMessage(MessageStatus.LOGIN_SUCCESS);
						}
						catch (IOException e)
						{}

					}
				}).start();
		}
	}
//	public void login()
//	{
//		new Thread(new Runnable(){
//
//				@Override
//				public void run()
//				{
//					if (MessageStatus.isLogin == false)
//					{
//						try
//						{
//							HttpURLConnection connect=getConnect("http://" + rIp + loginPath(realAccount, password));
//							connect.getInputStream();
//							handle.sendEmptyMessage(MessageStatus.LOGIN_SUCCESS);
//						}
//						catch (IOException e)
//						{
//							System.out.println(e.getMessage());
//							handle.sendEmptyMessage(MessageStatus.LOGIN_FAIL);
//						}
//					}
//				}
//			}).start();
//	}
//
//	public void loginAtTime()
//	{
//		new Thread(new Runnable(){
//
//				@Override
//				public void run()
//				{
//					if (MessageStatus.isLogin == false)
//					{
//						try
//						{
//							HttpURLConnection connect= getConnect("http://" + rIp + loginPathAtTime(realAccount, password, startHour, startMin, endHour, endMin));
//							handle.sendEmptyMessage(MessageStatus.LOGIN_ING);
//							connect.getInputStream();
//							handle.sendEmptyMessage(MessageStatus.LOGIN_SUCCESS);
//						}
//						catch (IOException e)
//						{
//							System.out.println(e.getMessage());
//							handle.sendEmptyMessage(MessageStatus.LOGIN_FAIL);
//						}
//					}
//				}
//			}).start();
//	}

	public String getBase64Acc()
	{
		return "Basic " + Base64.encode(rAccount + ":" + rPassword);
	}

	public void checkLoginStatus(final String HTML)
	{
		System.out.println("check");
		String KeywordWAN="var wanPara = new Array(";
		int tIndex=HTML.indexOf(KeywordWAN) + KeywordWAN.length();
		if (tIndex > 0)
		{
			int tEnd=HTML.indexOf(");", tIndex);
			if (tEnd > tIndex)
			{
				String tArray=HTML.substring(tIndex, tEnd);
				String[] wanInfo;
				wanInfo = tArray.split(",");
				//handle.sendMessage(handle.obtainMessage(MessageStatus.GET_WIFI_INFO_SUCCESS, wanInfo));
				if (wanInfo[14].equals("1"))
				{
					handle.sendEmptyMessage(MessageStatus.WAN_SUCCESS);
				}
				else
				{
					handle.sendEmptyMessage(MessageStatus.NEED_LOGIN);
				}
			}
		}
	}
	public void getWanHtml(final int want)
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					try
					{
						//URL url=new URL();
						HttpURLConnection connect=getConnect("http://" + rIp + "/userRpm/StatusRpm.htm");
//						connect.setRequestProperty(
//							"Authorization", getBase64Acc());
						InputStream is=connect.getInputStream();
						InputStreamReader isr=new InputStreamReader(is);
						BufferedReader br=new BufferedReader(isr);
						StringBuffer temp=new StringBuffer();
						//String HTML=null;
						while ((HTML = br.readLine()) != null)
						{
							temp.append(HTML);
						}
						HTML = temp.toString();
						handle.sendMessage(handle.obtainMessage(want, HTML));
					}
					catch (IOException e)
					{
						System.out.println(e.toString());
						if (e instanceof  java.net.SocketTimeoutException)
						{
							handle.sendEmptyMessage(10);
						}
						else if (e instanceof java.net.ConnectException)
						{
							handle.sendEmptyMessage(11);
						}
						else
						{
							handle.sendEmptyMessage(-1);
						}
					}
				}
			}).start();
	}
	public void getWanInfo()
	{
		//getWanHtml(MessageStatus.WANT_GET_HTML);
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
					handle.sendMessage(handle.obtainMessage(MessageStatus.GET_WIFI_INFO_SUCCESS, wanInfo));
				}
			}
		}
	}
}
			
	
