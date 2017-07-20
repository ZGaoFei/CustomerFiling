package cn.com.shijizl.customerfiling.net.model;


public class CodeResponse {

    public int code;
    public String message;
    public DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private int verifyCode;

        public int getVerifyCode() {
            return verifyCode;
        }

        public void setVerifyCode(int verifyCode) {
            this.verifyCode = verifyCode;
        }
    }
}
