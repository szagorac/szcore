package com.xenaksys.szcore.tst;

import java.util.Scanner;

/**
 * Created by Slav on 10/05/2016.
 */
public class Solution {
    public static void main(String[] args) {
        Scanner sc= new Scanner(System.in);
        int n=sc.nextInt();
        int []ints=new int[n];
        for(int i=0;i<n;i++)
        {
            ints[i]=sc.nextInt();
        }

        int count = 0;

        for(int i=0;i<ints.length;i++){
            for(int j = i; j < ints.length; j++){
                int sum = calcSum(i, j, ints);
                if(sum < 0){
//                    System.out.println("Got negative for range " + i + ":" + j);
                    count++;
                }

            }
        }

        System.out.println(count);
    }

    private static int calcSum(int i, int j, int[] ints) {
        int sum = 0;
        for(int k=i; k<=j; k++) {
            sum += ints[k];
        }
        return sum;
    }
}
