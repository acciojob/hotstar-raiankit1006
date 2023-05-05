package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();

        //now check which type of subs user wants
        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC)){
            int amount = 500 + (200 * subscriptionEntryDto.getNoOfScreensRequired());
            subscription.setSubscriptionType(SubscriptionType.BASIC);
            subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
            subscription.setTotalAmountPaid(amount);
        } else if (subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO)){
            int amount = 800 + (250 * subscriptionEntryDto.getNoOfScreensRequired());
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
            subscription.setTotalAmountPaid(amount);
        }else{
            int amount = 1000 + (350 * subscriptionEntryDto.getNoOfScreensRequired());
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
            subscription.setTotalAmountPaid(amount);
        }
        subscription.setStartSubscriptionDate(new Date());
        subscription.setUser(user);
        user.setSubscription(subscription);

        userRepository.save(user);

        return subscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();

        if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE))
            throw new Exception("Already the best Subscription");

        int currentSubsAmount = subscription.getTotalAmountPaid();

        int upgradedAmount;
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            upgradedAmount = 800 + (250 * subscription.getNoOfScreensSubscribed());
            subscription.setSubscriptionType(SubscriptionType.PRO);
        }else {
            upgradedAmount = 1000 + (350 * subscription.getNoOfScreensSubscribed());
            subscription.setSubscriptionType(SubscriptionType.ELITE);
        }
        subscription.setTotalAmountPaid(upgradedAmount);
        subscriptionRepository.save(subscription);

        return upgradedAmount - currentSubsAmount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        int revenue = 0;
        for(Subscription subscription : subscriptions){
            revenue += subscription.getTotalAmountPaid();
        }

        return revenue;
    }

}
