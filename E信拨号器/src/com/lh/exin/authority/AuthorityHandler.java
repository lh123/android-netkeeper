package com.lh.exin.authority;
import android.os.*;
import com.lh.exin.message.*;
import android.widget.*;
import android.content.*;

public class AuthorityHandler extends Handler
{
	private CheckAuthority authority;
	private Context context;

	public AuthorityHandler(CheckAuthority authority,Context context)
	{
		this.authority = authority;
		this.context=context;
	}
	@Override
	public void handleMessage(Message msg)
	{
		switch(msg.what)
		{
			case MessageStatus.AUTH_PASS:
				authority.setAuthStatusPass();
				Toast.makeText(context,"成功获得授权",Toast.LENGTH_SHORT).show();
				break;
			case MessageStatus.AUTH_FAILED:
				authority.setAuthStatusFail();
				authority.showDialog("未授权","是否向服务器取得授权?");
				break;
			case MessageStatus.AUTH_CONNECT_FAILED:
				authority.setAuthStatusFail();
				authority.showDialog("连接服务器失败","是否重新尝试取得授权");
				break;
			default:
				Toast.makeText(context,"出现未知错误",Toast.LENGTH_SHORT).show();
		}
		super.handleMessage(msg);
	}
	
}
