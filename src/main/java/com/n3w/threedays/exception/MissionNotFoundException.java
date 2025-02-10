package com.n3w.threedays.exception;

public class MissionNotFoundException extends RuntimeException {
    public MissionNotFoundException(Long missionId) {
        super("해당 미션을 찾을 수 없습니다. ID: " + missionId);
    }
}
