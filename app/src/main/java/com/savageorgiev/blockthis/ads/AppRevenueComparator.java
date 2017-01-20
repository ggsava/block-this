package com.savageorgiev.blockthis.ads;

import java.util.Comparator;

public class AppRevenueComparator implements Comparator<App>
{
    @Override
    public int compare(App app1, App app2) {
        return app2.getRevenueAmount().compareTo(app1.getRevenueAmount());
    }
}