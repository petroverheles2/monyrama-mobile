package com.monyrama.json;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;

import com.monyrama.model.Account;
import com.monyrama.model.Currency;
import com.monyrama.model.ExpensePlan;
import com.monyrama.model.ExpensePlanItem;
import com.monyrama.model.IncomeSource;
import com.monyrama.model.Server;

import junit.framework.TestCase;

public class ServerDataParserTest extends TestCase {
	
	public void testMe() throws IOException, JSONException {
		Server server = new Server();
		server.setServerid(2);
		final String PAYLOAD_FROM_SERVER = IOUtils.toString(ServerDataParserTest.class.getResourceAsStream("payload_from_server.json"));
		ServerData data = ServerDataParser.parse(server, PAYLOAD_FROM_SERVER);
		
		assertEquals(2, data.getCurrencies().size());
		Currency currency = data.getCurrencies().iterator().next();
		assertEquals(Long.valueOf(1389793492615L), currency.getCurrencyId());
		assertEquals(new BigDecimal("8.33"), currency.getExchangeRate());
		assertEquals("United States Dollar", currency.getName());
		assertEquals("USD", currency.getCode());
		assertEquals(Boolean.FALSE, currency.getMain());
		
		assertEquals(1, data.getExpensePlans().size());
		ExpensePlan expensePlan = data.getExpensePlans().iterator().next();
		assertEquals(1393427249667L, expensePlan.getExpenseplanid().longValue());
		assertEquals(new BigDecimal("100"), expensePlan.getRemainder());
		assertEquals(new BigDecimal("200"), expensePlan.getSum());
		assertEquals(1389825735540L, expensePlan.getCurrencyId().longValue());
		assertEquals("Test", expensePlan.getName());
		
		assertEquals(1, data.getExpensePlanItems().size());
		ExpensePlanItem expensePlanItem = data.getExpensePlanItems().iterator().next();
		assertEquals(1393427249668L, expensePlanItem.getItemid().longValue());
		assertEquals("General", expensePlanItem.getCategoryname());
		assertEquals(new BigDecimal("50.5"), expensePlanItem.getSum());
		assertEquals(new BigDecimal("10"), expensePlanItem.getRemainder());
		assertEquals("test", expensePlanItem.getName());
		assertEquals("com", expensePlanItem.getComment());
		assertEquals(1393427249667L, expensePlanItem.getExpensplanid().longValue());
		
		assertEquals(5, data.getAccounts().size());
		Account account = data.getAccounts().iterator().next();
		assertEquals(1391707664116L, account.getAccountId().longValue());
		assertEquals(1389793492615L, account.getCurrencyId().longValue());
		assertEquals("dollars", account.getName());
		assertEquals(new BigDecimal("4"), account.getSum());
		
		assertEquals(1, data.getIncomeSources().size());
		IncomeSource incomeSource = data.getIncomeSources().iterator().next();
		assertEquals(1408093772423L, incomeSource.getIncomeSourceId().longValue());
		assertEquals(1389825735540L, incomeSource.getCurrencyId().longValue());
		assertEquals("Work", incomeSource.getName());
		assertEquals(new BigDecimal("25"), incomeSource.getSum());		
	}
}
