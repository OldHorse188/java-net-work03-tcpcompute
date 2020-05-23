import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 服务端
public class ComputeServer {
    public static void main(String[] args) {

        try {
            // 监听2020端口，并使用 newFixedThreadPool 线程池限制同时运行的线程
            ServerSocket ss = new ServerSocket(2020);
            ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
            while (true) {
                fixedThreadPool.execute(new SocketThread(ss.accept()));
            }
        }
        catch (IOException e) {
            System.out.println("启动失败");
        }

    }
}


class SocketThread extends Thread {

    private Socket socket;

    SocketThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                // flag 用于标记字符串的合法性
                boolean flag = true;
                // str 存储一条来自客户端提交的内容
                String str = in.readUTF();

                // 对 空字符串 返回 空结果
                if (!str.equals("")) {
                    // 判断字符合法性
                    char[] chars = str.toCharArray();
                    for (char chr:chars) {
                        // 限制字符种类
                        if (!((chr >= '0' && chr <= '9') || chr == '\n' || chr == ',')) {
                            flag = false;
                            out.writeUTF("数据格式有误");
                            break;
                        }
                    }
                    if (flag) {
                        // 去除 空格和多余的回车，并依据逗号分割获取数值
                        str = str.replace(" ", "");
                        str = str.replace("\n", "");
                        String[] num = str.split(",");

                        // 和
                        double sum = 0;
                        // 平均数
                        double ave = 0;
                        // 方差
                        double var = 0;

                        for (String sr:num) {
                            sum += Double.parseDouble(sr);
                        }
                        ave = sum / num.length;
                        for (String sr:num) {
                            var += Math.pow((Double.parseDouble(sr) - ave),2);
                        }
                        var = var / num.length;

                        out.writeUTF("和："+sum+", 平均数："+ave+", 方差："+var);
                    }
                }
                else {
                    out.writeUTF("");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
