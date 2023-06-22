package com.m3u8.download.video.m3u8.uiEnum;

/**
 * @author Small_Tsk
 * @create 2023-06-20
 **/
public enum PopupMenuItemEnum {
    START("开始"),
    PAUSED("暂停"),
    PLAY("播放"),
    OPEN_FOLDER("打开所在文件夹"),
    PLAY_WHEN_FINISHED("文件完成时播放"),
    DELETE("删除"),
    COPY_LINK("复制链接"),
    MOVE_TO("移动文件至"),
    PERSONAL_DIRECTORY("个人目录"),
    OTHER_DIRECTORY("其他目录"),
    TOP("顶部"),
    FORCED_MERGE("强制合并"),
    BOTTOM("底部");

    private final String chineseName;

    PopupMenuItemEnum(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getChineseName() {
        return chineseName;
    }
}

