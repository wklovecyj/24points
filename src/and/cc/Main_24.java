package and.cc;

//24点程序
//实现以下功能
//1 随机生成4个数，并给出对应的扑克牌图片 2，计算我们给出的表达式的值 3，判断我们给出的表达式是否为24点 4，给出一个24点的解 5，刷新

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
public class Main_24 extends Application {
    //扑克牌图片Image类的集合
    private final List<Image>cardImages = new ArrayList<>(52);
    //加载扑克牌图片的方法
    public void loadCards() {
        for(int i = 1; i <= 52;i++) {
            String path = "/cards/" + i + ".png";
            Image img = new Image(getClass().getResourceAsStream(path));
            cardImages.add(img);
        }
    }
    //获取扑克牌图片的方法
    public List<Image> getCardImages() {
        return cardImages;
    }

    //生成存放四个随机数的数组
    int[] rNums = new int[4];
    //生成4个随机数的方法
    public void getFourRandomNums() {
        Random r = new Random();
        for(int i = 0;i < 4; i++) {
            rNums[i] = r.nextInt(52) + 1;
        }
    }
    //检查是否为24点的方法,这里我问了ai
    public boolean is24(String exp) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\d+|[-+*/]").matcher(exp);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        //检查表达式是否合法
        if(tokens.size() != 7) {
            return false;
        }
        for(int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (i % 2 == 0) {
                if (!token.matches("\\d+")) {
                    return false;
                }
                int num = Integer.parseInt(token);
                if (num < 1 || num > 13) {
                    return false;
                }
            } else {
                if (!token.matches("[-+*/]")) {
                    return false;
                }
            }
        }
        //提取数字和运算符
        List<Double> numbers = new ArrayList<>();
        List<Character> operators = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if(i % 2 == 0) {
                numbers.add(Double.parseDouble(tokens.get(i)));
            } else {
                operators.add(tokens.get(i).charAt(0));
            }
        }
        //处理乘除运算
        int i = 0;
        while (i < operators.size()) {
            char op = operators.get(i);
            if (op == '*' || op == '/') {
                double num1 = numbers.get(i);
                double num2 = numbers.get(i + 1);
                double result = calculate(num1, num2, op);
                if (Double.isNaN(result) || Double.isInfinite(result)) {
                    return false;
                }
                numbers.remove(i + 1);
                numbers.set(i, result);
                operators.remove(i);
                i = 0;
            } else {
                i++;
            }
        }
        //处理加减运算
        while (!operators.isEmpty()) {
            char op = operators.get(0);
            double num1 = numbers.get(0);
            double num2 = numbers.get(1);
            double result = calculate(num1, num2, op);
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                return false;
            }
            numbers.remove(1);
            numbers.set(0,result);
            operators.remove(0);
        }
        return Math.abs(numbers.get(0) - 24.0) < 1e-6;
    }
    //计算两个数的方法
    public static double calculate(double a, double b, char op) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if(b == 0) {
                    //看不懂
                    throw new IllegalArgumentException("除数不能为0");
                    //也可以写成 return Double.NaN;
                }
                return a / b;
            default :
                return Double.NaN;

        }
    }

    //重写
    @Override
    public void start(Stage st) {
        //第一步，加载扑克牌图片
        loadCards();
        //第二步，生成四个随机数
        getFourRandomNums();
        //第三步，创建图片视图并显示
        ImageView iv1 = new ImageView();
        ImageView iv2 = new ImageView();
        ImageView iv3 = new ImageView();
        ImageView iv4 = new ImageView();
        iv1.setFitHeight(200);
        iv1.setFitWidth(150);
        iv2.setFitHeight(200);
        iv2.setFitWidth(150);
        iv3.setFitHeight(200);
        iv3.setFitWidth(150);
        iv4.setFitHeight(200);
        iv4.setFitWidth(150);
        //把对应的图片放到对应的ImageView控件中
        iv1.setImage(cardImages.get(rNums[0] - 1));
        iv2.setImage(cardImages.get(rNums[1] - 1));
        iv3.setImage(cardImages.get(rNums[2] - 1));
        iv4.setImage(cardImages.get(rNums[3] - 1));
        //标签
        Label lb1 = new Label("Enter an expression:");
        //文本框
        TextField tf1 = new TextField();
        TextField tf2 = new TextField();
        tf2.setPromptText("请在此输入表达式");
        //弹窗
        Alert al = new Alert(AlertType.INFORMATION);
        al.setTitle("验证结果");
        //按钮
        Button findASolution = new Button("Find a solution");
        Button refresh = new Button("Refresh");
        Button verify = new Button("Verify");
        //按钮的事件
        verify.setOnAction(e -> {
           String exp = tf2.getText();
           if(is24(exp)) {
               al.setContentText(exp + "= 24 成立！");
           } else {
               al.setContentText(exp + "= 24 不成立！");
           }
           al.showAndWait();
        });
        refresh.setOnAction(e -> {
           getFourRandomNums();
           iv1.setImage(cardImages.get(rNums[0] - 1));
           iv2.setImage(cardImages.get(rNums[1] - 1));
           iv3.setImage(cardImages.get(rNums[2] - 1));
           iv4.setImage(cardImages.get(rNums[3] - 1));
        });
        //布局区
        //Vbox
        VBox vb1 = new VBox(30);
        HBox hb1 = new HBox(10);
        HBox hb2 = new HBox (10);
        HBox hb3 = new HBox(10);
        //设置对齐方式
        vb1.setAlignment(Pos.CENTER);
        hb1.setAlignment(Pos.CENTER);
        hb2.setAlignment(Pos.CENTER);
        hb3.setAlignment(Pos.CENTER);
        //添加控件
        hb1.getChildren().addAll(findASolution,tf1,refresh);
        hb2.getChildren().addAll(iv1,iv2,iv3,iv4);
        hb3.getChildren().addAll(lb1,tf2,verify);
        vb1.getChildren().addAll(hb1,hb2,hb3);
        //场景
        Scene sc = new Scene(vb1,800,600);
        //舞台
        st.setTitle("24点游戏");
        st.setScene(sc);
        st.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

//目前的问题是：
// 1，我不知道怎么生成24点的官方解
// 2，控件的布局不是很好看