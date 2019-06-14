package com.rapipay.android.agent.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class EnglishNumberToWords {

    public static final String[] units = { "", "One", "Two", "Three", "Four",
            "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
            "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen",
            "Eighteen", "Nineteen" };

    public static final String[] tens = {
            "", 		// 0
            "",		// 1
            "Twenty", 	// 2
            "Thirty", 	// 3
            "Forty", 	// 4
            "Fifty", 	// 5
            "Sixty", 	// 6
            "Seventy",	// 7
            "Eighty", 	// 8
            "Ninety" 	// 9
    };

    public static String convert(final int n) {
        if (n < 0) {
            return "Minus " + convert(-n);
        }

        if (n < 20) {
            return units[n];
        }

        if (n < 100) {
            return tens[n / 10] + ((n % 10 != 0) ? " " : "") + units[n % 10];
        }

        if (n < 1000) {
            return units[n / 100] + " Hundred" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
        }

        if (n < 100000) {
            return convert(n / 1000) + " Thousand" + ((n % 10000 != 0) ? " " : "") + convert(n % 1000);
        }

        if (n < 10000000) {
            return convert(n / 100000) + " Lakh" + ((n % 100000 != 0) ? " " : "") + convert(n % 100000);
        }

        return convert(n / 10000000) + " Crore" + ((n % 10000000 != 0) ? " " : "") + convert(n % 10000000);
    }

    public static void main(final String[] args) {

        int n;

        n = 5;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 16;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 50;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 78;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 456;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 1000;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 99999;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 199099;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 10005000;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");
    }

//    private static final String[] specialNames = {
//            "",
//            " thousand",
//            " million",
//            " billion",
//            " trillion",
//            " quadrillion",
//            " quintillion"
//    };
//
//    private static final String[] tensNames = {
//            "",
//            " ten",
//            " twenty",
//            " thirty",
//            " forty",
//            " fifty",
//            " sixty",
//            " seventy",
//            " eighty",
//            " ninety"
//    };
//
//    private static final String[] numNames = {
//            "",
//            " one",
//            " two",
//            " three",
//            " four",
//            " five",
//            " six",
//            " seven",
//            " eight",
//            " nine",
//            " ten",
//            " eleven",
//            " twelve",
//            " thirteen",
//            " fourteen",
//            " fifteen",
//            " sixteen",
//            " seventeen",
//            " eighteen",
//            " nineteen"
//    };
//
//    private String convertLessThanOneThousand(int number) {
//        String current;
//
//        if (number % 100 < 20) {
//            current = numNames[number % 100];
//            number /= 100;
//        } else {
//            current = numNames[number % 10];
//            number /= 10;
//
//            current = tensNames[number % 10] + current;
//            number /= 10;
//        }
//        if (number == 0) return current;
//        return numNames[number] + " hundred" + current;
//    }
//
//    public String convert(int number) {
//
//        if (number == 0) {
//            return "zero";
//        }
//
//        String prefix = "";
//
//        if (number < 0) {
//            number = -number;
//            prefix = "negative";
//        }
//
//        String current = "";
//        int place = 0;
//
//        do {
//            int n = number % 1000;
//            if (n != 0) {
//                String s = convertLessThanOneThousand(n);
//                current = s + specialNames[place] + current;
//            }
//            place++;
//            number /= 1000;
//        } while (number > 0);
//
//        return (prefix + current).trim();
//    }
//
////    public static void main(String[] args) {
////        NumberToWord obj = new NumberToWord();
////        System.out.println("*** " + obj.convert(123456789));
////        System.out.println("*** " + obj.convert(-55));
//    }
}
