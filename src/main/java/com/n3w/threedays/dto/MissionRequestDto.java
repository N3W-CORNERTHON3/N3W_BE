package com.n3w.threedays.dto;

import com.n3w.threedays.entity.MissionEntity;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MissionRequestDto {
    private String name;
    private MissionEntity.Category category;
    private MissionEntity.Level level;
    private String memo;  // null 가능

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MissionEntity.Category getCategory() {
        return category;
    }

    public void setCategory(MissionEntity.Category category) {
        this.category = category;
    }

    public MissionEntity.Level getLevel() {
        return level;
    }

    public void setLevel(MissionEntity.Level level) {
        this.level = level;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
