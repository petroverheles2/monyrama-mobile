package com.monyrama.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.monyrama.model.Account;
import com.monyrama.model.Currency;
import com.monyrama.model.ExpensePlan;
import com.monyrama.model.Envelope;
import com.monyrama.model.IncomeSource;
import com.monyrama.model.Server;

public class ServerDataParser {
	public static ServerData parse(Server server, String payload) throws JSONException {
		JSONObject root = new JSONObject(payload);
		
		JSONArray currenciesJsonArray = root.getJSONArray("currencies");
		List<Currency> currencies = new ArrayList<Currency>();
		for(int i = 0; i < currenciesJsonArray.length(); i++) {
			currencies.add(new Currency(server, currenciesJsonArray.getJSONObject(i)));
		}
		
		JSONArray openPlansJsonArray = root.getJSONArray("expensePlans");
		List<ExpensePlan> expensePlans = new ArrayList<ExpensePlan>();
		List<Envelope> expensePlanItems = new ArrayList<Envelope>();
		for(int planIndex = 0; planIndex < openPlansJsonArray.length(); planIndex++) {
			JSONObject openPlanJson = (JSONObject)openPlansJsonArray.get(planIndex);
			ExpensePlan expensePlan = new ExpensePlan(server, openPlanJson);
			expensePlans.add(expensePlan);
			JSONArray itemsJsonArray = openPlanJson.getJSONArray("items");
			for(int itemIndex = 0; itemIndex < itemsJsonArray.length(); itemIndex++) {
				JSONObject itemJson = (JSONObject)itemsJsonArray.get(itemIndex);
				Envelope item = new Envelope(server, expensePlan, itemJson);
				expensePlanItems.add(item);
			}
		}
		
		JSONArray accountsJsonArray = root.getJSONArray("accounts");
		List<Account> accounts = new ArrayList<Account>();
		for(int i = 0; i < accountsJsonArray.length(); i++) {
			accounts.add(new Account(server, accountsJsonArray.getJSONObject(i)));
		}
		
		JSONArray incomeSourcesJsonArray = root.getJSONArray("incomeSources");
		List<IncomeSource> incomeSources = new ArrayList<IncomeSource>();
		for(int i = 0; i < incomeSourcesJsonArray.length(); i++) {
			incomeSources.add(new IncomeSource(server, incomeSourcesJsonArray.getJSONObject(i)));
		}		
		
		return new ServerData(currencies, expensePlans, expensePlanItems, accounts, incomeSources);
	}
}
