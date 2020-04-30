package org.pkucare.pojo.constant;

public enum ResultCode {

    SUCCESS(0, "success"),
    PARAM_ERROR(400, "参数不全或参数错误"),
    FILE_TYPE_UNSUITABLE(1001, "文件类型不对"),
    FILE_SIZE_UNSUITABLE(1002, "文件大小不对"),
    NO_IDCARD(1003, "身份证号错误或系统中不存在"),
    NOT_WITHIN_UPLOAD_TIME(1004, "不在上传时间范围内，请确认时间后再试！"),
    ERROR(9999, "未知的错误");

    public int code;
    public String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
