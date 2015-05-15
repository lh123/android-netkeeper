package com.lh.exin.message;
import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import com.lh.exin.account.*;

public class MessageHandler extends Handler
{
	private Context context;
	private TextView tvStatus;
	private AccountLogin login;

	public MessageHandler(Context context, TextView tvStatus, AccountLogin login)
	{
		this.context = context;
		this.tvStatus = tvStatus;
		this.login = login;
	}
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
				//login.login();
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
			case MessageStatus.WANT_GET_WANINFO:
				login.getWanInfo();
				break;
			case MessageStatus.WANT_LOGIN:
				//login.login();
				login.checkLoginStatus((String)msg.obj);
				break;
			case MessageStatus.NEED_LOGIN:
				login.login();
				break;
			case MessageStatus.WANT_GET_HTML:
				break;
			case MessageStatus.CONNECT_TIMEOUT:
				tvStatus.setText("连接超时");
				break;
			case MessageStatus.CONNECT_FAIL:
				tvStatus.setText("无法连接到路由器");
				break;
			default:
				tvStatus.setText("未知错误");
		}
		super.handleMessage(msg);
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
}
