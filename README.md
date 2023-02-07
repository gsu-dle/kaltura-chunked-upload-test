# Kaltura parallel chunked upload test
Simple Java code that splits a big video file into smaller chunks and uploads it to the Kaltura server using the uploadToken API. The code creates 5 threads to benefit from concurrent uploading of chunks. To change the number of concurrent threads, set `threadCount` to the desired number.

This repo is meant as a proof of concept on how to utilize the `uploadToken.upload()` API to upload large files on the Kaltura server. Seeing how Flash has 2G limitation where it comes to uploading files and the uploaded file size allowed can also be limited in the PHP settings, big files should sometimes be split into smaller ones and uploaded in chunks.

## Contents
- `UploadSession.java` - a simple class which can be used to test chunked upload from the command line
- `chunkedupload/ParallelUpload.java` - a help class which uses `FileInputStream` to `read()` a certain amount of bytes from the original file [thus spliting it] and upload it to the Kaltura server.

## Compiling the sample code
The code relies on the Kaltura Java client. When using the latest version, that can be obtained from:

http://search.maven.org/#search|ga|1|kaltura

or from:

https://github.com/kaltura/KalturaGeneratedAPIClientsJava

When using older versions of the Kaltura server, one should run:
```
# php /opt/kaltura/app/generator/generate.php java
```

from the Kaltura server and then take the resulting package from `/opt/kaltura/web/content/clientlibs/java`
```bash
$ javac -cp /path/to/Kaltura/client/classes:/path/to/supporting/classes chunkedupload/ParallelUpload.java
$ javac -cp /path/to/Kaltura/client/classes:/path/to/supporting/classes UploadSession.java
```

## Testing from CLI:
```bash
$ java \
    -cp /path/to/Kaltura/client/classes:/path/to/supporting/classes \
    UploadSession \
    [service URL] \
    [partner ID] \
    [partner admin secret] \
    [/path/to/large/vid/file] \
    [file name] \ 
    [user] \
    [project location] \
    [web dir location] \
    [sessionid] \
    [tmpdir] \
    [upload_user_id] \
    [folder name] \
    [optional entryId to update]
```

You can also use the `run.sh` wrapper.

Note that to make bootstraping easier, the repository contains the following supporting JARs:
```
commons-codec-1.15.jar
gson-2.10.1.jar
json-20220924.jar
KalturaApiClient-19.1.0-SNAPSHOT.jar
kotlin-stdlib-1.8.10.jar
kotlin-stdlib-common-1.8.10.jar
kotlin-stdlib-jdk7-1.5.31.jar
kotlin-stdlib-jdk8-1.5.31.jar
log4j-api-2.19.0.jar
log4j-core-2.19.0.jar
okhttp-4.10.0.jar
okio-3.3.0.jar
okio-jvm-3.3.0.jar
```
You may want to update run.sh to use newer versions of these when available.

## Sample output
```
Created a new entry: 
[09 Oct 2017 12:10:17] INFO - "Uploading token  file size 109436200 in 11 chunks" - (ParallelUpload.java:207) 
[09 Oct 2017 12:10:17] INFO - "Thread-2: chunk 2 pos 20971520 size 10485760" - (ParallelUpload.java:127) 
[09 Oct 2017 12:10:17] INFO - "Thread-0: chunk 0 pos 0 size 10485760" - (ParallelUpload.java:127) 
[09 Oct 2017 12:10:17] INFO - "Thread-1: chunk 1 pos 10485760 size 10485760" - (ParallelUpload.java:127) 
[09 Oct 2017 12:10:17] INFO - "Thread-4: chunk 4 pos 41943040 size 10485760" - (ParallelUpload.java:127) 
[09 Oct 2017 12:10:17] INFO - "Thread-3: chunk 3 pos 31457280 size 10485760" - (ParallelUpload.java:127) 
[09 Oct 2017 12:10:24] INFO - "Thread-1: chunk 5 pos 52428800 size 10485760" - (ParallelUpload.java:127) 
[09 Oct 2017 12:10:24] INFO - "Thread-2: chunk 6 pos 62914560 size 10485760" - (ParallelUpload.java:127) 
[09 Oct 2017 12:10:26] INFO - "Thread-0: chunk 7 pos 73400320 size 10485760" - (ParallelUpload.java:127) 
[09 Oct 2017 12:10:27] INFO - "Thread-4: chunk 8 pos 83886080 size 10485760" - (ParallelUpload.java:127) 
[09 Oct 2017 12:10:27] INFO - "Thread-1: chunk 9 pos 94371840 size 10485760" - (ParallelUpload.java:127) 
[09 Oct 2017 12:10:28] INFO - "Thread-3: chunk 10 pos 104857600 size 4578600" - (ParallelUpload.java:127) 

Uploaded Video file to entry: 
```
