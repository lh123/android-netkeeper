package com.lh.exin.activity;

import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.lh.exin.account.*;
import com.lh.exin.message.*;
import com.lh.exin.routdata.*;
import com.lh.exin.toolbar.*;
import com.lh.exin.*;

public class About extends AppCompatActivity
{
	private ToolbarControl toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		init();
	}

	public void init()
	{
		toolbar=new ToolbarControl(this);
		toolbar.initToolbarNoDrawer("关于");
	}
}
