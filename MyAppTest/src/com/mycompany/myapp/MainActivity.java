package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import java.util.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					long a,b;
					System.out.println(a=System.currentTimeMillis());
					try
					{
						Thread.sleep(3000);
					}
					catch (InterruptedException e)
					{}
					System.out.println(b=System.currentTimeMillis());
					System.out.println(b-a);
					
				}
			}).start();
    }
}
