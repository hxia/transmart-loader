package com.recomdata.pipeline.util

import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator

/**
 * Created by haxia on 9/17/2015.
 */
class NumericUtil {

    private static final Logger log = Logger.getLogger(NumericUtil)


    static main(args) {

        PropertyConfigurator.configure("conf/log4j.properties")
        log.info("Current Root Path: " + System.getProperty("user.dir"))

        log.info NumericUtil.isNumeric("-12.3")
        log.info NumericUtil.isNumeric("-12.3a")

    }


    public static boolean isNumeric(String str)
    {
        //match a number with optional '-' and decimal.
        return str.matches("-?\\d+(\\.\\d+)?");
    }

}
