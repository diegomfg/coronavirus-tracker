package io.matheus.coronavirustracker.services;

import io.matheus.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";

    private List<LocationStats> stats = new ArrayList<>();


    @PostConstruct
    @Scheduled(cron = "0 * * * * *") // Format (<sec> <min> <hour> <day of month> <month> <day of week> <year?>)
    public void fetchVirusData() throws IOException, InterruptedException  {

        List<LocationStats> newStats = new ArrayList<>();

        // The http client will make the request
        HttpClient client = HttpClient.newHttpClient();

        // creating the request builder
        HttpRequest request = HttpRequest.newBuilder()
                                        .uri(URI.create(VIRUS_DATA_URL))
                                        .build();


        // Creating the response of type string.
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // A String Reader that extends from Reader has to be passed to the parser.
        StringReader csvBodyReader = new StringReader(response.body());

        // Parsing the dataset, knowing that the first record (row) is the header.
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        // Creating the
        for (CSVRecord record : records) {

            LocationStats locationStat = new LocationStats();

            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            locationStat.setLatestTotalCases(Integer.parseInt(record.get(record.size() - 1)));


            newStats.add(locationStat);
        }

        this.stats = newStats;
    }

    public List<LocationStats> getStats() {
        return stats;
    }
}




