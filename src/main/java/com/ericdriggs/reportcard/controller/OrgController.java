package com.ericdriggs.reportcard.controller;

import java.util.List;

import com.ericdriggs.reportcard.ReportcardService;
import com.ericdriggs.reportcard.db.tables.pojos.Org;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrgController {

    private final ReportcardService reportCardService;

    @Autowired
    public OrgController(ReportcardService reportCardService) {
        this.reportCardService = reportCardService;
    }

    @GetMapping(value ="/orgs", produces = "application/json")
    @ResponseBody
    public List<Org> getOrgs() {
        return this.reportCardService.getOrgs();
    }
}