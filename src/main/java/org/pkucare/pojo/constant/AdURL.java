package org.pkucare.pojo.constant;

/**
 * Created by weiqin on 2019/12/26.
 */
public enum AdURL {

    AD_JUNIOR(1,"https://wx031c58989e81ab49.h5.xiaoe-tech.com/v1/course/column/p_5ddddd0e54a33_F7G3gYWR?type=3"),
    AD_INTERMEDIATE(2,"https://wx031c58989e81ab49.h5.xiaoe-tech.com/v1/course/column/p_5def6307d4f9b_TFMw50MV?type=3"),
    AD_SENIOR(3,"https://wx031c58989e81ab49.h5.xiaoe-tech.com/v1/course/column/p_5def632adf1ed_eCL0y64U?type=3");

    int level;
    String url;

    AdURL(int level, String url){
        this.level = level;
        this.url = url;
    }

    public static String getUrl(int level) {
        for (AdURL ad : AdURL.values()){
            if (ad.level == level){
                return ad.url;
            }
        }
        return null;
    }

}
