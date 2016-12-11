package com.example.android.sunshine.app;

public class DateTimeItem {
    private String title;

    private int mResourceId;

    public DateTimeItem() {
        mResourceId = 0;
    }

    public DateTimeItem(String title ) {
        this.title = title;
        setResourceIdForItem();

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        setResourceIdForItem();
    }



    public int getResourceId() {
        return mResourceId;
    }

    private void setResourceIdForItem(){
        if (title.contains( "Sunny" )) {
            mResourceId = R.drawable.ic_launcher_sunshine;
        }

        if (title.contains("clear sky")) {
            mResourceId = R.mipmap.ic_sun;
        }
        if (title.contains("snow")) {
            mResourceId = R.mipmap.ic_snow;
        }
        if (title.contains("Clouds")) {
            mResourceId = R.mipmap.ic_rain_cloud_sun;
        }

        if (title.contains("rain")) {
            mResourceId = R.mipmap.ic_rain;
        }

    }


}