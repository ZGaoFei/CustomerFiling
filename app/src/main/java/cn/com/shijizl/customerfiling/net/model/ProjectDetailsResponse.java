package cn.com.shijizl.customerfiling.net.model;

import java.util.List;


public class ProjectDetailsResponse {

    private int code;
    private String message;
    private DataBean data;

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

        private int id;
        private String title;
        private int customerId;
        private int status;
        private long startTime;
        private List<BudgetImgsBean> budgetImgs;

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        private List<CadImgsBean> cadImgs;
        private List<StateImgsBean> stateImgs;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getCustomerId() {
            return customerId;
        }

        public void setCustomerId(int customerId) {
            this.customerId = customerId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public List<BudgetImgsBean> getBudgetImgs() {
            return budgetImgs;
        }

        public void setBudgetImgs(List<BudgetImgsBean> budgetImgs) {
            this.budgetImgs = budgetImgs;
        }

        public List<CadImgsBean> getCadImgs() {
            return cadImgs;
        }

        public void setCadImgs(List<CadImgsBean> cadImgs) {
            this.cadImgs = cadImgs;
        }

        public List<StateImgsBean> getStateImgs() {
            return stateImgs;
        }

        public void setStateImgs(List<StateImgsBean> stateImgs) {
            this.stateImgs = stateImgs;
        }

        public static class BudgetImgsBean {

            private String imgUrl;
            private int width;
            private int height;

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }
        }

        public static class CadImgsBean {

            private String imgUrl;
            private int width;
            private int height;

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }
        }

        public static class StateImgsBean {

            private String imgUrl;
            private int width;
            private int height;

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }
        }
    }
}
