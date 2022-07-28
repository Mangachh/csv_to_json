package cbs.csv_to_json.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cbs.csv_to_json.utils.CsvUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/cbs")
public class TransformController {

    /**
     * Gets a csv file and returns a proper json with the csv data.
     * The method assumes that the csv contains a header.
     * If no delimiter is supplied, the app will try to guess if it is a ";", "," or "\t"; 
     * 'cause those are the most used 
     * @param file      : the csv file to convert
     * @param delimiter : the items delimiter   
     * @return          : a json containing the csv data
     */
    @GetMapping(value = "/csv_to_json")
    public ResponseEntity<String> getMethodName(@RequestParam("file") MultipartFile file, @RequestParam(name = "delimiter", required = false) final String delimiter) {
        String json = "";

        if (CsvUtils.isCsv(file)) {
            json = CsvUtils.CsvToJson(file, delimiter);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(json);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Error", "The file is not a CSV").body(null);
        }       
        
    }

}
