package com.hcm.grw.model.mapper.hr;

import java.util.List;
import java.util.Map;

import com.hcm.grw.dto.hr.EmpSignDto;

public interface EmpSignDao {

	public List<EmpSignDto> selectAllSign(Map<String, Object> map);
	
	public int insertSign(Map<String, Object> map);
	
	public int setDefaultSign(Map<String, Object> map);
	
	public int deleteSign(Map<String, Object> map);
	
	public List<EmpSignDto> defaultChk(Map<String, Object> map);
	
	public int setUnDefaultSign(Map<String, Object> map);
	
	public int setAllDefaultSign(Map<String, Object> map);
	
	public int unSetDefaultSign(Map<String, Object> map);
}
