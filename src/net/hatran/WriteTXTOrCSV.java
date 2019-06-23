package net.hatran;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//写文件
public class WriteTXTOrCSV {
    File file;
    Writer writer;

    // 构造函数
    public WriteTXTOrCSV(String writeFilePathName) {
        file = new File(writeFilePathName);
        try {
            // 如果writeFilePathName不存在，则会创建新的writeFilePathName文件
            file.createNewFile();
            // 覆盖写
            writer = new FileWriter(file, false);
            BufferedWriter bufferedwriter = new BufferedWriter(writer);
            // 将内容写入缓冲区
            bufferedwriter.write("");
            // 将缓存区内容压入文件
            bufferedwriter.flush();
            // 关闭文件
            bufferedwriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void WriteFileContents(String title, Map<String, PositionInSpace> SpatialDatabase, Map<String, Double> Constraint) {
        try {
            // 追加写
            writer = new FileWriter(file, true);
            BufferedWriter bufferedwriter = new BufferedWriter(writer);
            // 将内容写入缓冲区
            bufferedwriter.write(title + "\r\n");
            for(String key: SpatialDatabase.keySet())
            {
                bufferedwriter.write(key.substring(key.lastIndexOf('.')+1, key.length()) + "," + key.substring(0, key.lastIndexOf('.')) + "," + SpatialDatabase.get(key).getX() + "," + SpatialDatabase.get(key).getY() + "," + Constraint.get(key) + "\r\n");
            }
            // 将缓存区内容压入文件
            bufferedwriter.flush();
            // 关闭文件
            bufferedwriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

