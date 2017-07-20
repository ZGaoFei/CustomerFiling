package cn.com.shijizl.customerfiling.net.model;


import java.util.List;

public class ProjectScheduleResponse {

    private int code;
    private String message;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {

        private int scheduleCode;
        private String stepDesc;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        private boolean isChecked;
        private boolean isEnable;

        public boolean isEnable() {
            return isEnable;
        }

        public void setEnable(boolean enable) {
            isEnable = enable;
        }

        public int getScheduleCode() {
            return scheduleCode;
        }

        public void setScheduleCode(int scheduleCode) {
            this.scheduleCode = scheduleCode;
        }

        public String getStepDesc() {
            return stepDesc;
        }

        public void setStepDesc(String stepDesc) {
            this.stepDesc = stepDesc;
        }
    }
}
