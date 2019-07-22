package com.pinyougou.fastdfs.test;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.fastdfs.test
 * 作者: Yanglinlong
 * 日期: 2019/6/25 11:13
 */
public class FastdfsTest {
    @Test
    public void uploadFastdfs() throws Exception{
        ClientGlobal.init("C:\\EnCoding\\pinyougouMajor\\pinyougou-parent\\pinyougou-upload-web\\src\\main\\resources\\config\\fastdfs_client.conf");
        TrackerClient trackerClient  = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getConnection();

        StorageClient storageClient = new StorageClient(trackerServer,null);
        String[] jpgs = storageClient.upload_file("C:\\Store\\yanglinlong.jpg", "jpg", null);
        for (String jpg : jpgs) {
            System.out.println(jpg);
        }
    }

}
