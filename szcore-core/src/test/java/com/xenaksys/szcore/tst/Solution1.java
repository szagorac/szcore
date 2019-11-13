package com.xenaksys.szcore.tst;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Solution1 {

        public static void main(String []argh)
        {
            //Input
            Scanner sc= new Scanner(System.in);
            int n=sc.nextInt();
            String []s=new String[n+2];
            for(int i=0;i<n;i++)
            {
                s[i]=sc.next();
            }

            List<BigDecimal> bdl = new ArrayList<>();
            Map<BigDecimal, List<String>> mbd = new HashMap<>();
            try{
                for(String val : s){
                    BigDecimal bd = new BigDecimal(val);
                    List<String> lstr = mbd.get(bd);
                    if(lstr == null){
                        lstr = new ArrayList<>();
                        mbd.put(bd, lstr);
                    }
                    if(!bdl.contains(bd)) {
                        bdl.add(bd);
                    }
                    lstr.add(val);
                }
            }catch (Exception e){

            }

            Collections.sort(bdl, Collections.reverseOrder());

            int len = bdl.size();
            //Output
            for(int i=0;i<len;i++)
            {
                BigDecimal bd = bdl.get(i);
                List<String> vals = mbd.get(bd);
                for(String val : vals ) {
                    System.out.println(val);
                }
            }

        }

}
