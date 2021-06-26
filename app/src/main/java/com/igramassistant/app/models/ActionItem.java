package com.igramassistant.app.models;


import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.igramassistant.app.ui.BaseActivity;
import com.igramassistant.app.utl;

public class ActionItem {


    public transient BaseActivity act;

    public String id = "";
    @NonNull
    public String title = "";
    @NonNull
    public String subTitle = "";
    @NonNull
    public String textAction = "";
    public String rightTop = "";
    public String rightBottom = "";
    public String image = "";

    public long dateTime = 0;
    @NonNull
    public String actionType = "";
    public String dataId = "";
    public String dataIdEx = "";
    public String accentColorId = "";
    public String bgColor = "";
    public int priority = 1;
    public boolean doFinish = false;

    public ActionItem() {
    }

    public ActionItem(BaseActivity act, String id, String title,
                      String actionType, String dataId) {
        this.act = act;
        this.id = id;
        this.title = title;
        this.actionType = actionType;
        this.dataId = dataId;
    }

    @Nullable
    public Drawable getIcon() {
        if(image!=null && !image.contains("http")){
            return utl.getDrawable(image);
        }
        return null;
    }

    public boolean matchTargetAudience(GenricUser user) {
        try {
            if (dataIdEx == null || user == null)
                return true;
            return dataIdEx.equals("all") || dataIdEx.equals("") ||
                    dataIdEx.equals(user.getType()) ||
                    dataIdEx.equals(user.getStatus()) ||
                    dataIdEx.equals(user.getId())
                    || dataIdEx.equals(user.getName()) ||
                    dataIdEx.equals(user.getAlias());
        } catch (Exception e) {
            return false;
        }
    }


    public static final class ActionItemBuilder {
        private transient BaseActivity act;
        private String id = "";
        private String title = "";
        private String subTitle = "";
        private String textAction = "";
        private String rightTop = "";
        private String rightBottom = "";
        private String image = "";
        private long dateTime = 0;
        private String actionType = "";
        private String dataId = "";
        private String dataIdEx = "";
        private String accentColorId = "";
        private int priority = 1;
        private boolean doFinish = false;

        private ActionItemBuilder() {
        }

        public static ActionItemBuilder anActionItem() {
            return new ActionItemBuilder();
        }

        public ActionItemBuilder withAct(BaseActivity act) {
            this.act = act;
            return this;
        }

        public ActionItemBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public ActionItemBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public ActionItemBuilder withSubTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public ActionItemBuilder withTextAction(String textAction) {
            this.textAction = textAction;
            return this;
        }

        public ActionItemBuilder withRightTop(String rightTop) {
            this.rightTop = rightTop;
            return this;
        }

        public ActionItemBuilder withRightBottom(String rightBottom) {
            this.rightBottom = rightBottom;
            return this;
        }

        public ActionItemBuilder withImage(String image) {
            this.image = image;
            return this;
        }

        public ActionItemBuilder withDateTime(long dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public ActionItemBuilder withActionType(String actionType) {
            this.actionType = actionType;
            return this;
        }

        public ActionItemBuilder withDataId(String dataId) {
            this.dataId = dataId;
            return this;
        }

        public ActionItemBuilder withDataIdEx(String dataIdEx) {
            this.dataIdEx = dataIdEx;
            return this;
        }

        public ActionItemBuilder withAccentColorId(String accentColorId) {
            this.accentColorId = accentColorId;
            return this;
        }

        public ActionItemBuilder withPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public ActionItemBuilder withDoFinish(boolean doFinish) {
            this.doFinish = doFinish;
            return this;
        }

        public ActionItem build() {
            ActionItem actionItem = new ActionItem(act, id, title, actionType, dataId);
            actionItem.doFinish = this.doFinish;
            actionItem.rightTop = this.rightTop;
            actionItem.rightBottom = this.rightBottom;
            actionItem.dataIdEx = this.dataIdEx;
            actionItem.accentColorId = this.accentColorId;
            actionItem.priority = this.priority;
            actionItem.textAction = this.textAction;
            actionItem.dateTime = this.dateTime;
            actionItem.subTitle = this.subTitle;
            actionItem.image = this.image;
            return actionItem;
        }
    }
}
