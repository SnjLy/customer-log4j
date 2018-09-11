package com.helijia.framework.log;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @Package: com.helijia.framework.
 * @Description: customer log4j appender class for docker log appender
 * @function: Applications migration to docker,
 * customize log files, print logs according to ${filePath}/${date}/${podName}/${fileName} for easy log collection,
 * support user define log filePath、fileName in log4j.xml.
 * create log file in ${filePath}/${date}/${podName}/${fileName} and fileName ends with .log
 * @Author : LiuYong
 * Created by yehao on 2018/8/7.
 */
public class DockerLogAppender extends AppenderSkeleton {

    private static final String POD_NAME = "POD_NAME";
    private static final String PATTEN = "yyyy-MM-dd";
    private static final String FILE_PATH = "/home/www/wwwroot/logs";
    private static final String FILE = "sys.log";
    private static final String FILE_LAST = ".log";
    /**
     * docker log file path
     * */
    private static volatile String filePath;
    /**
     * docker fileName ends with .log
     */
    private static volatile String fileName;
    /**
     * Docker environment variable POD_NAME
     */
    private static volatile String podName;

    static {
        podName = System.getenv(POD_NAME);
    }
    public void setFilePath(String filePath) {
        DockerLogAppender.filePath = filePath;
    }

    public void setFileName(String fileName) {
        DockerLogAppender.fileName = fileName;
    }

    @Override
    protected void append(LoggingEvent event) {
        if (null == podName || "".equals(podName)) {
            //定义没有环境变量的时候podName给一个默认值
            podName=UUID.randomUUID().toString();
        }
        if (null == filePath || "".equals(filePath)){
            filePath = FILE_PATH;
        }
        //filePath/
        File filePathDir = new File(filePath);
        if (!filePathDir.exists()) {
            boolean mkdirs = filePathDir.mkdirs();
        }
        // filePath/yyyy-MM-dd/
        String nowFormat = DateFormatUtils.format(new Date(), PATTEN);
        File filePathDateDir = new File(filePathDir, nowFormat);
        if (!filePathDateDir.exists()) {
            boolean makdir = filePathDateDir.mkdirs();
        }
        File podFile = new File(filePathDateDir, podName);
        if (!podFile.exists()) {
            boolean makdir = podFile.mkdirs();
        }
        // filePath/yyyy-MM-dd/podName/sys.log
        if (null == fileName || "".equals(fileName)){
            fileName = FILE;
        }
        int i = fileName.lastIndexOf(FILE_LAST);
        if (i<0){
            fileName = fileName.concat(FILE_LAST);
        }
        String logFileName = fileName;
        File logFile = new File(podFile, logFileName);
        if (!logFile.exists()) {
            try {
                boolean newFile = logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // append file content
        try {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(logFile, true);
                fos.write(layout.format(event).getBytes("utf-8"));
                if (layout.ignoresThrowable()) {
                    String[] throwableInfo = event.getThrowableStrRep();
                    if (throwableInfo != null) {
                        for (String aThrowableInfo : throwableInfo) {
                            fos.write(aThrowableInfo.getBytes("utf-8"));
                            fos.write(Layout.LINE_SEP.getBytes("utf-8"));
                        }
                    }
                }
                fos.flush();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
