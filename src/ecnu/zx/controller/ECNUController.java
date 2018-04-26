package ecnu.zx.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ecnu.zx.annotation.Controller;
import ecnu.zx.annotation.Qualifier;
import ecnu.zx.annotation.RequestMapping;
import ecnu.zx.service.ECNUService;

@Controller("ECNU")
public class ECNUController {

	@Qualifier("ECNUServiceImpl")
	private ECNUService service;

	/**
	 * 插入数据的方法
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 */
	@RequestMapping("insert")
	public String insert(HttpServletRequest request, 
			HttpServletResponse response
			,String param) {
		service.insert(null);
		return null;
	}
	
	/**
	 * 插入数据的方法
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 */
	@RequestMapping("delete")
	public String delete(HttpServletRequest request, 
			HttpServletResponse response
			,String param) {
		service.delete(null);
		return null;
	}
	
	/**
	 * 插入数据的方法
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 */
	@RequestMapping("update")
	public String update(HttpServletRequest request, 
			HttpServletResponse response
			,String param) {
		service.update(null);
		return null;
	}
	
	/**
	 * 插入数据的方法
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 */
	@RequestMapping("select")
	public String select(HttpServletRequest request, 
			HttpServletResponse response
			,String param) {
		service.select(null);
		return null;
	}

}
