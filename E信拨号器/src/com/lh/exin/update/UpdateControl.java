package com.lh.exin.update;
import android.content.*;

public class UpdateControl
{
	private Context context;
	private DownLoadHandler dHandler;
	private UpdateCheck check;
	private UpdateDownload down;

	public UpdateControl(Context context)
	{
		this.context = context;
	}
	public void setUpdateInfo()
	{
		down=new UpdateDownload(context);
		check=new UpdateCheck(context);
		dHandler=new DownLoadHandler(context,check,down);
		down.setHandler(dHandler);
		check.setHandler(dHandler);
	}
	public void startUpdate()
	{
		check.checkVersion();
	}
}
