import org.apache.spark.sql.SparkSession;
import java.math.BigDecimal;
public class PiCalculation {
    public static void main(String[] args) throws Exception {
        SparkSession spark = SparkSession
                .builder()
                .appName("PiCalculation")
                .getOrCreate();
        Pi pi = new Pi(new Integer(args[0]));
        BigDecimal result = pi.execute();
        System.out.println("Pi to the "+args[0]+"th decimal is:\n"+ result.toString());// + 4.0 * count / n);

        spark.stop();
    }
}