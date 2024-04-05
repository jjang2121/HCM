package com.hcm.grw.ctrl.hr;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hcm.grw.comm.CookiesMgr;
import com.hcm.grw.comm.Function;
import com.hcm.grw.dto.hr.CommuteDto;
import com.hcm.grw.dto.hr.EmployeeDto;
import com.hcm.grw.model.service.hr.CommuteService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/hr/commute/**")
public class CommuteController {
	
	@Autowired
	private CommuteService commuteService;
	
	
	@GetMapping("registCommute.do")
	public String registCommute(Authentication authentication, 
								Model model,
								HttpServletRequest req) {
		log.info("{} 출/퇴근 등록 페이지", Function.getMethodName());

		HttpSession session = req.getSession();
		EmployeeDto empDto = (EmployeeDto)session.getAttribute("userInfoVo");
		if(authentication == null || empDto == null) {
			Function.alertLocation("로그인 정보가 없습니다.("+Function.getMethodName()+")", "/login/login.do", "", "", "");
			return null;
		}
		
		String empl_id = authentication.getName();
		Date commuteInTime = null;
		Date commuteOutTime = null;
		CommuteDto cgDto = commuteService.selectCommuteDayInfo(empl_id);
		if(cgDto != null) {
			if(cgDto.getEmco_in_dt() != null) {
				commuteInTime = cgDto.getEmco_in_dt();
			}
			if(cgDto.getEmco_out_dt() != null) {
				commuteOutTime = cgDto.getEmco_out_dt();
			}
		}

		model.addAttribute("commuteInTime", commuteInTime);
		model.addAttribute("commuteOutTime", commuteOutTime);

		//ViewPage Device 정보 전달
		model.addAttribute("mobileFlag", StringUtils.defaultIfEmpty(String.valueOf(req.getSession().getAttribute("ckMobile")), ""));
		
		return "hr/commute/registCommute";
	}

	@GetMapping("registCommuteOk.do")
	public @ResponseBody void registCommuteOk(Authentication authentication, 
											  Model model) throws IOException {
		log.info("{} 출/퇴근 처리 페이지", Function.getMethodName());
		
		if(authentication == null) {
			Function.alertLocation("로그인 정보가 없습니다.("+Function.getMethodName()+")", "/login/login.do", "", "", "");
		}
		
		String empl_id = authentication.getName();
		String commuteInTime = "";
		CommuteDto cgDto = commuteService.selectCommuteDayInfo(empl_id);
		if(cgDto != null) {
			commuteInTime = cgDto.getEmco_in_dt().toString();
		}
		
		int cnt = 0;
		String commuteMsg = "";
		if(StringUtils.isEmpty(commuteInTime)) {
			CommuteDto cDto = new CommuteDto() {{
				setEmpl_id(empl_id);
			}};
			
			cnt += commuteService.registCommute(cDto);
			commuteMsg = "출근";
		}else {
			cnt += commuteService.updateCommute(empl_id);
			commuteMsg = "퇴근";
		}
		
		if(cnt > 0) {
			Function.alertLocation(commuteMsg + "처리가 완료 되었습니다.", "/hr/commute/registCommute.do", "", "", "");
		}else {
			Function.alertHistoryBack(commuteMsg + "처리 중 오류가 발생하였습니다.\n관리자에게 문의하세요.", "", "");
		}
		
	}

	
	@GetMapping("empCommuteList.do")
	public String empCommuteList(@RequestParam(required = false, name = "getYM") String getYM,
								Authentication authentication, 
								Model model) throws IOException {
		log.info("{} 출/퇴근 현황", Function.getMethodName());
		
		if(authentication == null) {
			Function.alertLocation("로그인 정보가 없습니다.("+Function.getMethodName()+")", "/login/login.do", "", "", "");
			return null;
		}
		
		String currentYearMonth = "";
		String preYearMonth = "";
		String nextYearMonth = "";
		System.out.println("현재 년월: " + currentYearMonth);		
		
		if(StringUtils.isEmpty(getYM)) {
			getYM = YearMonth.now().toString();
		}
		
		/* 현재월, 이전월, 다음월 구하기 시작 */
		currentYearMonth = getYM.replace("-", "");

		int year = Integer.parseInt(currentYearMonth.substring(0, 4));
        int month = Integer.parseInt(currentYearMonth.substring(4));		// YearMonth 객체 생성

		YearMonth cYearMonth = YearMonth.of(year, month);

        // 이전 월
        YearMonth previousMonth = cYearMonth.minusMonths(1);
        preYearMonth = previousMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
        //System.out.println("이전 월: " + preYearMonth);
        
        // 다음 월
        YearMonth nextMonth = cYearMonth.plusMonths(1);
        nextYearMonth = nextMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
        //System.out.println("다음 월: " + nextYearMonth);
		/* 현재월, 이전월, 다음월 구하기 종료 */
        
		Map<String, String> commuteMap = new HashMap<String, String>();
		commuteMap.put("empl_id", authentication.getName());
		commuteMap.put("searchdt", currentYearMonth);
		
	
		List<CommuteDto> lists = commuteService.commuteList(commuteMap);
		model.addAttribute("lists", lists);
		
		model.addAttribute("currentYearMonth", currentYearMonth);
		model.addAttribute("preYearMonth", preYearMonth);
		model.addAttribute("nextYearMonth", nextYearMonth);
		
		return "hr/commute/empCommuteList";
	}	
		
}
