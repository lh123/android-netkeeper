package com.lh.exin.toolbar;
import android.graphics.*;
import android.graphics.drawable.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import com.lh.exin.*;
import android.os.*;

public class ToolbarControl
{
	private Toolbar toolbar;
	private DrawerLayout drawer;
	private ActionBarDrawerToggle toggle;
	private AppCompatActivity activity;
	private boolean isOpen=false;

	public ToolbarControl(AppCompatActivity activity)
	{
		this.activity = activity;
	}
	public void initToolbar()
	{
		toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
		drawer = (DrawerLayout) activity.findViewById(R.id.drawerlayout);
		toolbar.setTitle("E信拨号器");
		setPading();
		activity.setSupportActionBar(toolbar);
		activity.getSupportActionBar().setHomeButtonEnabled(true);
		activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.open, R.string.close){
			public void onDrawerOpened(View drawerView)
			{
				isOpen = true;
			}
			public void onDrawerClosed(View drawerView)
			{
				isOpen = false;
			}
		};
		toggle.syncState();
		drawer.setDrawerListener(toggle);
		drawer.setStatusBarBackground(R.color.blue);

	}
	public void initToolbarNoDrawer(String title)
	{
		toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
		toolbar.setTitle(title);
		setPading();
		ViewCompat.setElevation(toolbar, 10);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 
		{
			Drawable drawable = activity.getApplication().getApplicationContext().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
			drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
			toolbar.setNavigationIcon(drawable);
		}
		else
		{
			toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
		}
		toolbar.setNavigationOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					activity.finish();
				}
			});
	}

	public void setPading()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			DisplayParams params=DisplayParams.getInstance(activity);
			toolbar.setPadding(0, DisplayUtil.dip2px(24,params.scale), 0, 0);
		}
	}
	public boolean drawerStatus()
	{
		return isOpen;
	}

	public void closeDrawer()
	{
		drawer.closeDrawers();
	}
}
