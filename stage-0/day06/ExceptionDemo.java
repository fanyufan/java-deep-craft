import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Day 6：异常体系 —— checked vs unchecked、try-with-resources、自定义异常。
 *
 * 编译运行（Windows 中文环境需指定 UTF-8）：
 *   javac -encoding UTF-8 ExceptionDemo.java
 *   java ExceptionDemo
 */
public class ExceptionDemo {

    public static void main(String[] args) {
        checkedVsUnchecked();
        tryWithResources();
        customException();
        exceptionPractices();
    }

    // ========== 1. checked vs unchecked ==========
    static void checkedVsUnchecked() {
        // unchecked（RuntimeException 及其子类）：编译器不管，运行时才可能炸
        // 通常是"代码 bug"：NPE、数组越界、非法参数 —— 应该改代码，而不是到处 try-catch
        int[] nums = {1, 2, 3};
        try {
            System.out.println(nums[10]);       // ArrayIndexOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("unchecked: 越界了 -> " + e.getClass().getSimpleName());
        }

        // checked（Exception 的直接子类，如 IOException）：编译器强制你处理
        // 通常是"外部世界的不可靠"：文件不存在、网络断开 —— 不处理编译都过不了
        // 下面这行如果不在 try 里，javac 直接报错：unreported exception FileNotFoundException
        try (InputStream in = new FileInputStream("不存在的文件.txt")) {
            in.read();
        } catch (IOException e) {
            System.out.println("checked: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        }

        // 记忆法：checked 是"别人的错"（环境），unchecked 是"自己的错"（bug）
    }

    // ========== 2. try-with-resources：自动关资源 ==========
    static void tryWithResources() {
        // 老式写法：finally 里手动 close，啰嗦还容易忘
        // InputStream in = null;
        // try { in = new FileInputStream(...); } finally { if (in != null) in.close(); }

        // 新写法：实现 AutoCloseable 的资源放进 try(...) 括号，出作用域自动 close
        // 无论正常结束还是抛异常，close 一定会被调用
        try (NoisyResource r = new NoisyResource("数据库连接")) {
            r.doWork();
            throw new RuntimeException("模拟业务出错");
        } catch (RuntimeException e) {
            System.out.println("\ncatch 到: " + e.getMessage());
        }
        // 观察输出顺序：close 在 catch 之前执行
    }

    static class NoisyResource implements AutoCloseable {
        private final String name;

        NoisyResource(String name) {
            this.name = name;
            System.out.println("\n打开资源: " + name);
        }

        void doWork() {
            System.out.println("使用中: " + name);
        }

        @Override
        public void close() {
            System.out.println("自动关闭: " + name + "（在 catch 之前执行）");
        }
    }

    // ========== 3. 自定义异常 ==========
    // 规则：想让调用方必须处理 -> 继承 Exception（checked）
    //       表示程序 bug 或可自由选择处理 -> 继承 RuntimeException（unchecked）
    // 业务异常通常做成 unchecked，避免方法签名被 throws 污染
    static class InsufficientBalanceException extends RuntimeException {
        private final double balance;
        private final double amount;

        InsufficientBalanceException(double balance, double amount) {
            super("余额不足: 当前 " + balance + " 元，尝试取出 " + amount + " 元");
            this.balance = balance;
            this.amount = amount;
        }

        double getShortfall() {
            return amount - balance;
        }
    }

    static void withdraw(double balance, double amount) {
        if (amount > balance) {
            // 抛异常时带上上下文信息，排查问题时能省半小时
            throw new InsufficientBalanceException(balance, amount);
        }
        System.out.println("\n取款成功: " + amount);
    }

    static void customException() {
        withdraw(100, 50);
        try {
            withdraw(100, 200);
        } catch (InsufficientBalanceException e) {
            System.out.println(e.getMessage());
            System.out.println("还差: " + e.getShortfall() + " 元");
        }
    }

    // ========== 4. 实践要点 ==========
    static void exceptionPractices() {
        // ① 别吞异常：catch 了什么都不做，bug 会被活埋
        // try { ... } catch (Exception e) {}   // 最差写法

        // ② catch 从具体到宽泛，子类在前父类在后（反了编译报错）
        try {
            Integer.parseInt("abc");
        } catch (NumberFormatException e) {     // 具体
            System.out.println("\n具体异常: " + e.getMessage());
        } catch (RuntimeException e) {          // 宽泛兜底
            System.out.println("兜底: " + e.getMessage());
        }

        // ③ 包装异常时保留原始异常（cause），别丢失堆栈
        try {
            readConfig();
        } catch (ConfigLoadException e) {
            System.out.println("包装异常: " + e.getMessage());
            System.out.println("根因 cause: " + e.getCause().getClass().getSimpleName());
        }

        // ④ finally：无论是否异常都执行，适合做收尾（但现在优先 try-with-resources）
        try {
            System.out.println("try 块");
        } finally {
            System.out.println("finally 块（总会执行）");
        }
    }

    static class ConfigLoadException extends RuntimeException {
        ConfigLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static void readConfig() {
        try {
            Integer.parseInt("不是数字");
        } catch (NumberFormatException e) {
            // 第二个参数传入原始异常，堆栈链不断
            throw new ConfigLoadException("配置项 port 解析失败", e);
        }
    }
}
