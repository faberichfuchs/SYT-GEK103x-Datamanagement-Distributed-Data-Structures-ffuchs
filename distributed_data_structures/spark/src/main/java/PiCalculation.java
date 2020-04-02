import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;

public class PiCalculation {
    public static void main(String[] args) {

        SparkSession spark = SparkSession.builder().appName("Pi Calculation").getOrCreate();


        spark.stop();
    }
}