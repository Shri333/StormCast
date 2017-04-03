package com.projects.shrihan.stormcast;

import java.io.Serializable;

/**
 * Created by shrihan on 3/4/17.
 */

class Alert implements Serializable {

    private String mType;
    private String mDescription;
    private String mDate;
    private String mExpires;
    private String mMessage;

    Alert(String type, String description, String date, String expires, String message) {
        mType = type;
        mDescription = description;
        mDate = date;
        mExpires = expires;
        mMessage = message;
    }

    String getType() {
        return mType;
    }

    String getDescription() {
        return mDescription;
    }

    String getDate() { return mDate; }

    String getExpires() {
        return mExpires;
    }

    String getMessage() {
        return mMessage;
    }
}
