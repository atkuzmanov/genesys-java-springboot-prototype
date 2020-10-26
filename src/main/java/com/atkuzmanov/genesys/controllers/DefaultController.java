package com.atkuzmanov.genesys.controllers;

import com.atkuzmanov.genesys.dao.TimestampEntity;
import com.atkuzmanov.genesys.dao.TimestampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.sql.Time;
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
    ResponseEntity<String> addTimestampToDB(@RequestParam String newTimestamp) {
        TimestampEntity tse = new TimestampEntity();
        if (newTimestamp.isBlank()) {
            newTimestamp = localDateTimeNowFormat_yyyyMMdddHHmmss();
        }
        tse.setTimestampAsString(newTimestamp);
        timestampRepo.save(tse);
        return new ResponseEntity<>("Saved.", HttpStatus.OK);
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    ResponseEntity<Iterable<TimestampEntity>> getAllTimestamps() {
        // This returns a JSON or XML with all the timestamps.
        return new ResponseEntity<Iterable<TimestampEntity>>(timestampRepo.findAll(), HttpStatus.OK);
    }

    @PutMapping(path = "/updateTimestamp")
    public @ResponseBody
    ResponseEntity<String> updateTimestamp(@RequestParam int timestampId) {
        if (timestampId <= 0) {
            throw new InvalidParameterException("Timestamp id must be a positive integer.");
        }
        Optional<TimestampEntity> optTse = timestampRepo.findById(timestampId);
        if (!optTse.isPresent()) {
            return new ResponseEntity<>("Not found.", HttpStatus.NOT_FOUND);
        }
        TimestampEntity tse = optTse.get();
        tse.setTimestampAsString(localDateTimeNowFormat_yyyyMMdddHHmmss());
        timestampRepo.save(tse);
        return new ResponseEntity<>("Updated.", HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete")
    public @ResponseBody
    ResponseEntity<String> deleteTimestamp(@RequestParam int timestampId) {
        if (timestampId <= 0) {
            throw new InvalidParameterException("Timestamp id must be a positive integer.");
        }
        Optional<TimestampEntity> optTse = timestampRepo.findById(timestampId);
        if (!optTse.isPresent()) {
            return new ResponseEntity<>("Not found.", HttpStatus.NOT_FOUND);
        }
        timestampRepo.deleteById(timestampId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private static String localDateTimeNowFormat_yyyyMMdddHHmmss() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(formatter);
    }
}
