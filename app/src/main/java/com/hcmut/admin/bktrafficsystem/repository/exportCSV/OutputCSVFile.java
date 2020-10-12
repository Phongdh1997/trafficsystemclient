package com.hcmut.admin.bktrafficsystem.repository.exportCSV;

import com.hcmut.admin.bktrafficsystem.business.UserLocation;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutputCSVFile {
    public static final String RENDER_STATUS_SPEED_TEST_FILE_NAME = "render_status_speed_test.csv";
    private static final String BASE_DIR;

    static {
        BASE_DIR = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static void writeLocationList(List<String[]> datas, String exportFileName) {
        String filePath = BASE_DIR + File.separator + exportFileName;
        File f = new File(filePath);
        FileWriter mFileWriter = null;
        CSVWriter writer = null;

        try {
            if (f.exists() && !f.isDirectory()) {
                mFileWriter = new FileWriter(filePath, true);
                writer = new CSVWriter(mFileWriter);
            } else {
                writer = new CSVWriter(new FileWriter(filePath));
                String[] data = {"Longitude", "Latitude", "Speed", "Timestamp"};
                writer.writeNext(data);
            }
            for (String[] data : datas) {
                writer.writeNext(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (mFileWriter != null) {
                    mFileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
