# CustomerLog
自定义log4j的appender,适用在docker环境下产生日志到指定目录，包含容器ID，自定义生成文件的权限

# pom.xml 依赖版本

```maven

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.5.11</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.5.11</version>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>4.3.18.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.3.2</version>
        </dependency>

        <!-- test -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>

```


Java web项目使用非管理员用户部署到linux weblogic服务器上时，生成的log日志文件读写权限为rw-r-----
导致其他用户如vlog，无法查看日志

解决方案：

1. log4j 2.9以上版本可以通过 [参考自点击打开链接](https://www.cnblogs.com/tootwo2/p/7679443.html)
```xml
<File name="File" fileName="my.log" filePermissions="rw-r--r--">
   <PatternLayout pattern="%m%n" />
</File>
```
中的filePermissions配置完成
2. log4j 2.9以下版本可以通过继承RollingFileAppender或者DailyRollingFileAppender来实现

# log4j.properties配置
```properties
#输出到文件 引用自定义的appender
log4j.appender.fileInfo = com.core.log4jconfig.Mylog4jWriter
log4j.appender.fileInfo.Threshold = DEBUG 
log4j.appender.fileInfo.layout = org.apache.log4j.PatternLayout 
log4j.appender.fileInfo.layout.ConversionPattern = %d{yyyy-MM-dd HH\:mm\:ss} %p %c %x - %m%n
log4j.appender.fileInfo.Append = TRUE 
log4j.appender.fileInfo.File = /data/my/logs/my.log 
log4j.appender.fileInfo.File='.'yyyy-MM-dd
```
# 重写setFile方法，设置生成文件的权限
```java 
com.core.log4jconfig.Mylog4jWriter.java代码

public class Mylog4jWriter extends DailyRollingFileAppender{
	
	@Override
	public synchronized void setFile(String fileName, boolean append,
			boolean bufferedIO, int bufferSize) throws IOException {
		super.setFile(fileName, append, bufferedIO, bufferSize);
		File f = new File(fileName);
		Set<PosixFilePermission> set = new HashSet<PosixFilePermission>();
		set.add(PosixFilePermission.OWNER_READ);
		set.add(PosixFilePermission.OTHERS_WRITE);
		set.add(PosixFilePermission.GROUP_READ);
		set.add(PosixFilePermission.OTHERS_READ);
		if(f.exists()){
			Files.setPosixFilePermissions(f.toPath(), set);
		}
	}
 
}
```
启动项目即可
生成的日志文件读写权限为rw-r--r--
