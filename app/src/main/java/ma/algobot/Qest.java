package ma.algobot;

import java.util.Arrays;

public class Qest {
    public static long nqest = 0;
    private String qest ;
    public String [] resp ;

    public Qest(String qest, String a,String b,String c){
        nqest++;
        this.qest = qest;
        this.resp = new String[3];
        this.resp[0] = a;
        this.resp[1] = b;
        this.resp[2] = c;
    }
    /*
    public Qest(String qest, String a,String b , String c ,String d){
        this(qest,a,b,c);
        this.resp[3] = d;
    }
    */
    public String getQest() {
        String elem = qest;
        for(int i=0 ; i < resp.length ; i ++) {
            elem += "\n"+(i+1) +" ) "+ resp[i];
        }
        return elem;

    }
    public String toString() {
        return "Qest { N* : " +nqest+ ' ' +
                ",Qestion : " + qest + ' ' +
                ",Repone :" + Arrays.toString(resp) + '}';
    }
}
