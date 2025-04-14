package main.java.com.vss;

public enum Operation {
    CHOOSE_FILE {
        @Override
        public void execute() {
            MainController.getInstance().chooseFile();  // direct pe instanță singleton
        }
    },
    UPLOAD_IMAGE {
        @Override
        public void execute() {
            SecretService.getInstance().processImage();  // procesare după ce imaginea e încărcată
        }
    };

    public abstract void execute();
}
