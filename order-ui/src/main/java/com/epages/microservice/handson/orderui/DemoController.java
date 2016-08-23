package com.epages.microservice.handson.orderui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class DemoController {
	@Value("${catalog.api.backend.host}")
	private String catalogApiBackendHost;

    @Value("${order.api.backend.host}")
    private String orderApiBackendHost;
	
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("orderApiBackendHost", orderApiBackendHost);
        modelAndView.addObject("catalogApiBackendHost", catalogApiBackendHost);
        return modelAndView;
    }
    
    
}