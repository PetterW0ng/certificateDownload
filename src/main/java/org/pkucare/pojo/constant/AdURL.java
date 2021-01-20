package org.pkucare.pojo.constant;

/**
 * Created by weiqin on 2019/12/26.
 */
public enum AdURL {

    AD_JUNIOR(1,"https://wx031c58989e81ab49.h5.xiaoe-tech.com/v1/course/column/p_5ddddd0e54a33_F7G3gYWR?type=3"),
    AD_INTERMEDIATE(2,"https://wx031c58989e81ab49.h5.xiaoe-tech.com/v1/course/column/p_5def6307d4f9b_TFMw50MV?type=3"),
    AD_SENIOR(3,"https://wx031c58989e81ab49.h5.xiaoe-tech.com/v1/course/column/p_5def632adf1ed_eCL0y64U?type=3"),
    AD_COUPON(4,"https://wx031c58989e81ab49.h5.xiaoe-tech.com/coupon/get/cou_5e16b179a754f-7WRAX0"),
    AD_TIT(5,"https://tit.pkucarenjk.com/"),
    AD_10(20, "http://www.pkucarenjk.com/autism/1887.html"),
    AD_20(30, "http://www.pkucarenjk.com/autism/1442.html"),
    AD_30(40, "http://www.pkucarenjk.com/autism/1821.html"),
    AD_40(10, "http://www.pkucarenjk.com/autism/1822.html"),
    AD_104(104, "https://appixgujdjj6027.h5.xiaoeknow.com/v1/course/column/p_5ea2b47f9f7f3_AJojc8jn?type=3"),
    AD_101(101, "https://appixgujdjj6027.h5.xiaoeknow.com/v1/course/column/p_5ef84e6ac5b04_zl7ToAc5?type=3"),
    AD_102(102, "https://appixgujdjj6027.h5.xiaoeknow.com/v1/course/column/p_5f3b5c5ae4b0118787319125?type=3"),
    AD_103(103, "https://appixgujdjj6027.h5.xiaoeknow.com/v1/course/column/p_5f0dec60e4b04349896bc6c4?type=3"),


    ;

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
