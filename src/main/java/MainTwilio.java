import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.commons.cli.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class MainTwilio {

    public static void main(String[] args) throws URISyntaxException, ParseException {

        String path = System.getProperty("user.dir");
        String message = "";
        String account_sid = "";
        String auth_token = "";
        String serviceId = "";

        // create Options object
        Options options = new Options();
        options.addOption("f", true, "Filename(relative path) of phone numbers.");
        options.addOption("m", true, "Message(160 characters per sms) to be sent to the phone numbers.");
        options.addOption("a", true, "Find your Account Sid at twilio.com/console.");
        options.addOption("t", true, "Find your Auth Token at twilio.com/console.");
        options.addOption("s", true, "Set your Service Id at twilio.com/console.");
        options.addOption("h", false,
                "-f Filename(relative path) of phone numbers, one per line in E.164 number formatting.\n"+
                        "-m Message(160 characters per sms) to be sent to the phone numbers.\n"+
                        "-a Find your Account Sid at twilio.com/console.\n"+
                        "-t Find your Auth Token at twilio.com/console.\n"+
                        "-s Set your Service Id at twilio.com/console.\n"+
                        "\nVisit https://therandomtechadventure.blogspot.com/2017/08/mass-sms-broadcast-in-java-with-twilio.html for more information.\n"

        );

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        if(cmd.hasOption("f") && cmd.hasOption("m") && cmd.hasOption("a") && cmd.hasOption("t") && cmd.hasOption("s")) {
            path += cmd.getOptionValue("f");
            message = cmd.getOptionValue("m");
            account_sid = cmd.getOptionValue("a");
            auth_token = cmd.getOptionValue("t");
            serviceId = cmd.getOptionValue("s");
        }
        else {
            System.out.println(options.getOption("h").getDescription());
            System.exit(0);
        }

        File file = new File(path);
        Twilio.init(account_sid, auth_token);
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String phonenumber = sc.nextLine();
                sendMessage(phonenumber,message,serviceId);
            }
            sc.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    static void sendMessage(String to,  String body, String serviceId){
        try {
            Message message = Message
                    .creator(new PhoneNumber(to), serviceId, body)
                    .create();
            if(message.getErrorCode()==null){ //null when sending is successful
                System.out.println("Successfully sent sms to: "+to);
            }else{
                System.out.println("Failed to send to: "+to+", please check your Twilio account dashboard for more information. ErrorCode "+message.getErrorCode());
            }
        }catch (com.twilio.exception.ApiException exception){
            System.out.println("Failed to send to: "+to+", "+exception.getMessage());
        }

    }
}