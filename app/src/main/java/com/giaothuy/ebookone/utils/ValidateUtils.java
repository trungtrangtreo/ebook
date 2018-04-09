package com.giaothuy.ebookone.utils;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;

import com.giaothuy.ebookone.config.Constant;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;

import java.util.Date;
import java.util.Locale;

/**
 * Created by 1 on 4/9/2018.
 */

public class ValidateUtils {

    public static String convertLongToDatetime(long time) {

        return DateFormat.format(Constant.TIME_STAMP.DATE_TIME_1, new Date(time)).toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static TimeAgoMessages getTimeAgo() {
        Locale LocaleBylanguageTag = Locale.forLanguageTag("en");
        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build();

        return messages;
    }
}
