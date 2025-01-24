import java.lang.Integer;
import java.lang.System;

public class PowersOfTwo {
    // Entry point.
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int i = -1, power = 1;
        while (++i <= n) {
            System.out.println(i + " " + power);
            power = power * 2;
        }
    }
}
