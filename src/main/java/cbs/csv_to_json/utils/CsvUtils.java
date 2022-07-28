package cbs.csv_to_json.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvUtils {

    private static final String CSV_MIME = "text/csv";
    // TODO: maybe put this in a properties file
    private static final List<Character> DELIMITERS = Arrays.asList(';', ',', '\t');
    private static final String STR_FULL_FORMAT = "\"%s\"";
    private static final String STR_SHORT_FORMAT = "%s";
    private static final String OPEN_OBJ = "{";
    private static final String CLOSE_OBJ = "}";
    private static final String OPEN_LIST = "[";
    private static final String CLOSE_LIST = "]";
    
    /**
     * Check if the file is a csv
     * 
     * @param file
     * @return
     */
    public static boolean isCsv(final MultipartFile file) {
        return file.getContentType().equals(CSV_MIME);
    }

    /**
     * Transforms a CSV file to a Json format
     * 
     * @param file : the file to convert
     * @param definedDelimiter : delimiter from the csv. If none provided, the method will try to guess
     * @return : json formatted string
     */
    public static String CsvToJson(final MultipartFile file, final String definedDelimiter) {
        StringBuilder builder = new StringBuilder();
        final String delimiter;
        log.info("Openning file: ".concat(file.getName()));

        try (InputStream st = file.getInputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(st));
            log.info("Reading file");

            //the first line is the header
            String line = reader.readLine();

            if (definedDelimiter == null) {
                log.info("No delimiter supplied.");
                delimiter = getDelimiter(line);
            } else {
                delimiter = definedDelimiter;
            }

            final String[] headers = line.split(delimiter);
            builder.append(OPEN_LIST);

            reader.lines().forEach(l -> appendToBuilder(l, builder, headers, delimiter));

            // delete the last coma and close the list
            builder.deleteCharAt(builder.length() - 1);
            builder.append(CLOSE_LIST);
            log.info("File read & converted to Json");

        } catch (IOException e) {
            builder.setLength(0);
            builder.append(e.getMessage());
            log.error(e.getMessage());
        }

        return builder.toString();
    }

    /**
     * 
     * @param line
     * @param builder
     * @param headers
     * @param delimiter
     */
    private static void appendToBuilder(final String line, final StringBuilder builder, final String[] headers,
            final String delimiter) {
        line.trim();

        // check empty lines
        if (line.isEmpty()) {
            return;
        }

        String[] items = line.split(delimiter);

        if (items == null || items.length == 0) {
            return;
        }

        // write to builder(open item, transform, close)
        builder.append(OPEN_OBJ);
        buildJsonLine(builder, headers, items);
        builder.append(CLOSE_OBJ);
        builder.append(",");
    }

    private static String getDelimiter(final String line) {
        log.info("Getting delimiter");
        for (Character delimiter : DELIMITERS) {
            // some csv have ";" as an end of line, we check just in case to get the correct one
            if (line.contains(delimiter.toString()) && line.charAt(line.length() - 1) != delimiter.charValue()) {
                log.info("Delimiter found: \"".concat(delimiter.toString().concat("\"")));
                return delimiter.toString();
            }
        }

        return "";
    }

    /**
     * Create a jSon line with the headers an items suplied
     * @param builder : the builder to work with
     * @param headers : array of headers o value_names
     * @param items : array of items o values
     */
    private static void buildJsonLine(final StringBuilder builder, final String[] headers, final String[] items) {

        for (int i = 0; i < items.length; i++) {

            appendString(builder, headers[i].trim());
            builder.append(":");
            appendString(builder, items[i].trim());
            builder.append(",");
        }

        builder.deleteCharAt(builder.length() - 1);
    }


    private static void appendString(final StringBuilder builder, final String str) {

        if (str.length() == 0) {
            return;
        }

        if (DigitUtils.isInt(str) || str.charAt(0) == '\"') {
            builder.append(String.format(STR_SHORT_FORMAT, str));
        } else if (DigitUtils.isDecimal(str)) {
            // just in case the de
            String digit = DigitUtils.comaDecimalToPoint(str);
            builder.append(String.format((STR_SHORT_FORMAT), digit));
        } else {
            // por si el csv no tiene comillas
            builder.append(String.format(STR_FULL_FORMAT, str));
        }

    }
}
