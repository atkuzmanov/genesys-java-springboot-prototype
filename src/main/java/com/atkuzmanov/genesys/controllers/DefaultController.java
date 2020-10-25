package com.atkuzmanov.genesys.controllers;

import com.atkuzmanov.genesys.dao.TimestampEntity;
import com.atkuzmanov.genesys.dao.TimestampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class DefaultController {

    @Autowired
    private TimestampRepository timestampRepo;

    @RequestMapping("/")
    public String index() {
        return localDateTimeNowFormat_yyyyMMdddHHmmss();
    }

    @PostMapping(path="/addTimestamp")
    public @ResponseBody String addTimestampToDB(@RequestParam String newTimestamp){
        TimestampEntity tse = new TimestampEntity();
        if(newTimestamp.isBlank()) {
            newTimestamp = localDateTimeNowFormat_yyyyMMdddHHmmss();
        }
        tse.setTimestampAsString(newTimestamp);
        timestampRepo.save(tse);
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<TimestampEntity> getAllTimestamps() {
        // This returns a JSON or XML with all the timestamps.
        return timestampRepo.findAll();
    }

    private static String localDateTimeNowFormat_yyyyMMdddHHmmss() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(formatter);
    }
}
