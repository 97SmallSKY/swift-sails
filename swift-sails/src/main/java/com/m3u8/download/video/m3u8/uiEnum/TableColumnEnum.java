package com.m3u8.download.video.m3u8.uiEnum;

/**
 * @author Small_Tsk
 * @create 2023-06-16
 **/
public enum TableColumnEnum {
    INDEX("序号", 0),
    NAME("名称", 1),
    LINK("链接", 2),
    PROGRESS("进度", 3),
    SPEED("速度", 4),
    STATUS("状态", 5),
    SIZE("大小", 6),
    ESTIMATED_COMPLETION("预计完成", 7),
    ELAPSED_TIME("耗时", 8);

    private final String columnName;
    private final int columnIndex;

    TableColumnEnum(String columnName, int columnIndex) {
        this.columnName = columnName;
        this.columnIndex = columnIndex;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

}

