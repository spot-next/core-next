package io.spotnext.sample.integrationtests;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.spotnext.core.CoreInit;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.LambdaQuery;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.core.testing.IntegrationTest;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.sample.SampleInit;
import io.spotnext.sample.types.party.Party;

@IntegrationTest(initClass = SampleInit.class)
@SpringBootTest(classes = { SampleInit.class, CoreInit.class })
public class PartyPersistenceIT extends AbstractIntegrationTest {

	private static final String PARTY_TITLE = "test party";

	@Autowired
	QueryService queryService;

	@Override
	protected void prepareTest() {
		final User guest = modelService.create(User.class);
		guest.setShortName("test user");
		guest.setUid("guest-01@gmail.com");

		final Party party = modelService.create(Party.class);
		party.setTitle(PARTY_TITLE);
		party.setDate(LocalDateTime.of(2019, 1, 1, 18, 0));

		// collections are never null!
		party.getInvitedGuests().add(guest);

		// only the main items has to be saved explicitly, all dependencies
		// will be saved too
		modelService.save(party);
	}

	@Override
	protected void teardownTest() {
		// no need to remove any items, integration tests are run in a separate
		// database and additionally each test changes will be roll-backed!
	}

	@Test
	public void queryPartyByTitle() {
		final JpqlQuery<Party> query = new JpqlQuery<>("SELECT p FROM Party AS p WHERE p.title = :title", Party.class);
		query.addParam("title", PARTY_TITLE);

		final QueryResult<Party> result = queryService.query(query);

		Assert.assertEquals(1, result.getResults().size());
		Assert.assertEquals(PARTY_TITLE, result.getResults().get(0).getTitle());
	}

	@Test
	public void queryPartyByTypeSafeQuery() {
		final LambdaQuery<Party> query = new LambdaQuery<>(Party.class);
		query.filter(q -> PARTY_TITLE.equals(q.getTitle()));

		final QueryResult<Party> result = queryService.query(query);

		Assert.assertEquals(1, result.getResults().size());
		Assert.assertEquals(PARTY_TITLE, result.getResults().get(0).getTitle());
	}

	@Test
	public void queryPartyByGenericAttribute() {
		final ModelQuery<Party> query = new ModelQuery<>(Party.class, Collections.singletonMap("title", PARTY_TITLE));

		final Party result = modelService.get(query);

		Assert.assertEquals(PARTY_TITLE, result.getTitle());
	}

	@Test
	public void queryPartyByGenericAttributeToDTO() {
		// selected columns are resolved via constructor or reflection field
		// access!
		final JpqlQuery<PartyData> query = new JpqlQuery<>(
				"SELECT p.title AS partyTitle, p.date as partyDate FROM Party AS p WHERE p.title = :title",
				PartyData.class);
		query.addParam("title", PARTY_TITLE);

		final QueryResult<PartyData> result = queryService.query(query);

		Assert.assertEquals(1, result.getResults().size());
		Assert.assertEquals(PartyData.class, result.getResults().get(0).getClass());
		Assert.assertEquals(PARTY_TITLE, result.getResults().get(0).getPartyTitle());
	}

	public static class PartyData {
		private String partyTitle;
		private LocalDate partyDate;

		public String getPartyTitle() {
			return partyTitle;
		}

		public LocalDate getPartyDate() {
			return partyDate;
		}
	}
}
