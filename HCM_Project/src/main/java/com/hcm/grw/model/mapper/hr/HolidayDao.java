package com.hcm.grw.model.mapper.hr;

import java.util.List;
import java.util.Map;

import com.hcm.grw.dto.hr.HolidayAdminDto;
import com.hcm.grw.dto.hr.HolidayDto;

public interface HolidayDao {

	/*휴가정보 조회(사용자) - 기간검색*/
	public List<HolidayDto> holidayList(Map<String, String> map);

	/*휴가정보 조회(사용자) - 정보검색*/
	public List<HolidayAdminDto> holidayAdminList(Map<String, Object> map);
	
	/*결재 일자 선택 시 정상적인 휴가일 수 가져오기*/
	public int selectHoliDayInfo(Map<String, String> map);
	
	/*사원 총휴가정보 가져오기*/
	public Map<String, Object> selectEmpTotalHoliDayInfo(String empl_id);
}
