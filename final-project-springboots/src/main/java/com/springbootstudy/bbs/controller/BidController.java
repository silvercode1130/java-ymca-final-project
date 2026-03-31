package com.springbootstudy.bbs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.springbootstudy.bbs.service.BidService;

@Controller
public class BidController {
	
	@Autowired
	private BidService bidService;
	
	
}
