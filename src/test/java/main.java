import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class main{

    public static void main(String args[]) throws IOException {
        String result = null;
        System.out.println(">Se va a lanzar ls");
        Process process = Runtime.getRuntime().exec("bash -c hp-scan --mode color --resolution 180");
        Runtime.getRuntime().exec("bash -c ls -la");
        try {
            //process.waitFor();
            System.out.println(process.toString());
            System.out.println(process.getOutputStream().toString());


            BufferedReader in =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                result += inputLine;
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(">Se ha lanzado ls");

    }
}

