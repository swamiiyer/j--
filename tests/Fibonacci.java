import java.lang.Integer;
import java.lang.System;

public class Fibonacci {
    // Entry point.
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int a = -1;
        int b = 1;
        int count = 0;
        while (count <= n) {
            int t = a;
            a = b;
            b += t;
            count += 1;
            System.out.println(b);
        }
    }
}
