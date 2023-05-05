package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        if(webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName()) != null)
            throw new Exception("Series is already present");

        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();

        WebSeries webSeries = new WebSeries();
        webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setProductionHouse(productionHouse);

        //now change the rating for this productionHouse
        productionHouse.getWebSeriesList().add(webSeries);
        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();
        double currentRating = 0;
        double totalWebSeries = productionHouse.getWebSeriesList().size();

        for(WebSeries w : webSeriesList){
            currentRating += w.getRating();
        }
        productionHouse.setRatings(currentRating/totalWebSeries);

        //    productionHouseRepository.save(productionHouse);
        WebSeries savedWebseries = webSeriesRepository.save(webSeries);
        return savedWebseries.getId();
    }

}

