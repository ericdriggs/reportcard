package com.ericdriggs.ragnarok.controller;

import java.util.List;

import com.ericdriggs.ragnarok.RagnarokService;
import com.ericdriggs.ragnarok.db.tables.pojos.Org;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrgController {

    private final RagnarokService ragnarokService;

    @Autowired
    public OrgController(RagnarokService ragnarokService) {
        this.ragnarokService = ragnarokService;
    }

    @GetMapping(value ="/orgs", produces = "application/json")
    @ResponseBody
    public List<Org> getOrgs() {
        return this.ragnarokService.getOrgs();
    }
}