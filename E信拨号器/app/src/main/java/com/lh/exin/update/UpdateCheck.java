package com.lh.exin.update;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import com.lh.exin.message.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.net.*;

public class UpdateCheck
{
	private Context context;
	private DownLoadHandler handler=null;
	private String version,versionName,description,path;
	private String currentVersionName;
	//private boolean checkstatus==false;

	public UpdateCheck(Context context)
	{
		this.context = context;
	}
	public void setHandler(DownLoadHandler handler)
	{
		this.handler=handler;
	}
	public int getCurrentVersion()
	{
		PackageManager pm=context.getPackageManager();
		try
		{
			PackageInfo pi=pm.getPackageInfo(context.getPackageName(), 0);
			int currentVersion=pi.versionCode;
			currentVersionName=pi.versionName;
			return Integer.parseInt(currentVersionName.replace(".",""));
		}
		catch (PackageManager.NameNotFoundException e)
		{
			return 0;
		}
	}
	public void checkVersion()
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					try
					{
						URL url=new URL("https://git.oschina.net/lh123/update-service/raw/master/update.xml");
						HttpURLConnection connect=(HttpURLConnection) url.openConnection();
						connect.setConnectTimeout(3000);
						DocumentBuilder db=DocumentBuilderFactory.newInstance().newDocumentBuilder();
						Document doc=db.parse(connect.getInputStream());
						Element root=doc.getDocumentElement();
						NodeList list=root.getChildNodes();
						for(int i=0;i<list.getLength();i++)
						{
							Node node=list.item(i);
							if(node.getNodeType()==Node.ELEMENT_NODE)
							{
								Element child=(Element) node;

								
								if(child.getNodeName().equals(("version")))
								{
									version=child.getFirstChild().getNodeValue();
								}
								else if ((child.getNodeName().equals("version_name")))
								{
									versionName=child.getFirstChild().getNodeValue();
								}
								else if ((child.getNodeName().equals("description")))
								{
									description=child.getFirstChild().getNodeValue();
								}
								
								else if (("url".equals(child.getNodeName())))
								{
									path=child.getFirstChild().getNodeValue();
								}
							}
						}
						handler.sendEmptyMessage(MessageStatus.CHECK_UPDATE_SUCCESS);
					}
					catch (SAXException e)
					{
						handler.sendEmptyMessage(MessageStatus.CHECK_UPDATE_FAILED);
					}
					catch (IOException e)
					{
						handler.sendEmptyMessage(MessageStatus.CHECK_UPDATE_FAILED);
					}
					catch (ParserConfigurationException e)
					{
						handler.sendEmptyMessage(MessageStatus.CHECK_UPDATE_FAILED);
					}
				}
			}).start();
	}
	
	public void showDialog()
	{
		new AlertDialog.Builder(context).setTitle("更新")
		.setMessage("当前版本:"+currentVersionName+"\n最新版本:"+versionName+"\n更新内容:\n"+description)
			.setPositiveButton("升级", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					handler.sendMessage(handler.obtainMessage(MessageStatus.CONFIRM_UPDATE,path));
				}
			})
		.setNegativeButton("取消",null)
		.setCancelable(false)
		.show();
	}
	
	protected void checkNeedUpdate()
	{
		int curren=getCurrentVersion();
		int aimversion=Integer.parseInt(versionName.replace(".",""));
		if(curren<aimversion)
		{
			handler.sendMessage(handler.obtainMessage(MessageStatus.NEED_UPDATE,path));
		}
		else
		{
			handler.sendEmptyMessage(MessageStatus.DONT_NEED_UPDATE);
		}
	}
	
	protected void checkNeedUpdateAuto()
	{
		int curren=getCurrentVersion();
		int aimversion=Integer.parseInt(versionName.replace(".",""));
		if(curren<aimversion)
		{
			handler.sendMessage(handler.obtainMessage(MessageStatus.NEED_UPDATE,path));
		}
	}
}
