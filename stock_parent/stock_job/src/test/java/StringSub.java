import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSub {

    private String info = "var hq_str_sh601003 = \"柳钢股份,5.270,5.410,5.410,5.420,5.210,5.400,5.410,22717336,121162097.000,164800,5.400,139800,5.390,88800,5.380,45500,5.370,13600,5.360,18300,5.410,337500,5.420,118900,5.430,201100,5.440,300200,5.450,2025-10-13,15:00:04,00,\";\n" +
            "var hq_str_sh601002 = \"晋亿实业,5.260,5.350,5.290,5.320,5.110,5.290,5.300,11015253,57880624.000,67100,5.290,123900,5.280,22400,5.270,18300,5.260,21000,5.250,25100,5.300,97800,5.310,139600,5.320,143600,5.330,183400,5.340,2025-10-13,15:00:02,00,\";"
            ;
    private String tt = " <div class=\"w-full p-2 lg:p-3\">\n" +
            "                                                                    <div\n" +
            "                                                                        class=\"mb-1 h-10 text-sm font-bold cursor-pointer line-clamp-2 lg:text-base lg:h-12\">\n" +
            "                                                                        抖音 童锣烧 岛遇 NO.018期 【2P4V】</div>\n" +
            "                                                                    <div\n" +
            "                                                                        class=\"text-xs flex items-center justify-between lg:text-sm\">\n" +
            "                                                                        <span class=\"text-sub-text mr-2\"><span class=\"mr-1\"><i class=\"van-badge__wrapper van-icon van-icon-clock-o\" style=\"\"><!--[--><!----><!----><!--]--><!----></i></span><span>2025-10-16</span></span>\n" +
            "                                                                        <div class=\"hidden lg:block\">";
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

    @Test
    public void t2(){
        String rex = "   抖音 童锣烧 岛遇 NO.018期 【2P4V】</div>";
        rex = "(抖音.*)</div>";
        Pattern compile = Pattern.compile(rex);
        Matcher matcher = compile.matcher(tt);
        while (matcher.find()){
            System.out.println(matcher.group(1));

        }

    }
}


