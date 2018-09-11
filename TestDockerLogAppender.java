package com.helijia.log;

import com.helijia.framework.log.DockerLogAppender;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Package: com.helijia.log
 * @Description: unit test
 * @function: unit test DockerLogAppender
 * @Author : LiuYong
 * Created by yehao on 2018/8/8.
 */
@RunWith(JUnit4.class)
public class TestDockerLogAppender {

   private static final Logger LOGGER = LoggerFactory.getLogger(TestDockerLogAppender.class);

   @Test
   public void test(){
       LOGGER.info("log info:" + this.getClass() + "--test()" );

       try {
           double s = 1/0;
       } catch (Exception e) {
          LOGGER.error("you should not do this like: 1/0 ", e);
       }

       LOGGER.debug("log debug:" + Thread.currentThread().getName() + " run log debug ");

   }
}
