package com.sharavel.sharavel_be.trip_script.dto.request;

public class ScriptLocationRequestDto {
	public String tripUid;
	public Integer dayNum;
	public Integer locNum;
	public String targetTripUid;
	public Integer targetDayNum;

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

	public Integer getLocNum() {
		return locNum;
	}

	public void setLocNum(Integer locNum) {
		this.locNum = locNum;
	}

	public String getTargetTripUid() {
		return targetTripUid;
	}

	public void setTargetTripUid(String targetTripUid) {
		this.targetTripUid = targetTripUid;
	}

	public Integer getTargetDayNum() {
		return targetDayNum;
	}

	public void setTargetDayNum(Integer targetDayNum) {
		this.targetDayNum = targetDayNum;
	}

	@Override
	public String toString() {
		return "ScriptLocationRequestDto [tripUid=" + tripUid + ", dayNum=" + dayNum + ", locNum=" + locNum
				+ ", targetTripUid=" + targetTripUid + ", targetDayNum=" + targetDayNum + "]";
	}

}
