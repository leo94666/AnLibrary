package com.top.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.top.arch.logger.AndroidLogAdapter;
import com.top.arch.logger.CsvFormatStrategy;
import com.top.arch.logger.DiskLogAdapter;
import com.top.arch.logger.FormatStrategy;
import com.top.arch.logger.Logger;
import com.top.arch.logger.PrettyFormatStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LoggerActivity extends Activity {

    ArrayList list = new ArrayList();
    HashMap map = new HashMap();
    HashSet set = new HashSet();
    String[] array = new String[]{"100", "101", "102", "103"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logger);

        list.add("list_aaa");
        list.add("list_bbb");
        list.add("list_ccc");

        map.put("map_aaa_key","map_aaa_value");
        map.put("map_bbb_key","map_bbb_value");
        map.put("map_ccc_key","map_ccc_value");

        set.add("set_aaa");
        set.add("set_bbb");
        set.add("set_ccc");


//
//        Log.d("LoggerActivity", "I'm a log which you don't see easily, hehe");
//        Log.d("LoggerActivity", "{ \"key\": 3, \n \"value\": something}");
//        Log.d("LoggerActivity", "There is a crash somewhere or any warning");
//
//        Logger.addLogAdapter(new AndroidLogAdapter());
//        Logger.d("message");
//        Logger.clearLogAdapters();
//
//
//        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
//                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
//                .methodCount(0)         // (Optional) How many method line to show. Default 2
//                .methodOffset(3)        // (Optional) Skips some method invokes in stack trace. Default 5
////        .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
//                .tag("LoggerActivity")   // (Optional) Custom tag for each log. Default PRETTY_LOGGER
//                .build();
//
//        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
//
//        Logger.addLogAdapter(new AndroidLogAdapter() {
//            @Override
//            public boolean isLoggable(int priority, String tag) {
//                return BuildConfig.DEBUG;
//            }
//        });
//
//        Logger.addLogAdapter(new DiskLogAdapter());
//
//
//        Logger.w("no thread info and only 1 method");
//
//        Logger.clearLogAdapters();
//        formatStrategy = PrettyFormatStrategy.newBuilder()
//                .showThreadInfo(false)
//                .methodCount(0)
//                .build();
//
//        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
//        Logger.i("no thread info and method info");
//
//        Logger.t("tag").e("Custom tag for only one use");
//
//        Logger.json("{ \"key\": 3, \"value\": something}");
//
//        Logger.d(Arrays.asList("foo", "bar"));
//
//        Map<String, String> map = new HashMap<>();
//        map.put("key", "value");
//        map.put("key1", "value2");
//
//        Logger.d(map);
//
//        Logger.clearLogAdapters();
//        formatStrategy = PrettyFormatStrategy.newBuilder()
//                .showThreadInfo(false)
//                .methodCount(0)
//                .tag("LoggerActivity")
//                .build();
//        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
//
//        Logger.w("my log message with my tag");

    }



    public void click_base(View view) {
        Logger.addLogAdapter(new AndroidLogAdapter());
        log();
    }

    public void click_more(View view) {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(3)        // (Optional) Skips some method invokes in stack trace. Default 5
                //.logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("LoggerActivity")   // (Optional) Custom tag for each log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        log();
    }

    public void click_file(View view) {
        Logger.addLogAdapter(new DiskLogAdapter());
        log();
    }

    public void click_file_more(View view) {
        FormatStrategy formatStrategy = CsvFormatStrategy.newBuilder()
                .tag("custom")
                .build();

        Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));
        log();
    }


    private void log(){
        Logger.i("Logger.i,  my log message");
        Logger.w("Logger.w,  my log message");
        Logger.e("Logger.e,  my log message");
        Logger.v("Logger.v,  my log message");
        Logger.d("Logger.d,  my log message");
        Logger.wtf("Logger.wtf,  my log message");


        Logger.i("hello %s", "logger");
        Logger.w("hello %s", "logger");
        Logger.e("hello %s", "logger");
        Logger.v("hello %s", "logger");
        Logger.d("hello %s", "logger");
        Logger.wtf("hello %s", "logger");

        Logger.d(list);
        Logger.d(map);
        Logger.d(array);
        Logger.d(set);

        Logger.json("{\n" +
                "\"a\":100,\n" +
                "\"b\":101,\n" +
                "\"c\":102\n" +
                "}");
        Logger.xml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>\n" +
                "  <a>100</a>\n" +
                "  <b>101</b>\n" +
                "  <c>102</c>\n" +
                "</root>");
    }

}
