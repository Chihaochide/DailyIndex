import com.ax.stock.util.BCrypt;
import com.ax.stock.util.RsaUtils;
import org.junit.jupiter.api.Test;

public class CreatePassWord {

    @Test
    public void createPWD(){
        String gensalt = BCrypt.gensalt();
        String root = BCrypt.hashpw("root", gensalt);
        System.out.println("root = " + root);

        boolean checkpw = BCrypt.checkpw("root", "$2a$10$MrGR0u939.DkAnPnWUgHneFkMFSR5deepRY/3thfeNEOCP3l6tdOW");
        System.out.println("checkpw = " + checkpw);

    }

}
