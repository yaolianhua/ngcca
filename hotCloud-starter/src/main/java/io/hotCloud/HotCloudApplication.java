package io.hotCloud;

import io.hotCloud.core.common.RuntimeCommandLine;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@SpringBootApplication
public class HotCloudApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
//        SpringApplication.run(HotCloudApplication.class,args);

        List<String> run = RuntimeCommandLine.run(new String[]{"docker", "pull", "alpine"}, p -> {
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            InputStream inputStream = p.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            List<String> list = new ArrayList<>();
            while (true) {
                try {
                    if (!((line = bufferedReader.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                    list.add(line);
                System.out.println(line);
            }
            return list;
        });
        run.forEach(System.out::println);


    }
}
