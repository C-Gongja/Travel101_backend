package com.sharavel.sharavel_be.trip_script.dto.request;

public class ScriptDayRequestDto {
	public String tripUid;
	public Integer dayNum;
	public String targetTripUid;

	public String getTripUid() {
		return tripUid;
	}

	public void setTripUid(String tripUid) {
		this.tripUid = tripUid;
	}

	public Integer getDayNum() {
		return dayNum;
	}

	public void setDayNum(Integer dayNum) {
		this.dayNum = dayNum;
	}

	public String getTargetTripUid() {
		return targetTripUid;
	}

	public void setTargetTripUid(String targetTripUid) {
		this.targetTripUid = targetTripUid;
	}
}
