package com.prospection.coding.assignment.web;

import com.prospection.coding.assignment.domain.AnalysisResult;
import com.prospection.coding.assignment.service.BIAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("bi")
public class BIAnalysisController {

    private final BIAnalysisService biAnalysisService;

    @Autowired
    public BIAnalysisController(BIAnalysisService biAnalysisService) {
        this.biAnalysisService = biAnalysisService;
    }

    @RequestMapping("/analysis")
    @ResponseBody
    public AnalysisResult analysis() throws Exception {
        return biAnalysisService.performBIAnalysis();
    }
}
