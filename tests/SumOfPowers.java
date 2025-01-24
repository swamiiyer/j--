import java.lang.Integer;
import java.lang.System;

public class SumOfPowers {
    // Entry point.
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);
        int i = n, sum = 0;
        while (i > 0) {
            sum += pow(i--, k);
        }
        System.out.println(sum);
    }

    // Returns x^k, where x >= 0 and k >= 0.
    private static int pow(int x, int k) {
        int result = 1;
        int i = k;
        while (i > 0) {
            result = result * x;
            i--;
        }
        return result;
    }
}
