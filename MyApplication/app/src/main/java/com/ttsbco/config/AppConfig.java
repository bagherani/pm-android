package com.ttsbco.config;


public class AppConfig
{

    public static String siteURL;
    public static int port;
    public static final int socketTimeOut = 7000;
    public static final int maxRetries = 2;

    public static String getSiteURL()
    {
        String res = "";
        if (siteURL != null)
        {
            res = "http://" + siteURL;
        }
        if (port != 80 && port > 0)
        {
            res = res + ":" + port + "/";
        }
        return res;
    }

    public static String getHistoryUrl(int id, int address, int skipDays, int takeDays)
    {
        String res = getSiteURL();
        res = res + "history/" + id + "/" + address + "/" + skipDays + "/" + takeDays;
        return res;
    }
}



