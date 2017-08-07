package com.beta.fbinvite.invite;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangfuzhi on 17/7/5.
 */

public class FBFriendsModel implements Serializable {

    private int __ar;
    private String __sf;
    private BootloadableBean bootloadable;
    private IxDataBean ixData;
    private String lid;
    private List<PayloadBean> payload;

    public int get__ar() {
        return __ar;
    }

    public void set__ar(int __ar) {
        this.__ar = __ar;
    }

    public String get__sf() {
        return __sf;
    }

    public void set__sf(String __sf) {
        this.__sf = __sf;
    }

    public BootloadableBean getBootloadable() {
        return bootloadable;
    }

    public void setBootloadable(BootloadableBean bootloadable) {
        this.bootloadable = bootloadable;
    }

    public IxDataBean getIxData() {
        return ixData;
    }

    public void setIxData(IxDataBean ixData) {
        this.ixData = ixData;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public List<PayloadBean> getPayload() {
        return payload;
    }

    public void setPayload(List<PayloadBean> payload) {
        this.payload = payload;
    }

    public static class BootloadableBean implements Serializable {
    }

    public static class IxDataBean implements Serializable {
    }

    public static class PayloadBean implements Serializable {
        public boolean isSelect;
        private int status;
        private String path;
        private String photo;
        private String text;
        private long uid;
        private int bootstrap;
        private String display;
        private List<?> paths;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public int getBootstrap() {
            return bootstrap;
        }

        public void setBootstrap(int bootstrap) {
            this.bootstrap = bootstrap;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public List<?> getPaths() {
            return paths;
        }

        public void setPaths(List<?> paths) {
            this.paths = paths;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
