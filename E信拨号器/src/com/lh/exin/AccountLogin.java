package com.lh.exin;

import java.io.*;
import java.net.*;
import java.util.logging.*;

public class AccountLogin
{
	private String realAccount,password,rAccount,rPassword,rIp;
	private MainActivity.MessageHandler handle;
	private String HTML=null;
	//private String[] wanInfo;


	public AccountLogin(String realAccount, String password, MainActivity.MessageHandler handle)
	{
		this.realAccount = realAccount;
		this.password = password;
		this.handle = handle;
	}

	public void setRoutInfo(String rAccount, String rPassword, String rIp)
	{
		this.rAccount = rAccount;
		this.rPassword = rPassword;
		this.rIp = rIp;
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

	public void login()
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					if (MessageStatus.isLogin == false)
					{
						try
						{
							System.out.println("login");
							URL url=new URL("http://" + rIp + loginPath(realAccount, password));
							HttpURLConnection connect=(HttpURLConnection) url.openConnection();
							handle.sendEmptyMessage(MessageStatus.LOGIN_ING);
							connect.setRequestProperty(
								"Authorization", getBase64Acc());
							connect.connect();
							connect.getInputStream();
							handle.sendEmptyMessage(MessageStatus.LOGIN_SUCCESS);
						}
						catch (IOException e)
						{
							System.out.println(e.getMessage());
							handle.sendEmptyMessage(MessageStatus.LOGIN_FAIL);
						}
					}
				}
			}).start();
	}
	public static String getBase64Acc()
	{
		return "Basic " + Base64.encode("admin" + ":"
										+ "admin");
	}

	public void checkLoginStatus(final String HTML)
	{
		new Thread(new Runnable(){

				@Override
				public void run()
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
								handle.sendEmptyMessage(MessageStatus.LOGIN_FAIL);
							}
						}
					}
				}
			}).start();
	}
	public void getWanHtml()
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					try
					{
						URL url=new URL("http://" + rIp + "/userRpm/StatusRpm.htm");
						HttpURLConnection connect=(HttpURLConnection) url.openConnection();
						connect.setRequestProperty(
							"Authorization", getBase64Acc());
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
						handle.sendMessage(handle.obtainMessage(MessageStatus.GET_WAN_HTML_SUCCESS,HTML));
					}
					catch (IOException e)
					{
						handle.sendEmptyMessage(-1);
					}
				}
			}).start();
	}
	public void getWanInfo()
	{
		getWanHtml();
		new Thread(new Runnable(){

				@Override
				public void run()
				{
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
								wanInfo = tArray.split(",");
								handle.sendMessage(handle.obtainMessage(MessageStatus.GET_WIFI_INFO_SUCCESS, wanInfo));
							}
						}
					}
				}}).start();
	}
}
			
	
