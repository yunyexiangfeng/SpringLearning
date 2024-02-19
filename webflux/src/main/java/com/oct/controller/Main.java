package com.oct.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

public class Main {

    public static void main(String[] args) {
//        Map<String, String> map = new HashMap<>();
//        map.put("group1", "[0, 200]");
//        map.put("group2", "[200, 400]");
//        map.put("group3", "[400, 600]");
//        map.put("group4", "[600, 800]");
//        map.put("group5", "[800, 1000]");
//        Map<String, List<Long>> groupMap = new HashMap<>();
//        long mod = 1000;
//        long sum = 0;
//        for (Map.Entry<String, String> entry : map.entrySet()){
//            String value = entry.getValue();
//
//            String[] values = value.replace("[", "").replace("]", "").split(",");
//            long begin = Long.parseLong(values[0].trim());
//            long end = Long.parseLong(values[1].trim());
//            System.out.println(begin);
//            System.out.println(end);
//            if (begin >= end){
//                System.out.println("error begin >= end");
//            }
//            sum = sum + (end - begin);
//            List<Long> group = new ArrayList<>();
//            group.add(begin);
//            group.add(end);
//            groupMap.put(entry.getKey(), group);
//        }
//        System.out.println("sum=" + sum);
//
//        if (sum > mod){
//            System.out.println("error sum > mod");
//            return;
//        }
//
//        long uidCrcVal = 90;
//        for (Map.Entry<String, List<Long>> entry : groupMap.entrySet()){
//            long begin = entry.getValue().get(0);
//            long end = entry.getValue().get(1);
//            if (begin < uidCrcVal && uidCrcVal <= end){
//                //增加重构标识,对比进行abtest,查看报表数据对比
//                System.out.println("begin=" + begin + "  uidCrcVal=" + uidCrcVal + " end=" + end);
//                return;
//            }
//        }

        CRC32 crc32 = new CRC32();
//        crc32.update(("gj_time_ab_" + "aad0c892fabe446e9174a9dc3527df3f").getBytes(StandardCharsets.UTF_8));
        crc32.update(("gj_time_ab_" + "8e6ceca1293b4a68ae50a38ea836e062").getBytes(StandardCharsets.UTF_8));
//        crc32.update(("gj_time_ab_" + "f58149dea800439598af19f7f7841c78").getBytes(StandardCharsets.UTF_8));
        System.out.println(crc32.getValue() % 1000);
    }
}
