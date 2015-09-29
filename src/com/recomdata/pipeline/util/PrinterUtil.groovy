package com.recomdata.pipeline.util

import org.apache.log4j.Logger

/**
 * Created by haxia on 9/17/2015.
 */


class PrinterUtil {

    private static final Logger log = Logger.getLogger(PrinterUtil)

    /**
     * print out ArrayList <String>
     *
     * @param al
     */
    static void printArrayList(String title, ArrayList<String> al) {
        printArrayList(title, al, "info")
    }

    static void printArrayList(String title, ArrayList<String> al, String logLevel) {
        printTitle(title, logLevel)
        printArrayList(al, logLevel)
    }

    static void printArrayList(ArrayList<String> al, String logLevel) {
        String level = logLevel.toLowerCase()
        for (int i in 0..al.size() - 1) {
            if (al[i]) {
                if (level.equals("trace")) log.trace(i + ":\t" + al[i])
                else if (level.equals("debug")) log.debug(i + ":\t" + al[i])
                else log.info(i + ":\t" + al[i])
            }
        }
    }

    /**
     * print out ArrayList of HashSet
     *
     * @param al ArrayList<HashSet>()
     */
    static void printHashSetArray(String title, ArrayList<HashSet> al) {
        printHashSetArray(title, al, "info")
    }

    static void printHashSetArray(String title, ArrayList<HashSet> al, String logLevel) {
        printTitle(title, logLevel)
        printHashSetArray(al, logLevel)
    }

    static void printHashSetArray(ArrayList<HashSet> al, String logLevel) {
        String level = logLevel.toLowerCase()
        for (int i in 0..al.size() - 1) {
            if (al[i]) {
                for (Object object : al[i]) {
                    if (level.equals("trace")) log.trace(i + ":\t" + (String) object)
                    else if (level.equals("debug")) log.debug(i + ":\t" + (String) object)
                    else log.info(i + ":\t" + (String) object)
                }
            }
        }
    }

    static void printHashMapArray(String title, ArrayList<HashMap<String, String>> hma) {
        printHashMapArray(title, hma, "info")
    }

    static void printHashMapArray(String title, ArrayList<HashMap<String, String>> hma, String logLevel) {
        printTitle(title, logLevel)
        printHashMapArray(hma, logLevel)
    }

    static void printHashMapArray(ArrayList<HashMap<String, String>> hma, String logLevel) {
        String level = logLevel.toLowerCase()
        for (int i in 0..hma.size() - 1){
            printHashMap(hma[i], level)
        }
    }


    static void printHashSet(String title, HashSet<String> hSet) {
        printHashSet(title, hSet, "info")
    }

    static void printHashSet(String title, HashSet<String> hSet, String logLevel) {
        printTitle(title, logLevel)
        printHashSet(hSet, logLevel)
    }

    static void printHashSet(HashSet<String> hSet, String logLevel) {
        String level = logLevel.toLowerCase()
        hSet.each {
            if (level.equals("trace")) log.trace(it)
            else if (level.equals("debug")) log.debug(it)
            else log.info(it)
        }
    }


    static void printHashMap(String title, HashMap<String, String> hMap) {
        printHashMap(title, hMap, "info")
    }

    static void printHashMap(String title, HashMap<String, String> hMap, String logLevel) {
        printTitle(title, logLevel)
        printHashMap(hMap, logLevel)
    }

    static void printHashMap(HashMap<String, String> hMap, String logLevel) {
        String level = logLevel.toLowerCase()
        hMap.each { k, v ->
            if (level.equals("trace")) log.trace(k + ":\t" + v)
            else if (level.equals("debug")) log.debug(k + ":\t" + v)
            else log.info("$k:\t\"$v\"")
        }
    }


    static void printStringBuffer(String title, StringBuffer sb) {
        printStringBuffer(title, sb, "info")
    }

    static void printStringBuffer(String title, StringBuffer sb, String logLevel) {
        printTitle(title, logLevel)
        printStringBuffer(sb, logLevel)
    }

    static void printStringBuffer(StringBuffer sb, String logLevel) {
        String level = logLevel.toLowerCase()
        int i = 0
        sb.eachLine {
            i++
            if (level.equals("trace")) log.trace(i + ":\t" + it)
            else if (level.equals("debug")) log.debug(i + ":\t" + it)
            else log.info(i + ":\t" + it)
        }
    }


    static void printString(String title, String str) {
        printString(title, str, "info")
    }

    static void printString(String title, String str, String logLevel) {
        printTitle(title, logLevel)

        String level = logLevel.toLowerCase()
        if (level.equals("trace")) log.trace(str)
        else if (level.equals("debug")) log.debug(str)
        else log.info(str)
    }


    static void printTitle(String title) {
        printTitle(title, "info")
    }

    static void printTitle(String title, String logLevel) {

        String s = ""
        for (int i in 0..title.size() - 1) s += "*"
        s += "******"

        String level = logLevel.toLowerCase()
        if (level.equals("trace")) {
            log.trace(s)
            log.trace(title)
            log.trace(s)
        } else if (level.equals("debug")) {
            log.debug(s)
            log.debug(title)
            log.debug(s)
        } else {
            log.info(s)
            log.info(title)
            log.info(s)
        }
    }
}

