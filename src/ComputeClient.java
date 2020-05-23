import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

// 客户端
public class ComputeClient extends Application {

    TextArea t1;
    TextArea t2;

    Socket socket;

    DataOutputStream out;
    DataInputStream in;

    @Override
    public void start(Stage primaryStage) {

        // 第一个文本域（用于结果显示）
        t1 = new TextArea();
        t1.setEditable(false);

        // 第二个文本域（用于输入数据）
        t2 = new TextArea();
        t2.setPrefRowCount(3);
        t2.setPromptText("1.按行区分多个输入 2.空格、空行将被忽略");

        // 提交按钮
        Button bt = new Button("计算");

        // 使用 HBox 装按钮 并将按钮靠右对齐
        HBox hb = new HBox();
        hb.setAlignment(Pos.BASELINE_RIGHT);
        hb.getChildren().add(bt);

        // 最底部 装 所有部件 的 BorderPane 布局
        BorderPane root = new BorderPane();
        root.setTop(t1);
        root.setCenter(t2);
        root.setBottom(hb);

        // 设置Stage框
        primaryStage.setTitle("客户机");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.setResizable(false);
        primaryStage.show();

        // 启动 Socket 线程（另开线程的目的是防止图形界面卡死）
        new ComputeThread().start();

        // 提交按钮 的 鼠标点击事件
        bt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    // 将 输入内容 按行分割
                    String[] strs = t2.getText().split("\n");
                    for (String str:strs) {
                        // 将 输入内容 提交给 Server
                        out.writeUTF(str);

                        // 在第一个文本域显示 非空的输入内容 及 对应的结果
                        String result = in.readUTF();
                        if (!result.equals("")) {
                            t1.appendText("输入："+str+"\n");
                            t1.appendText("结果："+result+"\n\n");
                        }
                    }
                    // 提交内容 并 获取结果后，清除输入框
                    t2.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public static void main(String[] args) {
        launch(args);
    }


    class ComputeThread extends Thread {
        @Override
        public void run() {

            try {
                // 与服务端建立连接 并获取输入输出流
                socket = new Socket("127.0.0.1", 2020);
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
