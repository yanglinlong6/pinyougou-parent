package test;

import org.junit.Test;

/**
 * 项目名:pinyougou-parent
 * 包名: test
 * 作者: Yanglinlong
 * 日期: 2019/6/30 16:20
 */
public class jisuntest {
    public static void main(String[] args) {

        for (int i = 1; i < 10; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.print(j + "*" + i + "=" + i * j + "  ");
            }
            System.out.println();
        }
    }

    @Test
    public void test1() {
        int count = 9;
        for (int i = 1; i <= count; i++) {
            for(int j = 1;j<=count-i;j++) {
                System.out.print(" ");
            }
            for(int k = 1;k<=2*i-1;k++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }
}
