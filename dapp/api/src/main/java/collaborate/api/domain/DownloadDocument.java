package collaborate.api.domain;

import java.io.File;

public class DownloadDocument {
    private String fileName;
    private File file;

    public DownloadDocument(String name, File file) {
        this.fileName = name;
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
