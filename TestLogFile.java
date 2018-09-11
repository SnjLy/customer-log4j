package com.helijia.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Package: com.helijia.framework.util
 * @Description:
 * @function:
 * @Author : LiuYong
 * Created by yehao on 2018/8/17.
 */
public class TestLogFile {

    private static final Logger logger = LoggerFactory.getLogger(TestLogFile.class);

    public static void main(String[] args) {
        logger.info("1");
        logger.info("2");
        logger.info("3");

        logger.info("log info: main --test()" );

        try {
            double s = 1/0;
        } catch (Exception e) {
            logger.error("you should not do this like: 1/0 ", e);
        }

        logger.debug("log debug:" + Thread.currentThread().getName() + " run log debug ");
    }


}
