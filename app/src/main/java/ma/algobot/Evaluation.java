package ma.algobot;

import java.util.Vector;

public class Evaluation {
    public Vector<Qest> element = new Vector<>() ;
    public int score = 0;
    public void start(int type){
        element.clear();
        switch(type){
            case 0:
                element.add(new Qest("","","",""));
                element.add(new Qest("","","",""));
                element.add(new Qest("","","",""));
                break;
            case 1:
                element.add(new Qest("La valuer de A est:\nA ← 1\nB ← A\nA ← A+B","2","1","0"));
                element.add(new Qest("La valuer de B est:\nA ← 2\nB ← 2\nA ← A-B","2","0","1"));
                element.add(new Qest("La valuer de C est:\nA ← 1\nC ← A\nB ← C","1","2","0"));
                break;
            default:
                element.add(new Qest("","","",""));
                element.add(new Qest("","","",""));
                element.add(new Qest("","","",""));
        }
    }
}
