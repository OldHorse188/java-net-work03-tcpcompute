import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ComputeServer {
    public static void main(String[] args) {

        try {
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
                boolean flag = true;
                String str = in.readUTF();

                if (!str.equals("")) {
                    // 判断字符合法性
                    char[] chars = str.toCharArray();
                    for (char chr:chars) {
                        if (!((chr >= '0' && chr <= '9') || chr == '\n' || chr == ',')) {
                            flag = false;
                            out.writeUTF("数据格式有误");
                            break;
                        }
                    }
                    if (flag) {
                        str = str.replace(" ", "");
                        str = str.replace("\n", "");
                        String[] num = str.split(",");

                        double sum = 0;
                        double ave = 0;
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
