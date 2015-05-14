package com.lh.exin;

import com.lh.exin.CXKUsername;

/**
 * Created by 陈耀璇 on 2015/5/6.
 * 真实帐号控制类。
 */
public class AccountController {

    public static final String[] RADIUS = {
              "cqxinliradius002", //重庆地区Netkeeper2.5
             "hubtxinli01", //湖北E信
            "singlenet01" //杭州地区
    };

    public static final int RADIUS_HOME = -1,
            RADIUS_CHONGQING = 0,
            RADIUS_HUBEI = 1,
            RADIUS_HANGZHOU = 2;

    /**RADIUS标识*/
    public static int mCurrentRadius = 1;

    public static String getRealAccount(String username){
        if(username == null) return null;
        if(mCurrentRadius != -1 && mCurrentRadius < RADIUS.length && mCurrentRadius >= 0){
            return new CXKUsername(username,RADIUS[mCurrentRadius]).Realusername();
        }
        return username;
    }

    public static String getRealAccount(String username,long time){
        if(username == null) return null;
        if(time > 0 && mCurrentRadius != -1 && mCurrentRadius < RADIUS.length && mCurrentRadius >= 0){
            return new CXKUsername(username,RADIUS[mCurrentRadius]).Realusername(time);
        }
        return username;
    }

}
