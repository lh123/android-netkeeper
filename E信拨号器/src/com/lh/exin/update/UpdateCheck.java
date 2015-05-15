package com.lh.exin.update;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import com.lh.exin.message.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class UpdateCheck
{
	private Context context;
	private DownLoadHandler handler=null;
	private String version,versionName,name,path;
	private String currentVersionName;

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
			return currentVersion;
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
						DocumentBuilder db=DocumentBuilderFactory.newInstance().newDocumentBuilder();
						Document doc=db.parse("https://raw.githubusercontent.com/lh123/update-service/master/update.xml");
						Element root=doc.getDocumentElement();
						NodeList list=root.getChildNodes();
						for(int i=0;i<list.getLength();i++)
						{
							Node node=list.item(i);
							if(node.getNodeType()==Node.ELEMENT_NODE)
							{
								Element child=(Element) node;

								if (("name".equals(child.getNodeName())))
								{
									name=child.getFirstChild().getNodeValue();
								}
								else if(child.getNodeName().endsWith("version"))
								{
									version=child.getFirstChild().getNodeValue();
								}
								else if (("version_name".equals(child.getNodeName())))
								{
									versionName=child.getFirstChild().getNodeValue();
								}
								else if (("url".equals(child.getNodeName())))
								{
									path=child.getFirstChild().getNodeValue();
								}
							}
						}
					}
					catch (SAXException e)
					{}
					catch (IOException e)
					{}
					catch (ParserConfigurationException e)
					{}
					checkNeedUpdate();
				}
			}).start();
	}
	
	public void showDialog()
	{
		new AlertDialog.Builder(context).setTitle("更新")
		.setMessage("当前版本:"+currentVersionName+"\n最新版本:"+versionName)
			.setPositiveButton("升级", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					handler.sendMessage(handler.obtainMessage(MessageStatus.CONFIRM_UPDATE,path));
				}
			})
		.setNegativeButton("取消",null)
		.show();
	}
	
	protected void checkNeedUpdate()
	{
		if(getCurrentVersion()<Integer.parseInt(version))
		{
			handler.sendMessage(handler.obtainMessage(MessageStatus.NEED_UPDATE,path));
		}
	}
}
