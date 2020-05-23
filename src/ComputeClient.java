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


public class ComputeClient extends Application {

    TextArea t1;
    TextArea t2;

    Socket socket;

    DataOutputStream out;
    DataInputStream in;

    @Override
    public void start(Stage primaryStage) {

        t1 = new TextArea();
        t1.setEditable(false);

        t2 = new TextArea();
        t2.setPrefRowCount(3);
        t2.setPromptText("1.按行区分多个输入 2.空格、空行将被忽略");

        Button bt = new Button("计算");

        HBox hb = new HBox();
        hb.setAlignment(Pos.BASELINE_RIGHT);
        hb.getChildren().add(bt);

        BorderPane root = new BorderPane();

        root.setTop(t1);
        root.setCenter(t2);
        root.setBottom(hb);

        primaryStage.setTitle("客户机");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.setResizable(false);
        primaryStage.show();

        new ComputeThread().start();

        bt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    String[] strs = t2.getText().split("\n");
                    for (String str:strs) {
                        out.writeUTF(str);

                        String result = in.readUTF();
                        if (!result.equals("")) {
                            t1.appendText("输入："+str+"\n");
                            t1.appendText("结果："+result+"\n\n");
                        }
                    }
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
                socket = new Socket("127.0.0.1", 2020);
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
