import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSub {

    private String info = "var hq_str_sh601003 = \"柳钢股份,5.270,5.410,5.410,5.420,5.210,5.400,5.410,22717336,121162097.000,164800,5.400,139800,5.390,88800,5.380,45500,5.370,13600,5.360,18300,5.410,337500,5.420,118900,5.430,201100,5.440,300200,5.450,2025-10-13,15:00:04,00,\";\n" +
            "var hq_str_sh601002 = \"晋亿实业,5.260,5.350,5.290,5.320,5.110,5.290,5.300,11015253,57880624.000,67100,5.290,123900,5.280,22400,5.270,18300,5.260,21000,5.250,25100,5.300,97800,5.310,139600,5.320,143600,5.330,183400,5.340,2025-10-13,15:00:02,00,\";"
            ;
    @Test
    public void testSubString(){
        // 匹配info
        String t = "var hq_str_(.+)=(.+);";
        Pattern compile = Pattern.compile(t);
        Matcher matcher = compile.matcher(info);
        while (matcher.find()){
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }


    }
}
