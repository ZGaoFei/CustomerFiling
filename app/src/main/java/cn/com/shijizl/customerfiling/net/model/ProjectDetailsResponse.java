package cn.com.shijizl.customerfiling.net.model;

import java.util.List;

public class ProjectDetailsResponse {

    private int code;
    private Object message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
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
        private List<BudgetImgListBean> budgetImgList;
        private List<CadImgListBean> cadImgList;
        private List<StateImgListBean> stateImgList;

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

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public List<BudgetImgListBean> getBudgetImgList() {
            return budgetImgList;
        }

        public void setBudgetImgList(List<BudgetImgListBean> budgetImgList) {
            this.budgetImgList = budgetImgList;
        }

        public List<CadImgListBean> getCadImgList() {
            return cadImgList;
        }

        public void setCadImgList(List<CadImgListBean> cadImgList) {
            this.cadImgList = cadImgList;
        }

        public List<StateImgListBean> getStateImgList() {
            return stateImgList;
        }

        public void setStateImgList(List<StateImgListBean> stateImgList) {
            this.stateImgList = stateImgList;
        }

        public static class BudgetImgListBean {

            private int id;
            private int projectId;
            private String imgUrl;
            private int width;
            private int height;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getProjectId() {
                return projectId;
            }

            public void setProjectId(int projectId) {
                this.projectId = projectId;
            }

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

        public static class CadImgListBean {

            private int id;
            private int projectId;
            private String imgUrl;
            private int width;
            private int height;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getProjectId() {
                return projectId;
            }

            public void setProjectId(int projectId) {
                this.projectId = projectId;
            }

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

        public static class StateImgListBean {

            private int id;
            private int projectId;
            private String imgUrl;
            private int width;
            private int height;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getProjectId() {
                return projectId;
            }

            public void setProjectId(int projectId) {
                this.projectId = projectId;
            }

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
