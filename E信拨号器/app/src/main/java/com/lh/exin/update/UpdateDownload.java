package com.lh.exin.update;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import java.io.*;
import java.net.*;
import com.lh.exin.message.*;
import android.widget.*;

public class UpdateDownload
{
	private Context context;
	
	private DownLoadHandler handler;

	public UpdateDownload(Context context)
	{
		this.context = context;
	}
	public void setHandler(DownLoadHandler handler)
	{
		this.handler=handler;
	}
	public void startDownload(final String path)
	{
		final ProgressDialog pd=new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载");
		pd.show();
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					try
					{
						URL url = new URL(path);
						HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
						conn.setConnectTimeout(5000);
						pd.setMax(conn.getContentLength());
						InputStream is = conn.getInputStream();
						File file = new File(Environment.getExternalStorageDirectory(), "updata.apk");
						FileOutputStream fos = new FileOutputStream(file);
						BufferedInputStream bis = new BufferedInputStream(is);
						byte[] buffer = new byte[1024];
						int len ;
						int total=0;
						while ((len = bis.read(buffer)) != -1)
						{
							fos.write(buffer, 0, len);
							total += len;
							pd.setProgress(total);
						}
						fos.close();
						bis.close();
						is.close();
						handler.sendMessage(handler.obtainMessage(MessageStatus.DOWN_SUCCESS,file));
					}
					catch (IOException e)
					{
						System.out.println("下载失败");
					}
				}
			}).start();
	}
	
	public void installApk(File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}
}
