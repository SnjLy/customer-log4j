
package com.helijia.framework.log;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.helpers.LogLog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

/**
 * @function: customer log file appender for docker, DailyRollingFile
 * DockerDailyRollingFileAppender
 *
 * If you want to use this DockerDailyRollingFileAppender with log4j, you must configure parameters filePath
 * and fileName, finally generator filePath/${podName}/fileName
 * @author yehao
 * @date 2018-08-09
 */
public class DockerDailyRollingFileAppender extends DailyRollingFileAppender {

    private static final String POD_NAME = "POD_NAME";
    private static final String POD_NAMESPACE = "POD_NAMESPACE";
    /**
     *  要求以.log为后缀
     */
    private static final String FILE_LAST = ".log";
    private static final String FILE_D = "/";
    private static final String FILE_P = "-";

    /**
     * 系统环境变量配置
     */
    private static String podName;
    private static String podNameSpace;

    static {
        podName = System.getenv(POD_NAME);
        if (null == podName || "".equals(podName)){
            LogLog.warn("DockerDailyRollingFileAppender no configuration environment variable POD_NAME");
        }
        podNameSpace = System.getenv(POD_NAMESPACE);
        if (null == podNameSpace){
            LogLog.warn("DockerDailyRollingFileAppender no configuration environment variable POD_NAMESPACE");
        }
    }
    /**
     * log4j config file path like /home/www/app-name
     * */
    private  String filePath;
    /**
     * log4j config file name such as sys.log
     */
    private  String fileName;

    /**
     * The default constructor does nothing.
     */
    public DockerDailyRollingFileAppender() {
    }

    private void changeFileName(){
        if (null == filePath || "".equals(filePath)){
            super.fileName = null;
            LogLog.error("DockerDailyRollingFileAppender filePath option not set for appender ["+name+"].");
            return ;
        }
        if (!filePath.endsWith(FILE_D)){
            filePath = filePath + FILE_D;
        }
        String path = filePath;
        if (null != podName && !"".equals(podName) && null != podNameSpace && !"".equals(podNameSpace)){
            path = filePath.concat(podNameSpace).concat(FILE_P).concat(podName).concat(FILE_D);
        }
        if (null == fileName || "".equals(fileName)) {
            LogLog.error("DockerDailyRollingFileAppender fileName option not set for appender [" + name + "].");
            super.fileName = null;
            return;
        }
        if (!fileName.endsWith(FILE_LAST)) {
            fileName = path.concat(fileName).concat(FILE_LAST);
        } else {
            fileName = path.concat(fileName);
        }
        super.fileName = fileName;
    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void activateOptions() {
        this.changeFileName();
        super.activateOptions();
    }


    /**
     * set file's read and write permission to 755
     * @throws IOException
     */
    @Override
    public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
        super.setFile(fileName, append, bufferedIO, bufferSize);
        File f = new File(fileName);
        Set<PosixFilePermission> set = new HashSet<PosixFilePermission>();
        //drwxr-xr-x
        set.add(PosixFilePermission.OWNER_READ);
        set.add(PosixFilePermission.OWNER_EXECUTE);
        set.add(PosixFilePermission.OWNER_WRITE);

        set.add(PosixFilePermission.OTHERS_READ);

        set.add(PosixFilePermission.GROUP_READ);
        set.add(PosixFilePermission.GROUP_WRITE);
        if(f.exists()){
            Files.setPosixFilePermissions(f.toPath(), set);
        }

    }
}
