package app.factories.implementations;

import app.factories.PageFactory;
import app.models.QPerson;
import app.pojos.pages.FilterRequest;
import app.pojos.pages.PageDataRequest;
import app.pojos.pages.PageDataResponse;
import com.querydsl.core.types.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PageFactoryImplTest {

    @Autowired
    private PageFactory pageFactory;

    /**
     * Should have OPERATION constants
     */
    @Test
    public void constants() {
        final String EQ = "EQ";
        final String NE = "NE";
        final String GT = "GT";
        final String GET = "GET";
        final String LT = "LT";
        final String LET = "LET";
        final String LIKE = "LIKE";
        final String STARTSWITH = "STARTSWITH";
        final String ENDSWITH = "ENDSWITH";
        final List<String> operationsAllowed = Arrays.asList(EQ, NE, GT, GET, LT, LET, LIKE, STARTSWITH, ENDSWITH);

        assertEquals(EQ, PageFactoryImpl.OPERATION.EQ);
        assertEquals(NE, PageFactoryImpl.OPERATION.NE);
        assertEquals(GT, PageFactoryImpl.OPERATION.GT);
        assertEquals(GET, PageFactoryImpl.OPERATION.GET);
        assertEquals(LT, PageFactoryImpl.OPERATION.LT);
        assertEquals(LET, PageFactoryImpl.OPERATION.LET);
        assertEquals(LIKE, PageFactoryImpl.OPERATION.LIKE);
        assertEquals(STARTSWITH, PageFactoryImpl.OPERATION.STARTSWITH);
        assertEquals(ENDSWITH, PageFactoryImpl.OPERATION.ENDSWITH);
        assertEquals(operationsAllowed, PageFactoryImpl.operationsAllowed);
    }

    /**
     * Should reset page and size to default value when null
     */
    @Test
    public void pageRequestPageSizeDefaultWhenNull() {
        final Integer DEFAULT_PAGE = 0;
        final Integer DEFAULT_SIZE = 1;
        final PageDataRequest pageDataRequest = new PageDataRequest();

        final PageDataRequest pageDataRequestExpected = new PageDataRequest(DEFAULT_PAGE, DEFAULT_SIZE, null, null, null);
        final PageRequest pageRequestExpected = new PageRequest(DEFAULT_PAGE, DEFAULT_SIZE, null);

        final PageRequest pageRequestResult = pageFactory.pageRequest(pageDataRequest);

        assertNotSame(pageDataRequestExpected, pageDataRequest);
        assertEquals(pageDataRequestExpected, pageDataRequest);

        assertNotSame(pageRequestExpected, pageRequestResult);
        assertEquals(pageRequestExpected, pageRequestResult);
    }

    /**
     * Should reset page and size to default value when minimum
     */
    @Test
    public void pageRequestPageSizeDefaultWhenMinimum() {
        final Integer DEFAULT_PAGE = 0;
        final Integer DEFAULT_SIZE = 1;
        final PageDataRequest pageDataRequest = new PageDataRequest(-1, 0, null, null, null);

        final PageDataRequest pageDataRequestExpected = new PageDataRequest(DEFAULT_PAGE, DEFAULT_SIZE, null, null, null);
        final PageRequest pageRequestExpected = new PageRequest(DEFAULT_PAGE, DEFAULT_SIZE, null);

        final PageRequest pageRequestResult = pageFactory.pageRequest(pageDataRequest);

        assertNotSame(pageDataRequestExpected, pageDataRequest);
        assertEquals(pageDataRequestExpected, pageDataRequest);

        assertNotSame(pageRequestExpected, pageRequestResult);
        assertEquals(pageRequestExpected, pageRequestResult);
    }

    /**
     * Should reset direction to null when sort is null
     */
    @Test
    public void pageRequestDirectionNullWhenSortNull() {
        final Integer PAGE = 2;
        final Integer SIZE = 5;
        final PageDataRequest pageDataRequest = new PageDataRequest(PAGE, SIZE, "direction", null, null);

        final PageDataRequest pageDataRequestExpected = new PageDataRequest(PAGE, SIZE, null, null, null);
        final PageRequest pageRequestExpected = new PageRequest(PAGE, SIZE, null);

        final PageRequest pageRequestResult = pageFactory.pageRequest(pageDataRequest);

        assertNotSame(pageDataRequestExpected, pageDataRequest);
        assertEquals(pageDataRequestExpected, pageDataRequest);

        assertNotSame(pageRequestExpected, pageRequestResult);
        assertEquals(pageRequestExpected, pageRequestResult);
    }

    /**
     * Should reset direction and sort to null when sort is empty
     */
    @Test
    public void pageRequestDirectionNullWhenSortEmpty() {
        final Integer PAGE = 2;
        final Integer SIZE = 5;
        final PageDataRequest pageDataRequest = new PageDataRequest(PAGE, SIZE, "direction", Collections.EMPTY_LIST, null);

        final PageDataRequest pageDataRequestExpected = new PageDataRequest(PAGE, SIZE, null, null, null);
        final PageRequest pageRequestExpected = new PageRequest(PAGE, SIZE, null);

        final PageRequest pageRequestResult = pageFactory.pageRequest(pageDataRequest);

        assertNotSame(pageDataRequestExpected, pageDataRequest);
        assertEquals(pageDataRequestExpected, pageDataRequest);

        assertNotSame(pageRequestExpected, pageRequestResult);
        assertEquals(pageRequestExpected, pageRequestResult);
    }

    /**
     * Should set direction value to ASC when invalid
     */
    @Test
    public void pageRequestDirectionDefaultWhenInvalid() {
        final Integer PAGE = 2;
        final Integer SIZE = 5;
        final List<String> SORT = Arrays.asList("sort1");
        final PageDataRequest pageDataRequest = new PageDataRequest(PAGE, SIZE, "direction", SORT, null);

        final PageDataRequest pageDataRequestExpected = new PageDataRequest(PAGE, SIZE, "ASC", SORT, null);
        final PageRequest pageRequestExpected = new PageRequest(PAGE, SIZE, new Sort(Sort.Direction.ASC, SORT));

        final PageRequest pageRequestResult = pageFactory.pageRequest(pageDataRequest);

        assertNotSame(pageDataRequestExpected, pageDataRequest);
        assertEquals(pageDataRequestExpected, pageDataRequest);

        assertNotSame(pageRequestExpected, pageRequestResult);
        assertEquals(pageRequestExpected, pageRequestResult);
    }

    /**
     * Should fix direction value when ASC is not perfect
     */
    @Test
    public void pageRequestDirectionFixASC() {
        final Integer PAGE = 2;
        final Integer SIZE = 5;
        final List<String> SORT = Arrays.asList("sort1");
        final PageDataRequest pageDataRequest = new PageDataRequest(PAGE, SIZE, "aSc", SORT, null);

        final PageDataRequest pageDataRequestExpected = new PageDataRequest(PAGE, SIZE, "ASC", SORT, null);
        final PageRequest pageRequestExpected = new PageRequest(PAGE, SIZE, new Sort(Sort.Direction.ASC, SORT));

        final PageRequest pageRequestResult = pageFactory.pageRequest(pageDataRequest);

        assertNotSame(pageDataRequestExpected, pageDataRequest);
        assertEquals(pageDataRequestExpected, pageDataRequest);

        assertNotSame(pageRequestExpected, pageRequestResult);
        assertEquals(pageRequestExpected, pageRequestResult);
    }

    /**
     * Should fix direction value when DESC is not perfect
     */
    @Test
    public void pageRequestDirectionFixDESC() {
        final Integer PAGE = 2;
        final Integer SIZE = 5;
        final List<String> SORT = Arrays.asList("sort1");
        final PageDataRequest pageDataRequest = new PageDataRequest(PAGE, SIZE, "dESc", SORT, null);

        final PageDataRequest pageDataRequestExpected = new PageDataRequest(PAGE, SIZE, "DESC", SORT, null);
        final PageRequest pageRequestExpected = new PageRequest(PAGE, SIZE, new Sort(Sort.Direction.DESC, SORT));

        final PageRequest pageRequestResult = pageFactory.pageRequest(pageDataRequest);

        assertNotSame(pageDataRequestExpected, pageDataRequest);
        assertEquals(pageDataRequestExpected, pageDataRequest);

        assertNotSame(pageRequestExpected, pageRequestResult);
        assertEquals(pageRequestExpected, pageRequestResult);
    }

    /**
     * Should return null when filtersRequest is null
     */
    @Test
    public void getSpecificationsNullWhenFiltersNull() {
        final Specifications specificationsResult = pageFactory.getSpecifications(null);

        assertNull(specificationsResult);
    }

    /**
     * Should return null when filtersRequest is empty
     */
    @Test
    public void getSpecificationsNullWhenFiltersEmpty() {
        final Specifications specificationsResult = pageFactory.getSpecifications(Collections.EMPTY_LIST);

        assertNull(specificationsResult);
    }

    /**
     * Should get Specifications when success
     */
    @Test
    public void getSpecifications() {
        final List<FilterRequest> filtersRequest = Arrays.asList(
                new FilterRequest("field1", "value1", null),
                new FilterRequest("field2", "2002-04-20T12:30:52Z", "abc"),
                new FilterRequest("field3", "2010-11-23", "nE")
        );

        final Specifications specifications = pageFactory.getSpecifications(filtersRequest);

        assertNotNull(specifications);
    }

    /**
     * Should return null when filtersRequest is null
     */
    @Test
    public void getPredicateNullWhenFiltersNull() {
        final Predicate predicate = pageFactory.getPredicate(null, null);

        assertNull(predicate);
    }

    /**
     * Should return null when filtersRequest is empty
     */
    @Test
    public void getPredicateNullWhenFiltersEmpty() {
        final Predicate predicate = pageFactory.getPredicate(Collections.EMPTY_LIST, null);

        assertNull(predicate);
    }

    /**
     * Should get Predicate when success
     */
    @Test
    public void getPredicate() {
        final List<FilterRequest> filtersRequest = Arrays.asList(
                new FilterRequest("name", "value1", null),
                new FilterRequest("createdAt", "2002-04-20T12:30:52Z", "abc"),
                new FilterRequest("birthday", "2010-11-23", "nE")
        );

        final Predicate predicate = pageFactory.getPredicate(filtersRequest, QPerson.person);

        assertNotNull(predicate);
    }

    /**
     * Should get PageDataResponse
     */
    @Test
    public void pageResponse() {
        final Integer TOTAL_PAGES = 15;
        final Long TOTAL_ELEMENTS = 500L;
        final PageDataRequest pageDataRequest = new PageDataRequest(1, 2, "A", Arrays.asList("S1", "S2"), Arrays.asList(new FilterRequest("A", "B", "C")));
        final Page page = mock(Page.class);
        given(page.getTotalPages()).willReturn(TOTAL_PAGES);
        given(page.getTotalElements()).willReturn(TOTAL_ELEMENTS);

        final PageDataResponse pageDataResponseExpected = new PageDataResponse(TOTAL_PAGES, TOTAL_ELEMENTS, pageDataRequest);

        final PageDataResponse pageDataResponseResult = pageFactory.pageResponse(page, pageDataRequest);

        assertNotSame(pageDataResponseExpected, pageDataResponseResult);
        assertEquals(pageDataResponseExpected, pageDataResponseResult);
        verify(page, times(1)).getTotalPages();
        verify(page, times(1)).getTotalElements();
    }
}