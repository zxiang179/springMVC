package ecnu.zx.service.impl;

import java.util.Map;

import ecnu.zx.annotation.Service;
import ecnu.zx.service.ECNUService;

@Service("ECNUServiceImpl")
public class ECNUServiceImpl implements ECNUService{

	@Override
	public int insert(Map map) {
		System.out.println("调用了ECNUServiceImpl的insert方法");
		return 0;
	}

	@Override
	public int delete(Map map) {
		System.out.println("调用了ECNUServiceImpl的delete方法");
		return 0;
	}

	@Override
	public int update(Map map) {
		System.out.println("调用了ECNUServiceImpl的update方法");
		return 0;
	}

	@Override
	public int select(Map map) {
		System.out.println("调用了ECNUServiceImpl的select方法");
		return 0;
	}
	

}
