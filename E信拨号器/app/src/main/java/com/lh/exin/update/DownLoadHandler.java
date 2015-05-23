package com.lh.exin.update;
import android.os.*;
import android.content.*;
import com.lh.exin.message.*;
import java.io.*;
import android.widget.*;

public class DownLoadHandler extends Handler
{
	private Context context;
	private UpdateCheck updateCheck;
	private UpdateDownload down;

	public DownLoadHandler(Context context, UpdateCheck updateCheck, UpdateDownload down)
	{
		this.context = context;
		this.updateCheck = updateCheck;
		this.down = down;
	}
	@Override
	public void handleMessage(Message msg)
	{
		switch(msg.what)
		{
			case MessageStatus.NEED_UPDATE:
				updateCheck.showDialog();
				break;
			case MessageStatus.DONT_NEED_UPDATE:
				Toast.makeText(context,"已是最新版",Toast.LENGTH_SHORT).show();
				break;
			case MessageStatus.CONFIRM_UPDATE:
				down.startDownload((String)msg.obj);
				break;
			case MessageStatus.DOWN_SUCCESS:
				down.installApk((File)msg.obj);
				break;
//			case MessageStatus.DOWN_FAILED:
//				Toast.makeText(context,"下载失败",Toast.LENGTH_SHORT).show();
//				break;
			case MessageStatus.CHECK_UPDATE_SUCCESS:
				updateCheck.checkNeedUpdate();
				break;
			case MessageStatus.CHECK_UPDATE_FAILED:
				Toast.makeText(context,"检查更新失败",Toast.LENGTH_SHORT).show();
				break;
		}
		super.handleMessage(msg);
	}
}
