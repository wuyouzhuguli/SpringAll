package net.evecom;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

import java.io.IOException;

public class JavaScp {
    public static void main(String[] args) throws IOException {
        Scp scp=new Scp();
        scp.connect();
    }
    private static class Scp {
        //数据服务器的ip地址
        private String dataServerIp = "172.16.40.156";
        //数据服务器的用户名
        private String dataServerUsername = "root";
        //数据服务器的密码
        private String dataServerPassword = "abc@123";

        public String getDataServerIp() {
            return dataServerIp;
        }

        public void setDataServerIp(String dataServerIp) {
            this.dataServerIp = dataServerIp;
        }

        public String getDataServerUsername() {
            return dataServerUsername;
        }

        public void setDataServerUsername(String dataServerUsername) {
            this.dataServerUsername = dataServerUsername;
        }

        public String getDataServerPassword() {
            return dataServerPassword;
        }

        public void setDataServerPassword(String dataServerPassword) {
            this.dataServerPassword = dataServerPassword;
        }

        public String getDataServerDestDir() {
            return dataServerDestDir;
        }

        public void setDataServerDestDir(String dataServerDestDir) {
            this.dataServerDestDir = dataServerDestDir;
        }

        //数据服务器的目的文件夹
        private String dataServerDestDir = "/home/neo4j-new/";
        //从远程到本地的保存路径
        //    private String localDir = "D:\\上传文件的临时目录";

        //文件scp到数据服务器
        Connection conn = new Connection(dataServerIp);
        private void connect() throws IOException {
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(dataServerUsername, dataServerPassword);
            if (isAuthenticated == false){
                throw new IOException("Authentication failed.文件scp到数据服务器时发生异常");
            }
            SCPClient client = new SCPClient(conn);


            client.put("D:\\11.xlsx", dataServerDestDir); //本地文件scp到远程目录
//            client.get(dataServerDestDir + "00审计.zip", localDir);//远程的文件scp到本地目录
            conn.close();
        }
        //创建时使用的模式0600 即rw
    }
}
