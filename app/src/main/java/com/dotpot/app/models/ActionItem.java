package com.dotpot.app.models;


import com.dotpot.app.ui.BaseActivity;

public class ActionItem {

    public transient BaseActivity act;

    public String id="";
    public String title="";
    public String subTitle="";
    public String bottom="";
    public String rightTop="";
    public String rightBottom="";
    public String image="";

    public long dateTime=0;
    public String actionType="";
    public String dataId="";
    public String dataIdEx="";
    public String accentColorId="";
    public int priority=1;

    public boolean doFinish=false;

    public ActionItem(){}

    public ActionItem(BaseActivity act, String id, String title,
                      String actionType, String dataId) {
        this.act = act;
        this.id = id;
        this.title = title;
        this.actionType = actionType;
        this.dataId = dataId;
    }

    public boolean matchTargetAudience(GenricUser user)
    {
        try{
            if(dataIdEx==null ||user==null)
                return true;
            return dataIdEx.equals("all") || dataIdEx.equals("") ||
                    dataIdEx.equals(user.getType()) ||
                    dataIdEx.equals(user.getStatus()) ||
                    dataIdEx.equals(user.getId())
                    || dataIdEx.equals(user.getName()) ||
                    dataIdEx.equals(user.getAlias());
        }catch (Exception e)
        {
            return false;
        }
    }
}
