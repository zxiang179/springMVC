package ecnu.zx.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ecnu.zx.annotation.Controller;
import ecnu.zx.annotation.Qualifier;
import ecnu.zx.annotation.RequestMapping;
import ecnu.zx.annotation.Service;
import ecnu.zx.controller.ECNUController;

//@WebServlet("/DispatcherServlet")
public class DispatcherServlet extends HttpServlet {

	// 将全限定类名放入集合类
	private List<String> packageNames = new ArrayList<String>();
	// 注解属性对应各层对象实例Map
	private Map<String, Object> instanceMaps = new HashMap<String, Object>();
	// 请求url对应的method
	private Map<String, Method> handlerMaps = new HashMap<String, Method>();

	@Override
	public void init() throws ServletException {
		/**
		 * 扫描basePackage上的注解：ecnu.zx 扫描我们的基础包之后拿到我们的全限定名（包名称+类名称）
		 * /springMvc/src/zx/ecnu/controller/ECNUController.java 替换上面的斜杠，
		 * zx.ecnu.controller.ECNUController 将实例注入我们各层的bean变量
		 */
		// 1. 扫描全包
		scanBasePackage("ecnu.zx");
		// 2. 找到实例
		try {
			filterAndInstance();
			// 3. 注入 springIOC
			springIOC();
			// 4. 完成url到method的映射
			handlerMaps();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 通过url找到相应的method对象，进行处理
	private void handlerMaps() throws Exception {
		if (instanceMaps.size() <= 0) {
			return;
		}
		// 存的实例有控制层和Service层
		for (Map.Entry<String, Object> entry : instanceMaps.entrySet()) {
			if (entry.getValue().getClass().isAnnotationPresent(Controller.class)) {
				Controller controllerAnnotation = (Controller) entry.getValue().getClass()
						.getAnnotation(Controller.class);
				String baseUrl = controllerAnnotation.value();
				Method[] controllerMethods = entry.getValue().getClass().getMethods();
				for (Method controllerMethod : controllerMethods) {
					if (controllerMethod.isAnnotationPresent(RequestMapping.class)) {
						// 方法上包含@RequestMapping这个注解方法对象
						String methodUrl = ((RequestMapping) controllerMethod.getAnnotation(RequestMapping.class))
								.value();
						handlerMaps.put("/" + baseUrl + "/" + methodUrl, controllerMethod);
					} else {
						continue;
					}
				}
			}
		}
	}

	// 将实例注入到springIOC
	private void springIOC() throws Exception {
		if (instanceMaps.size() <= 0) {
			return;
		}
		for (Map.Entry<String, Object> entry : instanceMaps.entrySet()) {
			// 拿到instance的属性，判断是否有的注解Qualifier
			Field[] fields = entry.getValue().getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Qualifier.class)) {
					// 注入
					String qualifierValue = ((Qualifier) field.getAnnotation(Qualifier.class)).value();
					field.setAccessible(true);
					field.set(entry.getValue(), instanceMaps.get(qualifierValue));
				} else {
					continue;
				}
			}
		}
	}

	// 拦截方法请求然后在对应请求地址找到对应的handler实例
	private void filterAndInstance() throws Exception {
		// 上来判断集合中是否有实例
		if (packageNames.size() <= 0) {
			return;
		}
		for (String className : packageNames) {
			Class ccName = Class.forName(className.replace(".class", ""));
			// 判断是否有controller的注解
			if (ccName.isAnnotationPresent(ecnu.zx.annotation.Controller.class)) {
				Object instance = ccName.newInstance();
				// 将实例装入Map key-注解上的值
				Controller annotationInstance = (Controller) ccName.getAnnotation(Controller.class);
				// 通过注解对象拿到属性值 xml key:beanID value:class = com.d
				String keyString = annotationInstance.value();
				// 放入Map,通过注解的key值找到对应的bean的实例（类似于xml）
				instanceMaps.put(keyString, instance);
			} else if (ccName.isAnnotationPresent(ecnu.zx.annotation.Service.class)) {
				Object instance = ccName.newInstance();
				Service annotationInstance = (Service) ccName.getAnnotation(Service.class);
				String keyString = annotationInstance.value();
				instanceMaps.put(keyString, instance);
			} else {
				continue;
			}
		}
	}

	// 扫描全包的方法
	private void scanBasePackage(String basePackage) {
		URL url = this.getClass().getClassLoader().getResource("/" + replacePath(basePackage));
		// 拿到该路径下面的文件夹以及文件
		String pathFile = url.getFile();
		// 最终目的是将这个路径封装成一个File类
		File file = new File(pathFile);
		String[] files = file.list();
		for (String path : files) {
			// 再次构造成一个file类
			File eachFile = new File(pathFile + path);
			if (eachFile.isDirectory()) {
				scanBasePackage(basePackage + "." + eachFile.getName());
			} else if (eachFile.isFile()) {
				System.out.println("扫描类有：" + basePackage + "." + eachFile.getName());
				packageNames.add(basePackage + "." + eachFile.getName());
			}
		}
	}

	// 将全包名称替换成一个路径
	private String replacePath(String path) {
		return path.replaceAll("\\.", "/");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 拿到完整路径
		String uri = req.getRequestURI();
		String projectName = req.getContextPath();
		// path = baseUrl+methodUrl
		String path = uri.replace(projectName, "");
		// 方法对象
		Method method = handlerMaps.get(path);
		PrintWriter outPrintWriter = resp.getWriter();
		if (method == null) {
			outPrintWriter.write("404 not find");
			return;
		}
		// http://localhost:8080/springMvc/ECNU/delete
		String className = uri.split("/")[2];
		ECNUController dn = (ECNUController) instanceMaps.get(className);
		try {
			method.invoke(dn, new Object[] { req, resp, null });
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
