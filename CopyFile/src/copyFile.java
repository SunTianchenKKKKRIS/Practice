import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class copyFile {

    public static void Copy(String fileAddress  , String pasteAddress){
        File file = new File(fileAddress);
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            fileOutputStream = new FileOutputStream(pasteAddress+"//复制版"+file.getName());
            int len = (int) file.length();
            byte[] data = new byte[1024*len];
            while (fileInputStream.read(data)!=-1){
                fileOutputStream.write(data,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fileInputStream!=null){
                try {
                    fileInputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(fileOutputStream!=null){
                try {
                    fileInputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }



}
