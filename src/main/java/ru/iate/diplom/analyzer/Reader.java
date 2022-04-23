package ru.iate.diplom.analyzer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

public class Reader  {
    int current;
    byte[] input;
    Dictionary<Integer, String> dict;


    public boolean EOF(){
        return current==input.length;
    }
    public Reader(Path dataPath) throws IOException {
        input = Files.readAllBytes(dataPath);
        current = 0;
        InitDictionary();
    }

    public int ReadInt(int bytes){
        int sum = 0;
        bytes--;
        int pow = 0;
        for(int i = bytes; i >= 0; i--){

            sum += (0xFF & input[current + i]) * Math.pow(16, pow);
            pow += 2;
        }
        current+=bytes + 1;
        return sum;
    }

    public String ReadDouble(int bytes, int accuracy){
        double sum = 0;
        bytes--;
        int pow = 0;
        for(int i = bytes; i >= 0; i--){

            sum += (0xFF & input[current + i]) * Math.pow(16, pow);
            pow += 2;
        }
        current+=bytes + 1;
        if(sum == 128 || sum == 32768)
            return String.valueOf((int)sum);
        else
        return String.valueOf(sum/Math.pow(10,accuracy));

    }

    public String ReadSymbols(int bytes){
        if(current == 32576)
            System.out.println("hhe");

        String result = "";
        for (int i = 0; i < bytes; i++) {
            if(dict.get(0xff & input[current + i]) != null){
                result += dict.get(0xff & input[current + i]);
            }
            else{
                result += new String(Arrays.copyOfRange(input, current + i, current + i + 1), Charset.forName("IBM500"));
            }
        }
        current+=bytes;
        return result;
    }
    private void InitDictionary()
    {
        dict = new Hashtable<>();
        dict.put(0X76,"ю");
        dict.put(0X77,"а");
        dict.put(0X78,"б");
        dict.put(0X80,"ц");
        dict.put(0X8A,"д");
        dict.put(0X8B,"е");
        dict.put(0X8C,"ф");
        dict.put(0X8D,"г");
        dict.put(0X8E,"х");
        dict.put(0X8F,"и");
        dict.put(0X90,"й");
        dict.put(0X9A,"к");
        dict.put(0X9B,"л");
        dict.put(0X9C,"м");
        dict.put(0X9D,"н");
        dict.put(0X9E,"о");
        dict.put(0X9F,"п");
        dict.put(0XA0,"я");
        dict.put(0XAA,"р");
        dict.put(0XAB,"с");
        dict.put(0XAC,"т");
        dict.put(0XAD,"у");
        dict.put(0XAE,"ж");
        dict.put(0XAF,"в");
        dict.put(0XB0,"ь");
        dict.put(0XB1,"ы");
        dict.put(0XB2,"з");
        dict.put(0XB3,"ш");
        dict.put(0XB4,"э");
        dict.put(0XB5,"щ");
        dict.put(0XB6,"ч");
        dict.put(0XB7,"ъ");
        dict.put(0XB8,"Ю");
        dict.put(0XB9,"А");
        dict.put(0XBA,"Б");
        dict.put(0XBB,"Ц");
        dict.put(0XBC,"Д");
        dict.put(0XBD,"Е");
        dict.put(0XBE,"Ф");
        dict.put(0XBF,"Г");
        dict.put(0XCA,"Х");
        dict.put(0XCB,"И");
        dict.put(0XCC,"Й");
        dict.put(0XCD,"К");
        dict.put(0XCE,"Л");
        dict.put(0XCF,"М");
        dict.put(0XDA,"Н");
        dict.put(0XDB,"О");
        dict.put(0XDC,"П");
        dict.put(0XDD,"Я");
        dict.put(0XDE,"Р");
        dict.put(0XDF,"С");
        dict.put(0XEA,"Т");
        dict.put(0XEB,"У");
        dict.put(0XEC,"Ж");
        dict.put(0XED,"В");
        dict.put(0XEE,"Ь");
        dict.put(0XEF,"Ы");
        dict.put(0XFA,"З");
        dict.put(0XFB,"Ш");
        dict.put(0XFC,"Э");
        dict.put(0XFD,"Щ");
        dict.put(0XFE,"Ч");
        dict.put(0X00, "\u2400");
        dict.put(0X0F, "\u240f");

    }

    public void Skip(int num){
        current+=num;
    }


}
