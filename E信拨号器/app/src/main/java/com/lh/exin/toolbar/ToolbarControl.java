package com.lh.exin.toolbar;

import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import com.lh.exin.*;
import android.graphics.*;

public class ToolbarControl
{
	private Toolbar toolbar;
	private DrawerLayout drawer;
	private ActionBarDrawerToggle toggle;
	private AppCompatActivity activity;

	public ToolbarControl(AppCompatActivity activity)
	{
		this.activity = activity;
	}
	public void initToolbar()
	{
		toolbar=(Toolbar) activity.findViewById(R.id.toolbar);
		drawer=(DrawerLayout) activity.findViewById(R.id.drawerlayout);
		toolbar.setTitle("E信拨号器");
		toolbar.setTitleTextColor(Color.WHITE);
		activity.setSupportActionBar(toolbar);
		activity.getSupportActionBar().setHomeButtonEnabled(true);
		activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toggle=new ActionBarDrawerToggle(activity,drawer,toolbar,R.string.open,R.string.close);
		toggle.syncState();
		drawer.setDrawerListener(toggle);
		drawer.setStatusBarBackground(R.color.blue);
		
	}
	public void initToolbarNoDrawer()
	{
		toolbar=(Toolbar) activity.findViewById(R.id.toolbar);
		toolbar.setTitleTextColor(Color.WHITE);
		toolbar.setTitle("定时连接");
		toolbar.setElevation(5);
		activity.setSupportActionBar(toolbar);
		activity.getSupportActionBar().setHomeButtonEnabled(true);
		activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					activity.finish();
				}
			});
	}
	
	public void closeDrawer()
	{
		drawer.closeDrawers();
	}
}
