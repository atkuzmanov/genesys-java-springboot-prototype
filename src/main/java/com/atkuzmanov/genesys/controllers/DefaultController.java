package com.atkuzmanov.genesys.controllers;

import com.atkuzmanov.genesys.dao.TimestampEntity;
import com.atkuzmanov.genesys.dao.TimestampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
public class DefaultController {

    @Autowired
    private TimestampRepository timestampRepo;

    @RequestMapping("/")
    public String index() {
        return localDateTimeNowFormat_yyyyMMdddHHmmss();
    }

    @PostMapping(path = "/addTimestamp")
    public @ResponseBody
    String addTimestampToDB(@RequestParam String newTimestamp) {
        TimestampEntity tse = new TimestampEntity();
        if (newTimestamp.isBlank()) {
            newTimestamp = localDateTimeNowFormat_yyyyMMdddHHmmss();
        }
        tse.setTimestampAsString(newTimestamp);
        timestampRepo.save(tse);
        return "Saved.";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<TimestampEntity> getAllTimestamps() {
        // This returns a JSON or XML with all the timestamps.
        return timestampRepo.findAll();
    }

    @PutMapping(path="/updateTimestamp")
    public @ResponseBody
    String updateTimestamp(@RequestParam int timestampId) {
        if (timestampId <= 0) {
            throw new InvalidParameterException("Timestamp id must be a positive integer.");
        }
        Optional<TimestampEntity> optTse = timestampRepo.findById(timestampId);
        if (!optTse.isPresent()) {
            return "Not found.";
        }
        TimestampEntity tse = optTse.get();
        tse.setTimestampAsString(localDateTimeNowFormat_yyyyMMdddHHmmss());
        timestampRepo.save(tse);
        return "Updated.";
    }

    @DeleteMapping(path = "/delete")
    public @ResponseBody
    String deleteTimestamp(@RequestParam int timestampId) {
        if (timestampId <= 0) {
            throw new InvalidParameterException("Timestamp id must be a positive integer.");
        }
        timestampRepo.deleteById(timestampId);
        return "Deleted.";
    }

    private static String localDateTimeNowFormat_yyyyMMdddHHmmss() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(formatter);
    }
}
